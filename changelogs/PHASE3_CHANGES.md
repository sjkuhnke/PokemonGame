# Phase 3 change list (eval, matrix, solver, sampling - NORMAL and HARD behind a flag)

Every change is tied to spec §7.4/§7.8-§7.14.2/§7.13, an acceptance test, or an explicit v1 note.

## New files (package `pokemon`)

| File | Purpose |
|---|---|
| `EvalWeights` | §7.8 style weights struct; only `BALANCED` exists (per-trainer profiles are Phase 6) |
| `Evaluator` | `eval`, `material`, `activeMatchup`, `bestRange`, `hazardPain`, `benchStatusValue`, `fieldValue`, `tempo`, `forcedTurnPenalty` (§7.8) |
| `MonWeights` | `compute(SimState)` -> `{ai, player}` weight arrays (§7.14.1 v1: `contribution` only) |
| `ActionGen` | `genAIActions`, `genPlayerActions` (§7.4); `switchRowsAllowed`, `deadTurn` (§7.13.1); `candidateBench`, `sackCandidates`, `playerSwitchColumns` (§7.14.2 v1) |
| `MatrixBuilder` | `buildMatrix` (§7.9) - Tier 2 only this phase |
| `Solver` | `solveZeroSum` via regret matching (§7.10) |
| `Shaper` | `shape`, `sample` (§7.12) |
| `AIV2` | `TrainerAI` implementation: forced pre-checks (§7.13) + the full `decide()` pipeline, `toMoveDecision`, UI reporting (§7.12) |
| `Phase3Tests` | T4, T6, T7, T8, T12, T13, T16, T17, T28, T29, T30, T31 - see "Tests" below, T7 is complete and standalone, the rest need `AITestHarness` filled in |

## Changed files

| File | Change | Ties to |
|---|---|---|
| `Action.java` | Added `public static final Action PASS = new Action((Move) null);` | `deadTurn`'s "AI: PASS, player: PASS" / "AI: MOVE m, player: PASS" probes (§7.13.1). No `ActionKind`/`BattleSimulator` change needed - a null-move `Action` already resolves to "does nothing" under `BattleSimulator.performMoves`'s existing `aMove != null` check. |
| `AIConfig.java` | Added `allowVoluntarySwitch`, `deadTurnForcesSwitch`, `sackRatio`, `maxPlayerSwitchCols`, `maxAISwitchRows`, `minProb`, `epsilon`, `style`, `useFullSim`; added `normal()`/`hard()` factories | §6 struct fields Phase 3 actually reads, per §0 rule 5 and Phase 2's own precedent comment on this file |
| `Pokemon.java` | Widen `scoreWeatherValue`, `scoreScreenValue`, `calcHazardTeamValue`, `hazardMoveForEffect` from `private` to package-private (drop the `private` keyword; no other change) | `Evaluator.fieldValue`/`hazardPain` need to call them from a different class in the same package. See "Pokemon.java diff" below - this is the one edit to that file this phase. |

### Pokemon.java diff

```diff
-	private double scoreWeatherValue(Effect weather, Pokemon foe) {
+	double scoreWeatherValue(Effect weather, Pokemon foe) {
@@
-	private double scoreScreenValue(Effect screen, Pokemon activeFoe, Pokemon likelySwitchIn) {
+	double scoreScreenValue(Effect screen, Pokemon activeFoe, Pokemon likelySwitchIn) {
@@
-	private double calcHazardTeamValue(Move hazardMove, Trainer team, Field field) {
+	double calcHazardTeamValue(Move hazardMove, Trainer team, Field field) {
@@
-	private Move hazardMoveForEffect(Effect effect) {
+	Move hazardMoveForEffect(Effect effect) {
```
Behavior is unchanged; only visibility. No other line in `Pokemon.java` is touched this phase - `legacyBestMove`, `scoreMove`, `scorePokemon`, `evaluateSwitchInScore`, `matchupScore`, `analyzeDefensiveResponse`, etc. all stay exactly as they are, reachable only through `LegacyAI` (deletion is Phase 4+).

## `matchupScore`: no change needed

