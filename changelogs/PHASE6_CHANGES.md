# Phase 6 — Player model (§7.11) and difficulty gates (T27)

Status: implemented, **not compiled against your project** (see "What was and wasn't verified").
Scope kept to Phase 6. Nothing from Phase 7 (lead selection) or 8 (tuning, caching) is in here.

> **Revision 2 — "best" now comes from the matrix.** The first cut classified the player's switch as "best back" with
> a type-chart proxy (`cheapMatchup`). A real run showed it disagreeing with the simulation (the proxy's pick got 2% of
> the equilibrium's mass, the simulated best got 98%). Now the player's best stay and best switch are the columns with
> the lowest AI payoff against the AI's equilibrium mix, and the "has a good back" bucket flag became "the simulation says
> switching is the better reply". Classes renamed `STAY_BEST / STAY_OTHER / SWITCH_BEST / SWITCH_OTHER`. `ActionGen` is
> no longer touched. Anything saved by revision 1 should be reset (`player.playerModel().reset()`): its counts mean
> something different.

> **Revision 3 — first real-battle log.** Three findings from a champion fight:
> 1. *SWITCH_BEST never fired.* Recomputing the best switch from the logged matrices: on three switches the top two
>    columns were 0.8, 0.2 and 4.4 eval points apart, and a single winner took "best" by a hair. "Best" is now a **band**:
>    every column within `PlayerReader.BEST_TOL` (5 eval points) of the lowest counts. Separately, on two other switches
>    the AI's active mon was the same on consecutive turns and the player's switch columns were identical, so the mon the
>    player switched to had **no column** (`ActionGen` keeps only the top 3 by type chart + one sack column). The
>    observation log line now says which case it was.
> 2. *Protect was recorded as STAY_OTHER.* Protect-family moves (`PROTECT, DETECT, LAVA_LAIR, OBSTRUCT, SPIKY_SHIELD,
>    AQUA_VEIL`, the group in `Pokemon.move`) are now their own class, `STAY_PROTECT`, and "a Protect move is available"
>    is a bit in the situation, so the rate is measured over turns the player could have protected. Whether the player
>    was threatened is already in the bucket, which is how a scouting Protect (not threatened) is told apart from a
>    defensive one (threatened) without the AI having to guess intent.
> 3. *"Turn 0 observed STAY_OTHER".* Not a bug: the line prints at the start of the next decision and reports the click
>    from the turn before. It was your Protect.
>
> Layout change: 5 classes, 48 fine buckets, ~1.1 KB serialized. A save written by revision 2 loads as an empty model.

> **Revision 4 — slot numbers go stale on a real player.** `Player.swapToFront` reorders `team[]` when the player switches
> (the new active mon moves to slot 0), while `Trainer.swap`, the AI's, does not. The model remembered the best switch
> columns as slot numbers at decision time, so by the time a switch was observed the number pointed at a different mon
> (the log showed it expecting slot 3 while Grust, switched to, was now slot 0). It now remembers the mons themselves, by
> identity, built from the real team at decision time. Nothing else the model carries across turns is index-based (the
> bucket, per-move PP and `Move` values are all stable). New test: a Player-style reordered team must classify exactly
> like an unreordered one.

## What this phase does, in one paragraph

The AI now keeps a small, saved record of **how the player plays**: when the AI threatens their active, do they
stay in and click their best attack, switch to their safe back, or switch somewhere else? Each decision, the AI
predicts the player's mix from that record and blends a best response into its equilibrium strategy:
`x = alphaEff * x_eq + (1 - alphaEff) * x_exploit`. The record is per player, persists across battles in the save,
fades if the player changes style, and starts empty, so trainers get harder as the run produces data. With an empty
model the output is **exactly** the Phase 5 equilibrium.

## Decisions that differ from the spec text (all per your instructions)

| Spec says | Done instead | Why |
|---|---|---|
| §8.2 `infoMode` FULL / REVEALED_ONLY | **Not added** | Open team sheets: the AI always sees the player's full sets. |
| §8.2 trainer style profiles | **Not added** | You want difficulty to come from accumulated data, not per-trainer labels. |
| §7.11 history via `Player.recordTurn` | **Own observation** (`PlayerReader.observe`) | `recordTurn` feeds the battle-log upload and exists only for the human seat. The AI diffs the player's active mon and PP between its own decisions, so it also works against self-play opponents. |
| §7.11 per-battle history | **Cross-battle, saved with `Player`** | Your requirement. Fixed-size table, ~1.1 KB serialized. |
| §7.10 quantal-response solver (`temperature`) | **Not added** | Off by default in the spec and not requested; the exploit softmax has its own `temperatureExploit`. Candidate for Phase 8 if you want it. |
| T27 EXTREME half | **Deferred to Phase 7** | EXTREME's only gate is `selectLead`. `forDifficulty(EXTREME)` returns HARD's config for now. |

## Files

| File | Change |
|---|---|
| `pokemon/PlayerModel.java` | **New.** Persistent counts + all pure math (record, estimate, predict, finalStrategy). No engine dependencies. |
| `pokemon/PlayerReader.java` | **New.** Engine glue: situation bucket, column classes, `begin` / `observe`, debug line. |
| `pokemon/ScriptedAI.java` | **New.** Fixed-habit opponents (always-switch-when-threatened, never-switch) for self-play. Test support, like `SelfPlay`. |
| `test/Phase6Tests.java` | **New.** See Tests. |
| `pokemon/AIConfig.java` | +`useHistoryModel`, `alpha`, `temperatureExploit`, `forDifficulty(int)`; header doc updated. |
| `pokemon/AIV2.java` | `plan(root, cfg, model)` overload; predict + blend; `decide` observes / begins; `NO_HISTORY`; log + label use `y_hat`. |
| `pokemon/SelfPlay.java` | `Config.persistModels`; `playBattle` overload taking the two seats' models; determinism replays restart from model snapshots. |
| `pokemon/Trainer.java` | +`transient PlayerModel sessionModel`, +`playerModel()`. |
| `pokemon/Player.java` | +`public PlayerModel habits`, +`playerModel()` override. |
| `test/Phase5Tests.java` | Three one-word visibility edits, see below. |

`changed/` has full copies of AIConfig, AIV2, SelfPlay. `small_edits.patch` has Trainer and Player (25 added lines).

### Edit to apply by hand in Phase5Tests (needed by the T21 probability test)

```
-	private static final class Scen {
+	static final class Scen {
-	private static Scen sackScenario(double scrubHp, ...
+	static Scen sackScenario(double scrubHp, ...
-	private static int switchRow(List<Action> A, int slot) {
+	static int switchRow(List<Action> A, int slot) {
```
Also replace the trailing comment in `t21()` (`// Phase 6: once predict() exists...`) with
`// The probability form lives in Phase6Tests.t21Probability.`

## Design

### "Best" is defined by the AI's own matrix
For each player column `j` the AI's payoff against its own equilibrium mix is `v[j] = sum_i x_eq[i] * M[i][j]`; the
player wants it low. The **best stay** is the stay column (a move or a pivot column) with the lowest `v`; the **best
switch** is the switch column with the lowest `v` (first column wins ties). So "best" means what the full simulation says
(damage, speed, items, KO chances, status, setup) and not a type chart; and a player who keeps picking it is playing the
AI's own idea of good play. The raw equilibrium `x_eq` is used, not the blended strategy, so the classes can never depend
on what the model has learned (tested).

### Situation (what the AI conditions on)
- **threatened**: the AI's active has a move with KO chance >= 0.3 on the player's active (`SackAnalysis.threatens`).
- **switchBetter**: the best switch beats the best stay by more than 5% of the matrix's payoff range, i.e. the simulation
  says switching is the player's better reply. False when the player cannot switch (no switch columns).
- **HP band** of the player's active: under 1/3, under 2/3, or above.
- **AI outspeeds** the player's active.
- **Protect available**: the player has a Protect-family move among their columns.

2 x 2 x 3 x 2 x 2 = **48 fine buckets**; (threatened, switchBetter) gives **4 coarse buckets**; plus **1 global**.

### Action classes (what the AI learns)
`STAY_BEST`, `STAY_OTHER`, `SWITCH_BEST`, `SWITCH_OTHER`, `STAY_PROTECT`. "Best" is a band (within 5 eval points of the
lowest AI payoff, separately for non-Protect stays and for switches). A pivot move counts as a stay (classed by its
move); a switch to a mon that has no column is `SWITCH_OTHER`. What a player's habits look like in this table:

- plays it safe = switches in buckets where switching is better, and also over-switches where staying is better
- plays it right = `*_BEST` almost everywhere (the AI's own good play; the AI cannot gain much by exploiting that)
- plays risky = stays in the "threatened, switching is better" buckets
- always goes to the safe switch-in = high `SWITCH_BEST` among switches

"Playing around the crit" is **not** captured separately.

### How it learns, and how much it trusts it
- `record(fine, class)` decays then increments the fine, coarse and global slots. Decay is 0.97 per observation of
  that slot, so a slot holds at most ~33 observations and old habits fade if the player changes style. This is also
  what bounds the save size.
- `estimate(fine)`: global habit → coarse (shrunk toward global with 4 pseudo-observations) → fine (shrunk toward
  coarse). A situation the player rarely hits still leans on what they do in general, at low confidence.
- Trust `w = eff / (eff + 6)` (spec's K = 6), where `eff` is the effective sample size behind the estimate.
  From the scratch run: 1 observation → w ≈ 0.27; 40 observations in a situation → w ≈ 0.82.

### Prediction and blend
- `predict`: class mix `= w * history + (1 - w) * equilibrium`, spread over each class's columns in proportion to the
  equilibrium (uniformly if the equilibrium gives that class zero mass, so a habit the equilibrium never plays can
  still be predicted). Classes with no column in this matrix are dropped and the history renormalized.
- `finalStrategy`: `alphaEff = 1 - (1 - alpha) * w`; `x_exploit` = softmax over each row's payoff against `y_hat`,
  payoffs scaled by their range (`temperatureExploit = 6`).
- **Wrong-read bound** (tested): against any player strategy the blend is worth at least
  `v_eq - (1 - alphaEff) * range(M)`. With alpha = 0.7 and w ≈ 0.82 that is about 25% of the payoff range, at worst.

### The "gets harder over the run" ramp
Confidence rises with observations, so the exploit share rises from 0 toward `(1 - alpha)` (30% of the mix at full
trust, ~25% once w ≈ 0.82). Two knobs shape the ramp, both placeholders for Phase 8:

- `AIConfig.alpha` — how hard the AI punishes a habit at full confidence. **This is the main "how much harder" knob**:
  at 0.7, no read can move more than ~30% of the probability mass.
- `PlayerModel.K_BLEND` (6) and `DECAY` (0.97) — how fast it ramps and how long it remembers. With the current
  values the ramp is steep: a handful of battles saturate the global habit. If you want it spread across a whole run,
  try `K_BLEND` ≈ 30 and `DECAY` ≈ 0.99.

### Observation rules (`PlayerReader.observe`)
Run at the top of `decide`, before any forced-action return, against the player's CURRENT active:

- different AI trainer, or `Field.turns` lower than at decision time → new battle, discard;
- same mon and no PP spent → nothing observable (flinch / sleep / a second decision in the same turn) → record nothing;
- some move's PP dropped → a stay by that move's class (even if the mon then pivoted or fainted);
- different mon, no PP spent, previous mon not fainted → a switch, best switch or other;
- different mon, previous mon fainted without moving → a KO, not a decision → record nothing.

Known noise (small, only blurs best-back vs other): a forced phaze before the player acts looks like a switch; a
switch-in KO'd the same turn is classed by its replacement. The last turn of each battle is never observed.

### Persistence
`Player.habits` is a plain serializable field. `Player` has a fixed `serialVersionUID`, so existing saves load with
`habits == null` and get an empty model on first use. Non-Player trainers (self-play, betting sim) use a transient
per-session model. A saved table with a different layout loads as empty rather than crashing.

## Method-level change list

**PlayerReader**: `read(model, root, P, M, xEq)` classifies from the matrix; `observe` returns the class it recorded (null if none).
**PlayerModel**: `report()` readout for a debug key.
**AIConfig**: `forDifficulty(int)` new (the one place difficulty is decided; EXTREME = HARD until Phase 7); three new fields.
**AIV2**:
- `plan(root, cfg)` → delegates to the new `plan(root, cfg, model)`. Same steps through the equilibrium, then `read` →
  `predict` → `finalStrategy` → `Shaper.shape`. Pure given the model; no Rng; never writes to the model.
- `Plan` gains `yHat`, `read`, `alphaEff`.
- `decide`: `forDifficulty`; `observe` first (and logs `[AIV2] observed the player: ...`); `begin` after planning; sack label and branch log use `plan.yHat`;
  new `logRead` debug line (situation, trust, per-class history / equilibrium / prediction).
- New instance `AIV2.NO_HISTORY` for A/B self-play; `getName()` reflects the variant.
**SelfPlay**: new `Config.persistModels`; `playBattle(..., on1, on2)` overload (old signature delegates); sampled
determinism replays start from copies of the models taken before the original battle (otherwise they would always mismatch).
**Deleted**: nothing. Legacy AI code was already removed in Phase 4 (your grep for the old identifiers came back empty).

## Tests (`Phase6Tests.runAll()`)

Pure: shrinkage / confidence / decay · predict math · wrong-read bound over 150 random matrices + average exploit gain ·
scripted always-switch 2x2 · serialization round trip, size < 1 KB, pending not saved · **T27** config diff by reflection.
Engine: per-trainer model identity · classes come from the matrix (as a band) and never from the model · Protect is its own class and a situation bit · observe classification end to end through `bestMove2` · empty model == Phase 5
exactly · plan purity (T3/T4 with the model on) · exploit at decision level (shifts toward the habit, bounded when wrong) ·
**T21 probability half** (sack row's probability falls when a switch is expected).
Re-run unchanged and expected green: Phase3Tests (T16, T17, T28, T29, T30), Phase4Tests, Phase5Tests.

Note on one assertion: exploiting a read is **not** guaranteed to beat the equilibrium against that read in every
single matrix (when the equilibrium already best-responds, the soft exploit can be slightly lower). Measured over 400
random matrices: mean gain +4.3% of range, worst shortfall 2.4%. The tests assert exactly that (positive mean, shortfall
under 5%) rather than a per-case claim that would be false.

## What was and wasn't verified

- **Run here:** `PlayerModel` compiled and exercised standalone: shrinkage, decay cap (33.33), predict, the wrong-read
  bound on 200 random matrices (0 violations), the scripted-switcher case (equilibrium 5.0 vs blend 11.2 against an
  always-switcher), serialization (1,137 bytes, round trip exact, `pending` not serialized). The best-band finding was
  checked by recomputing column values from the matrices printed in your battle log.
- **Not run:** everything touching the engine (including Revision 2's matrix-based classification). The uploaded sources don't include `Move`, `Field`, `ActionKind`,
  `SimPolicy`, `Moveslot`, so `PlayerReader`, `ScriptedAI`, `Phase6Tests` and the edited files could only be checked for
  syntax and for non-missing-symbol type errors: none found. `Phase6Tests` is therefore unrun; expect to fix small
  fixture issues the way earlier phases did, and treat a SKIPPED test as "scenario search found nothing", not a pass.

## Assumptions to confirm

1. **`Field.turns` resets to 0 at the start of every battle.** New-battle detection relies on it going down (AIV2 already
   relies on `turns == 0` for the first turn). If a battle can start without that, a stale pending turn could be
   misattributed once; the cost is one noisy observation.
2. **Saves are Java serialization of `Player`** (fixed `serialVersionUID`, `update*` migration methods suggest so). If
   saving is JSON or hand-copied field by field, `habits` needs adding there.
3. **`foe.trainer` is the `Player` object** in real battles, so `playerModel()` hits the saved model.
4. `Moveslot.currentPP` is the PP source of truth and drops by >= 1 when a move is used (Pressure's extra drop is fine).

## Manual verification (live game, game thread)

1. `Phase6Tests.runAll();` then the Phase 3/4/5 suites. Nothing that passed before may fail.
2. Watch it learn: fight any trainer on HARD with `AIV2.AI_DEBUG` on. Each turn prints `[AIV2] observed the player:
   <class> -> model n=...` (what it concluded you did) and a `read fine=.. n=.. w=..` line that starts at `n=0.0 w=0.00`
   with `hist` filling in; `alphaEff` falls from 1.00 as `w` rises. Print the table any time with
   `Print.debug(player.playerModel().report());`. Always switch out when threatened for a few turns and watch the
   "switching is better" rows move toward `best switch`; then stay in for a battle and watch them drift back.
3. Persistence: finish a battle, save, reload, start another: `n=` should start where it left off, not at 0. Load a
   pre-Phase-6 save: it should start at `n=0.0` without errors.
4. Exploit measurement (self-play, 100+ battles each):
   `Phase6Tests.selfPlayVsScripted(ScriptedAI.SAFE_SWITCHER, 100);` and the same with `ScriptedAI.STAYER`.
   It runs `AIV2.INSTANCE` then `AIV2.NO_HISTORY` against the same scripted opponent with models carried across
   battles; the acceptance signal is engine A's win rate higher with the model on.
5. `Phase6Tests.selfPlayHardVsNormal(100);` — HARD should win clearly.
6. `Phase6Tests.selfPlayHistoryAB(100);` — model on vs off on random teams: should be roughly even or better, and
   think time per decision should not noticeably grow (the model adds one threat check, two matchup scans and up to
   four damage calcs per decision).
7. Determinism: the `determinismMismatches` count in the self-play reports stays 0 with `persistModels = true`.

## If SWITCH_BEST still looks low after the band fix

Read the `[AIV2] observed the player: SWITCH_...` lines: each says whether the switch target was in the best band, had a
column outside the band, or had **no column**. If "NO column" is common, the cause is `ActionGen.playerSwitchColumns`
(top `AIConfig.maxPlayerSwitchCols` = 3 by type chart, plus one sack column), which is also a blind spot for the AI's
planning in general. Raising `maxPlayerSwitchCols` to 4 or 5 is a one-line experiment; the cost is matrix width (each
extra column is another full simulation per AI row, and pivot columns multiply with it), so check think time first.
I did not change it without data.

## Not done / for later

- Phase 7: `selectLead`, the EXTREME half of T27.
- Phase 8: tune `alpha`, `K_BLEND`, `K_PARENT`, `DECAY`, `temperatureExploit`; decide whether a quantal-response solver is worth adding.
- Unmodelled habits: playing around crits, Protect/setup tendencies beyond "best attack vs other", per-species habits.