The spec's Phase 3 task list says "adapt `matchupScore` inputs," but the existing signature -
`matchupScore(int myMaxMoveScore, double foeMaxDamagePercent, boolean iAmFaster)` - already takes
a **percent**, not a raw move-score int (its own javadoc: *"my best move's max-roll damage %"*,
and the body does `myMaxMoveScore / 100.0`). `Evaluator.activeMatchup` and
`MonWeights.baseEdge` both feed it `DamageRange.expectedCapped(hp) / hp * 100` - an *expected*
damage percent rather than legacy's *max-roll* percent, matching §7.8's `myR.frac`/`foeR.frac`
naming - but the formula, constants (`KILL_VALUE`, `THREAT_WEIGHT`, etc.) and signature are all
untouched, so no Pokemon.java edit was needed here at all.

## Known v1 simplifications (flagged in code, not silent)

| Item | Status |
|---|---|
| `hazardPain` multiplies `calcHazardTeamValue` by the hazard's layer count externally (the method itself doesn't scale by layers) | Documented in `Evaluator`'s class doc; revisit alongside Phase 4's §9 checklist pass or Phase 5's hazard-in-sacking work |
| `benchStatusValue` uses flat per-status points instead of reusing `scoreInflictedStatus` (needs a specific incoming-move context that doesn't generalize to "any bench mon vs an unknown opponent"); not yet Natural Cure aware | §9 checklist item, Phase 4 |
| `MonWeights`: `unique`/`utility`/`aceMultiplier` terms not implemented, `raw[m] = BASE_W + contribution` only | Explicitly Phase 5 per §7.14.1's own "Finalize computeMonWeights" task |
| `sackCandidates`: no `SACK_MIN_GAIN` guard | Explicitly Phase 5 per §7.14.2 |
| `MonWeights.baseline()` resets a clone's `status` to `null` (matching `SimState.fingerprint`'s null-check pattern) rather than `Status.HEALTHY` | Worth confirming against the engine's actual default-status representation; one-line fix either way if wrong |
| `MatrixBuilder` has no Tier 0/1 fast path or switch-in/damage-range caching - every cell runs full `simulateTurn` | Explicitly Phase 8 per §7.9's own tiering note ("implement AFTER correctness") |
| `predict()`/`finalStrategy()` (§7.11) are un-implemented: `AIV2` uses the raw equilibrium `x`/`y` with no player model or exploit blending | Explicitly Phase 6 |
| `aiReplacementHeuristic`/`chooseReplacement` unification (§7.14.3) not done - `simulateTurn` still uses Phase 2's `BattleSimulator.cheapReplacementSlot` placeholder | Explicitly Phase 5 per that method's own Phase 2 javadoc |
| `deadTurn`'s choice-lock skip mirrors `legacyBestMove`'s existing inline check exactly, but omits the spec's `isChoiceHolderNotYetLocked` refinement | No regression vs. existing behavior; refine when Choice-lock state tracking is revisited |
| `report()` (sim UI hooks) assumes `SimBattleUI.p1Moves`/`p2Moves`/`p1Switch`/`p2Switch` are public and typed `Pair<Pokemon,Double>`/`Pair<Pokemon,String>`, inferred from `legacyBestMove`'s own usage, not from `SimBattleUI.java`'s field declarations directly - flag if that's wrong | Should be a compile-time catch if incorrect |

## What still calls `legacyBestMove`

Nothing new. `TrainerAI.Config.defaultAI` still defaults to `LegacyAI.INSTANCE`; wire up `AIV2.INSTANCE`
(globally or per-`Trainer.ai`) wherever your difficulty selection / self-play harness decides which
engine to run, e.g.:

```java
TrainerAI.Config.defaultAI = AIV2.INSTANCE; // or set specific Trainer.ai fields
```

`legacyBestMove` itself is untouched and remains reachable via `LegacyAI` for A/B self-play.

## Tests

### T7 (solver sanity) - runs today, no setup needed

```java
Phase3Tests.t7_solverSanity();
```
Checks matching pennies (50/50), rock-paper-scissors (1/3 each), a strictly dominated row (weight
~0), and a dominant row (weight ~1) against `Solver.solveZeroSum` directly - no `Pokemon`/`Trainer`/
`Field` involved.

### T4, T6, T8, T12, T13, T16, T17, T28-T31 - need `AITestHarness` filled in first

`Phase3Tests.AITestHarness` is a shell: its dozen `Scenario` builder methods (`basic1v1`,
`poisonFightingVsGhostBackSteel`, `perishCountNWithHealthyBench`, etc.) each `throw new
UnsupportedOperationException("TODO")` and need real `Pokemon`/`Trainer`/`Field` construction
plugged in - specifically moveset assignment, `Pokemon.gp` static wiring for a standalone (non-UI)
battle, and how ability/item/status/`perishCount` get set on a mon before the test runs. I don't
have that construction code in what's been uploaded so far (it isn't `bestMove2`/`Trainer`/
`SimState`/`BattleSimulator` - it's whatever builds a `Pokemon` + a battle for the self-play
harness or the existing unit tests, if any exist). Once those builders are filled in:

```java
Phase3Tests.runAll(); // uncomment the calls in runAll() once AITestHarness compiles
```

Each test's assertions are already written against `Scenario.ai`/`Scenario.foe` and the small
`isPlainSwitch`/`isPivot`/`isSwitchOrPivot`/`matrixHadSwitchRows` helpers, so once the builders
return real mons/fields the tests themselves shouldn't need further changes.

## Manual verification steps

1. **Compile check**: drop the seven new files plus the two changed files into your source tree,
   apply the `Pokemon.java` visibility diff above, and confirm it compiles. The likeliest break
   points, in order of risk: `SimBattleUI.p1Moves`/`p1Switch` field visibility/type in `report()`
   (flagged above); `Ability.RECHARGE`/`CHARGING`/`LOCKED`/`TRAPPED` `Status` enum constants used
   in `Evaluator.tempo`/`forcedTurnPenalty` (taken from the spec's own §7.13 prose, not grepped
   directly against `Status.java`, which wasn't uploaded).
2. **Wire up `AIV2`**: point `TrainerAI.Config.defaultAI` (or a specific `Trainer.ai`) at
   `AIV2.INSTANCE` for a HARD-difficulty trainer in `SIM_BATTLE_STATE`, and step through a few
   turns in the debugger or via `Print.debug` output - confirm `p1Moves`/`p1Switch` populate and
   the chosen move/switch is legal (PP > 0, not Disabled, switch target alive).
3. **Determinism smoke test**: call `Rng.setSeed(1234L)`, run `decide()` once, reset the *same*
   starting position, `Rng.setSeed(1234L)` again, run `decide()` again - same action both times
   (this is `T4`, doable by hand even before `AITestHarness` exists).
4. **NORMAL vs HARD spot check**: put the same mon in the same bad matchup at NORMAL and HARD.
   NORMAL should never emit a plain `SWITCH` (`isPlainSwitch` in `Phase3Tests.AITestHarness`)
   unless `perishCount == 1`; HARD should switch when the position calls for it. This is `T16`/
   `T30` by hand.
5. **Self-play win rate**: once `AIV2.INSTANCE` is wired into the self-play harness from Phase 0,
   run the existing `SelfPlay.run(...)` with `AIV2` on one side and `LegacyAI` on the other at
   both NORMAL and HARD; confirm `AIV2`'s win rate is >= `LegacyAI`'s, per the Phase 3 acceptance
   criterion. Also eyeball average think time per decision - no hard budget yet (that's Phase 8),
   but a decision that takes multiple seconds signals `buildMatrix`'s uncached, uncapped cell
   count (up to ~12x9, each running a full `simulateTurn`) is worth an early look.

## Addendum (this session's fixes)

| Item | Change |
|---|---|
| `MonWeights.baseline()` | Confirmed: the engine's healthy/default status is `Status.HEALTHY`, not `null`. Fixed (and `Evaluator.statusPenalty` now treats both `null` and `HEALTHY` as "no penalty" defensively). |
| `AIV2` debug output | Added `AIV2.AI_DEBUG` (one line per action + its sampling probability + the pick, via `Print.debug` - auto-silenced by `SelfPlay`'s existing `Print.setDebugSuppressed` unless `cfg.aiTrace`) and `AIV2.AI_DEBUG_MATRIX` (full A x P payoff matrix, verbose, off by default). Both are `public static boolean`, flip them from anywhere. |
| `Phase3Tests.AITestHarness` | Rebuilt from scratch against the real `Trainer.trainers` pool via `SelfPlay.buildPool()` instead of hand-authored mons - see below. |

### Running the tests

Same as Phase 0-2, in-game (`Trainer.trainers` populated), e.g. from a debug key:

```java
Phase3Tests.runAll();
```

Each test now catches its own failure and prints `"<label> passed."` or `"<label> FAILED: ..."`
rather than throwing out of the batch, so one scenario the roster can't build doesn't hide the
rest of the results.

**T7** is pure matrix math and always runs. **T4, T12, T13, T16, T17, T28, T29, T30, T31** scan
`SelfPlay.buildPool()` for a matchup with the needed *property* (an immune matchup, a KO'ing
move, a healthy bench, an item where `isChoiceItem()` is true, ...) rather than naming specific
mons, so they should work against whatever roster you've got. **T6** and **T8** are genuinely
roster-dependent (T6 needs a mon with both a Poison and a Fighting move facing a Ghost-active/
Steel-backed trainer; T8 needs a mon with both a priority and non-priority move that each KO a
1 HP, slower foe) - if either throws `"no ... found in the pool"`, tell me the specific mon/team
you want used (or paste what's actually in your roster) and I'll hand-target the scenario instead
of scanning for it.

A few assumptions the harness makes that are worth flagging if something doesn't compile:
`Trainer.setCurrent(Pokemon)` is accessible from `pokemon` package code (seen called inside
`Trainer.simShell`, not independently confirmed public); `Item.isChoiceItem()` is enumerable via
`Item.values()`; `PType.GHOST`/`PType.STEEL`/`PType.FIGHTING` exist alongside the already-confirmed
`PType.POISON`/`FIRE`/`WATER`/`ROCK`; `Pokemon.fainted` and `Moveslot.currentPP` are settable from
another class in the same package (both used that way already in `Phase2Tests`/`SimState`, just
not previously *written* from outside `Pokemon.java` itself in what I've seen).

### Wiring into SelfPlay

No `SelfPlay.java` changes needed - `Config.aiA`/`aiB` already take any `TrainerAI`. To run `AIV2`
against `LegacyAI` at HARD:

```java
SelfPlay.Config c = new SelfPlay.Config();
c.battles = 100;
c.aiA = AIV2.INSTANCE;
c.aiB = LegacyAI.INSTANCE;
c.difficultyA = Player.HARD;
c.difficultyB = Player.HARD;
c.teams = SelfPlay.randomCompetitive(3); // or SelfPlay.storyTrainers()
SelfPlayReport rep = SelfPlay.run(c);
```

Swap `c.difficultyA = Player.NORMAL` (and drop `c.swapSeats` concerns - it already alternates
seats to cancel team strength) to get the NORMAL-vs-NORMAL comparison the Phase 3 acceptance
criterion asks for. Leave `c.aiTrace = false` for the batch run (default) - flip `AIV2.AI_DEBUG`
off too, or the string-building for every decision across every battle will slow things down even
with `Print` output suppressed, since suppression happens after the string is built, not before.
Actually - to avoid paying that cost during a batch run, gate it explicitly:

```java
AIV2.AI_DEBUG = cfg.aiTrace; // only build the debug strings when SelfPlay would actually show them
```

## Not in Phase 3 (deliberately)


`unique`/`utility`/`aceMultiplier` in `computeMonWeights`; `SACK_MIN_GAIN`; the real
`chooseReplacement` unification; Tier 0/1 pruning and caching in `buildMatrix`; the player model,
`infoMode`, `alpha`/`temperature` exploit blending; trainer style profiles beyond `BALANCED`; lead
selection (`EXTREME`'s `selectLead`, `chooseLead`). All per their explicit phase numbers above.
