# Trainer AI Overhaul: Design Spec and Working Prompt

Target: `Pokemon.java` Trainer AI (`bestMove2` and everything it calls), Pokémon Xhenos.
Purpose of this file: upload it alongside `Pokemon.java` (plus any files requested in §13) at the start of each implementation session. Work proceeds **one phase at a time** (§12).

---

## 0. Instructions for the assistant

You are helping implement a redesign of a Java Pokémon-battle Trainer AI. Follow these rules in every session:

1. **One phase per session.** Do not jump ahead. State which phase you are on and its acceptance criteria before writing code.
2. **Ask before assuming.** §13 lists classes and methods that exist but were not shown (e.g. `Trainer`, `Field`, `Task`, `Player`, `calc()`, `fullClone()`). If a phase depends on one, ask to see it. Do not invent its behavior.
3. **Method-level changes, not file dumps.** Return new or replaced methods with clear "replace X / add Y / delete Z" instructions. Do not re-emit the whole of `Pokemon.java`.
4. **Preserve integration points.** `bestMove2(Pokemon foe, boolean first, int difficulty)` must keep returning `MoveDecision` with the same slot-encoding conventions (§2). The debug/sim UI hooks (`simBattleUI.p1Moves`, `p1Switch`, etc.) must keep being populated.
5. **No drive-by refactors.** Only touch what the phase requires. Tie every change to a bug ID (§4), a spec section, or a test (§11).
6. **Every phase ends with**: (a) the tests from §11 that apply, (b) manual verification steps, (c) a list of assumptions you made, (d) what to upload next.
7. **Java style**: tabs for indentation, match existing naming (`fullClone`, `getFaster`, `calcWithTypes`, `Print.debug`).
8. **Determinism rule**: no `Math.random()` / `new Random()` inside evaluation code. Randomness may only appear in (a) the real battle engine and (b) the final action sampling in `decide()`, both via the injectable `Rng` (§7.1).
9. **Difficulty rule**: difficulty is defined only in §1.1 (`allowVoluntarySwitch`, `selectLead`). Do not add any other difficulty-dependent behavior.

---

## 1. Goals and non-goals

**Goals**
- The AI should be *hard because it is unpredictable and because it reads the player*, not because it cheats.
- Unpredictability comes from **mixed strategies** over actions that are each a good answer to some plausible player action.
- Prediction comes from a **player model** (structural + observed history) that biases the mix toward exploiting the player.
- The cost of switching (hit on the way in, hazards, residual damage, lost tempo) is priced by simulation, not by a hand-tuned penalty.
- A move into an immunity can still be clicked when it is the right read (e.g. Fighting move into a Ghost anticipating a switch to Steel).
- Strictly dominated options (e.g. Aqua Jet when Liquidation kills and outspeeds anyway) get ~0 weight.
- Stat boosts/drops, status, hazards and screens are valued by *what they change in the resulting position*, not by placeholder constants.
- One engine for every difficulty. Difficulty only gates voluntary mid-turn switching (off in NORMAL) and lead selection (on in EXTREME); see §1.1.
- **Sacking**: when the AI is in a bad position, it sacks a less valuable bench mon to get a free, safe replacement instead of letting a more valuable active mon faint (§7.14).
- **EXTREME lead selection**: the AI picks its lead from the player's team via a simultaneous-choice matrix, so its lead can't be counter-picked (§7.15).

**Non-goals (for now)**
- Multi-turn search beyond a shallow continuation (1.5-ply via `eval`).
- Doubles.
- Rewriting the real battle engine (only extracting a reusable turn-resolution order if necessary).

### 1.1 Difficulty modes (authoritative definition)

There is **one AI engine**. Difficulty changes only two gates. Move evaluation, player prediction, the payoff matrix, the solver, the forced-replacement chooser and the sacking logic are identical at every difficulty.

| Mode | Move selection and prediction | Switching out of an unfavorable matchup | Lead selection |
|---|---|---|---|
| **NORMAL** | Full HARD checks (payoff matrix over moves, including predicted player switches) | **Off.** The AI stays and fights. If it loses the matchup, the strong switch-in AI (the same one every difficulty uses) picks its replacement. | Trainer's team order (existing behavior; verify) |
| **HARD** (default) | Full | **On**, including sacking (§7.14) | Trainer's team order |
| **EXTREME** | Full (identical to HARD) | On | **The AI picks its own lead** from the player's team via a simultaneous-choice lead matrix (§7.15). Both sides choose without seeing the other's pick. |

**Purpose of NORMAL.** It helps the player learn the move-selection AI and rewards having good answers. If the player's Pokémon wins the matchup, the AI loses that Pokémon and the strong switch-in logic chooses the best answer against the player's mon. The back-and-forth tempo still exists; it is easier because the AI won't bail out of bad matchups.

**Switching that is always allowed, at every difficulty** (none of these is "leaving an unfavorable matchup"):
1. **Pivot moves** (U-turn, Volt Switch, Flip Turn, Parting Shot, etc.).
2. **Perish Song will faint it this turn** (`perishCount == 1`).
3. **Only Struggle is available.** If it can switch, it swaps (existing behavior).
4. **Dead turn**: nothing the mon can do matters against the active foe. Examples: Choice-locked into a move the foe is immune to (Choice Band Earthquake after the player switches in a Flying type), every move immune or blocked, only useless status moves. See §7.13.1.

Implementation notes:
- NORMAL removes plain `SWITCH` rows from the AI's action set unless one of cases 2–4 applies (`switchRowsAllowed`, §7.13.1). Pivot moves (case 1) are separate `MOVE_THEN_SWITCH` rows and are always present.
- NORMAL does **not** change how the AI predicts the player. The player's switch columns stay in the matrix at every difficulty, so NORMAL still anticipates pivots and can click into them.
- EXTREME's extra behavior happens once, before turn 1. After the leads are out, it plays exactly like HARD.
- Difficulty is checked in exactly two places: the switch-row gate in action generation and the lead gate at battle start. Do not add other difficulty-specific behavior (§0 rule 9).

---

## 2. Conventions observed in the existing code

(Inferred from the provided code; verify where marked.)

- `Move.cat`: 0 = physical, 1 = special, 2 = status.
- `getStat(0)` = max HP, 1 = Atk, 2 = Def, 3 = SpA, 4 = SpD (Speed accessed through `getFaster`).
- `statStages` indices: 0 Atk, 1 Def, 2 SpA, 3 SpD, 4 Spe, 5 Acc, 6 Eva.
- `calcWithTypes(...)` returns `Pair<damage, damagePercent>` with sentinels: `-1` = move unusable/fails (Fake Out not first turn, Dream Eater on awake foe), `0` = no damage/immune, `(1, 0.0)` = accuracy check failed when `checkAcc`.
- `calc(..., mode)`: `mode` 0 = random roll (**verify**), `1` = max, `-1` = min (from `script ? 1 : foe.script ? -1 : 0`). `calcWithTypes` mode 0 also rolls random crits and other random branches (Chromo Beam, Explosion, Future Sight).
- Decisions are encoded as:
  - `new MoveDecision(move)`: plain move.
  - `new MoveDecision(pivotMove, Status.TEMP_SWITCHING, -(slot+1))`: pivot move then switch to `slot`.
  - `new MoveDecision(Move.GROWL, Status.SWAP, slot+1)`: voluntary switch (dummy move `GROWL`). `BattleUI.NORMAL_SWITCH` = "let the game pick".
  - `new MoveDecision(move, pendingSwitchList)`: move plus deferred switch(es).
- Simulation-suppression: static flag `createTask` toggled around clone simulations. Clones have `cloned == true`. `fullClone()` clones the mon; **`clone.trainer` still points at the real `Trainer`**.
- `Print.debug(String)` is used for AI tracing; strings are built even when debug is off.
- `Player` records history (`recordTurn`, `recordSwitchIn`, `recordDamageDealt`). Contents unverified (see §13).

---

## 3. Current system: keep / change / delete

| Existing piece | Verdict | Notes |
|---|---|---|
| `bestMove2` (shell, script-battle handling, `MoveDecision` output, sim-UI reporting) | **Change** | Becomes thin wrapper around `decide()`. Keep script-battle turn-1 logic and the "no valid moves → Struggle / forced swap" handling as pre-checks. |
| NORMAL-difficulty rule branch (perish rule, `hasRealAction` swap) | **Delete** | Replaced by `switchRowsAllowed` (§7.13.1): NORMAL = the same matrix with plain SWITCH rows removed unless a pivot move, Perish-in-1, Struggle-only or a dead turn applies. `hasRealAction` becomes `deadTurn` (bug B2 disappears). Keep these cases as *regression tests*, not code paths. |
| Non-NORMAL score-diff switch branch (`weights`, `chance = min(95,|diff|/2)`) | **Delete** | Replaced by switch rows in the matrix. |
| `findFoeStrongestMove`, `FoeMoveResult` | **Delete** | Matrix columns replace "assume foe clicks strongest". (May be kept as a cheap heuristic for pruning only.) |
| `DefensiveResponseResult`, `analyzeDefensiveResponse`, `calculateDefensiveResponse`, `applyAggression` | **Delete** | The aggression scalar is replaced by the equilibrium/exploit blend. |
| `scoreMove` (damage + accuracy + kill bonuses + custom heuristics) | **Delete, harvest** | Its special-move heuristics are catalogued in §9; each must be re-expressed as simulator behavior or an `eval` term. |
| `analyzeMoveEffect`, `EffectChange`, `EffectAnalysisResult`, `scoreEffectUsefulness`, `statChangeIsUseful`, `detectStatStageChanges`, `find*` helpers, `vStatusesEqual` | **Delete** | "Diff what changed and score categories" is replaced by `eval(after) - eval(before)`. |
| `scorePokemon`, `evaluateSwitchInScore` (recursive) | **Delete** | Recursion is the main perf problem. Switch-in state comes from the simulator. Also removes the `Integer.MIN_VALUE + 1` "fainted on switch-in = terrible" rule, which is what currently prevents sacking (§7.14). **External callers exist** (`Trainer.pickLead`, `predictPlayerLeads`, and probably the real forced-replacement path): keep a thin non-recursive replacement until they are migrated. |
| `simulateSwitchIn` | **Keep, extend** | Becomes the switch step inside `simulateTurn` (uses `swapIn` with hazards; save/restore `createTask`). |
| `matchupScore` | **Keep, change inputs** | Keep the continuous formula and constants. Feed it *damage fractions and KO probabilities* (from `DamageRange`), not move scores. Becomes the `activeMatchup` term of `eval`. |
| `calcHazardTeamValue`, `hazardMoveForEffect`, `isHazardUseful` | **Keep** | Used by `eval`'s hazard term and to prune useless hazard rows. |
| `scoreWeatherValue`, `scoreScreenValue` | **Keep, adapt** | Duration-weighted field terms in `eval` (lasting effects can't be seen by a one-turn evaluation). |
| `evaluateRetype`, `evaluateAbilityOverwrite` | **Mostly delete** | After simulation, `calc` already sees the new type/ability. Keep only ability values that damage calc can't see. |
| `hasPivotMove`, `isUsefulPivot` | **Keep** | Used by action generation for `MOVE_THEN_SWITCH`. |
| `chooseSwitchInSlot` | **Change to `chooseReplacement`** | The shared, non-recursive forced-replacement chooser used by both the real battle and the simulator (§7.14.3). |
| `Trainer.pickLead`, `predictPlayerLeads`, `weightedRandomSelection` | **Replace (Phase 7)** | Superseded by `chooseLead` (§7.15). `pickLead` stays as a thin wrapper. They currently call `evaluateSwitchInScore(foe, field)`, so don't delete that overload before Phase 7. |
| `calcStatBoostScore` | **Delete** | Value of stripping boosts (Red Card, Roar) emerges from `eval` on the post-phase state. |
| `calcWithTypes` | **Change** | Split into deterministic `calcRange` (§7.3) for fast paths. Random branches removed from evaluation use. |
| `move()`, `swapIn()`, `endOfTurn()` | **Keep, harden** | Become the ground truth for Tier-2 simulation. Requires a simulation-safe mode (§7.2, §4 bugs B9–B12). |
| `Trainer.swapRandom` | **Change** | Must be safe on trainer shells (§7.2). |

---

## 4. Bug list

Fix the **Persistent** bugs in Phase 0/1 (they will corrupt the new system). **Legacy-only** bugs die with the code they live in, so only fix them if the legacy path stays live during migration.

| ID | Where | Problem | Fix | Class |
|---|---|---|---|---|
| B1 | `bestMove2` non-NORMAL branch | `allyScore > currentScore && allyScore > 0` blocks switching whenever all allies are negative even if the active mon is far worse (observed: active -150, best ally -12, no switch weights computed). | Drop the `> 0` gate (or compare relative). | Legacy-only |
| B2 | `bestMove2` NORMAL branch, `hasRealAction` loop | `this.calcWithTypes(foe, strongestMove, ...)` uses the **foe's** strongest move instead of `resolvedMove`. It checks whether the foe's best move would hurt the foe. | Use `resolvedMove`. | Legacy-only |
| B3 | `endOfTurn`, Shed Skin | `(int)(Math.random()) % 2 == 0` is always true, so Shed Skin always procs. | Replace with `Rng.chance(1.0/3)` (confirm intended chance). | Persistent |
| B4 | `move()`, Magic Reflect | `move == BRICK_BREAK && move == MAGIC_FANG && move == PSYCHIC_FANGS` can never be true (`calcWithTypes` has it correct with `!=` chain). | Use `||` in `move()`. | Persistent |
| B5 | `scoreMove`, Endure | `score -= -20` adds 20. | `score -= 20`. | Legacy-only |
| B6 | `calcWithTypes` | Merciless treated as active only vs POISONED/TOXIC; `move()` also includes PARALYZED. | Make identical. | Persistent |
| B7 | `calcWithTypes` | Light move vs foe with `Ability.NEUROFORCE` returns 0 damage; `move()` has no such immunity (looks like a typo for the attacker-side Neuroforce super-effective bonus). | Remove NEUROFORCE from the immunity check. | Persistent |
| B8 | `calcWithTypes` mode 0 | Random crits, Chromo Beam roll, Explosion roll, Future Sight roll leak randomness into AI evaluation. | Introduce `calcRange` (deterministic); evaluation must never use mode 0. | Persistent |
| B9 | `simulateSwitchIn`, `analyzeMoveEffect` | `createTask = true` in `finally` overwrites the outer state; nested calls re-enable tasks early. | Save previous value, restore it (or replace with `SimContext` counter). | Persistent |
| B10 | `Trainer.swapRandom` | `oldCloned` shortcut sets `this.current = team[index]` on a **shared** trainer, so the real trainer's `current` can change during AI analysis; `analyzeMoveEffect` compares `trainer.current` but never restores it. | Operate only on trainer shells (§7.2); add invariance test T3. | Persistent |
| B11 | `move()` | `recordTurn` / `recordDamageDealt` on the player run for clones too (`swapIn` guards with `!cloned`, `move` doesn't). | Guard with `!this.cloned` / sim-mode flag. Also check other counters (`headbuttCrit`, `tailCrit`, `field.misses/crits/superEffective`). | Persistent |
| B12 | `endOfTurn`, Leech Seed | `hp *= 1.3` (Big Root) applied *after* damage; heal uses `damage`. | Scale the heal amount. | Persistent |
| B13 | `analyzeMoveEffect` | `foeClone.getAbility(field)` reads the original field instead of `fieldClone`. | n/a | Legacy-only |
| B14 | `calculateDefensiveResponse` | Debug label says `(x)^2`; code is linear. | n/a | Legacy-only |
| B15 | `findFoeStrongestMove` | Hardcodes `first=true`, uses `Collections.shuffle` (non-deterministic ties). | n/a | Legacy-only |

---

## 5. Target architecture

```
bestMove2(foe, first, difficulty)
  └─ decide(self, foe, cfg)
       1. root = SimState.snapshot(self, foe)                # one clone pass, trainer shells
       2. forced = forcedAction(root)                        # script turn, Struggle, recharge, locked, charging...
          if forced → return it
       3. A = genAIActions(root, cfg)                        # rows: moves + pivots; plain switches gated by difficulty; sack candidates always included
          P = genPlayerActions(root, cfg)                    # columns
       4. (A, P) = prune(A, P, root)                         # dominance + plausibility caps
       5. M[a][p] = payoff(root, a, p)                       # simulateTurn + eval (tiered)
       6. y_hat = playerModel.predict(M, root, cfg)          # column prior
       7. x = solve(M, y_hat, cfg)                           # equilibrium + exploit blend
       8. x = shape(x, cfg)                                  # cutoff, temperature, epsilon
       9. a* = sample(x, Rng)                                # only randomness in the pipeline
      10. report(x, y_hat, M)  → Print.debug, simBattleUI    # keep UI probabilities working
      11. return toMoveDecision(a*)
```

Key property: `M[a][p]` is a **pure function of the snapshot** (deterministic given `cfg`), so decisions are reproducible from a seed.

Pre-battle (EXTREME only): `chooseLead(...)` (§7.15) runs once before turn 1, independently of the player's lead selection. After the leads are out, every difficulty uses the same `decide()`.

---

## 6. Data structures (language-neutral)

```
enum ActionKind { MOVE, SWITCH, MOVE_THEN_SWITCH }

struct Action {
    kind: ActionKind
    move: Move            // MOVE, MOVE_THEN_SWITCH
    slot: int             // SWITCH, MOVE_THEN_SWITCH (team index)
    label(): string       // for debug
}

struct DamageRange {
    usable: bool          // false for "move fails" (replaces -1 sentinel)
    immune: bool          // type/ability immunity (replaces 0 sentinel)
    min, avg, max: double // damage in HP, all-roll, no crit
    critProb: double
    accuracy: double      // effective accuracy 0..1 (1.0 for never-miss)
    hits: double          // expected number of hits
    koProb(hp): double    // P(damage >= hp) over the 16 rolls, includes crit mixture
    expected(): double    // accuracy * hits-adjusted avg incl. crit expectation
}

struct Branch { prob: double; state: SimState }   // outcome of one cell

struct SimState {
    field: Field                  // cloned
    ai:     SideState
    player: SideState
    turn:   int
    fork(touched: set<slot,side>): SimState       // copy-on-write; clones only touched mons + field
    fingerprint(): long                           // for invariance tests
}

struct SideState {
    shell: Trainer                // shallow shell: own team[] array, own current, own side effect lists
    active(): Pokemon
    bench(): Pokemon[]            // lazily cloned
}

struct AIConfig {                 // difficulty gates (§1.1) + shared tuning knobs (§8)
    allowVoluntarySwitch: bool    // NORMAL=false (pivot moves and Perish-in-1 still allowed); HARD/EXTREME=true
    selectLead: bool              // EXTREME only (§7.15)
    deadTurnForcesSwitch: bool    // NORMAL dead turn: force a swap instead of letting the matrix decide (default false; §7.13.1)
    sackRatio: double             // a sack candidate must be worth < sackRatio * value(active) (§7.14)
    considerPlayerSwitches: bool
    maxPlayerSwitchCols: int
    maxAISwitchRows: int
    temperature: double           // QRE/softmax sharpness
    alpha: double                 // weight of equilibrium vs exploit
    minProb: double               // cutoff
    epsilon: double               // mistake rate
    useHistoryModel: bool
    infoMode: FULL | REVEALED_ONLY
    style: EvalWeights            // aggressive/stall/balanced profile
    branchBudget: int
    useFullSim: bool              // Tier-2 on/off
}

struct PlayerModel {
    counts: map<Situation, ActionClassCounts>   // shrinkage-smoothed
    predict(M, root, cfg): double[]             // distribution over columns
}
```

---

## 7. Algorithms

### 7.1 Randomness plumbing (`Rng`)

```
class Rng {
    static setSeed(long); static double next(); static boolean chance(double p);
    static int nextInt(int n);
}
```
Replace every `Math.random()` and `new Random()` in battle/AI code with `Rng`. This enables seeded self-play (§11) and reproducible bug reports. **Simulation flag**: `SimContext.active` (depth counter) tells `move()` / `endOfTurn()` / `swapIn()` to (a) suppress Tasks, (b) suppress player-stat recording, (c) route randomness through `SimPolicy` (below) instead of `Rng`.

```
SimPolicy (when SimContext.active)
    accuracy:   forced hit or forced miss per branch (chosen by simulateTurn)
    damageRoll: average roll (0.925)
    crit:       never; expected crit handled via DamageRange
    secondary:  expected value, or forced proc/no-proc per branch (Phase 4+)
    randomTarget (Red Card, Roar, Dragon Tail): expected over eligible bench (see 7.7)
```

### 7.2 Snapshot, trainer shells and `SimState`

Problem: `clone.trainer` is the real `Trainer`. Anything that mutates trainer state (`swapRandom`, `current`, `team[]`, per-side hazard lists, `canSwitch`) from a clone corrupts the real battle.

```
SimState snapshot(self, foe):
    fieldC  = field.clone()
    aiShell     = Trainer.simShell(self.trainer)      # new Trainer object: team[] = refs to CLONES, current = clone of self
    playerShell = Trainer.simShell(foe.trainer)
    for each mon clone c on a side:  c.trainer = that side's shell;  c.cloned = true
    copy per-side field-effect lists (hazards etc.) into the clones/shell so real lists are untouched
    return SimState(fieldC, ai, player)

SimState fork(touched):        # per matrix cell
    s = shallowCopy(this)
    s.field = field.clone()
    for (side, slot) in touched ∪ {both actives}:
        s.side.team[slot] = team[slot].fullClone(); repoint trainer to s's shell copy
    s.side.shell = shell.copyWithNewTeamArray()
    return s
```
Bench mons not in `touched` stay shared and **read-only**. Enforce with a debug assertion: `fingerprint()` of shared bench mons must not change across a cell (test T3).

`Trainer.swapRandom` / `canSwitch` / `hasValidMembers` must work purely on the shell (`this.team`, `this.current`). Remove the `oldCloned` early-return hack once shells exist (B10).

Invariance contract: **`decide()` leaves every real object unchanged**. Test T3 checks `fingerprint(real teams, real field, real trainer.current)` before and after.

### 7.3 Deterministic damage primitive

```
DamageRange calcRange(attacker, defender, move, state, ctx):
    # Same rules as calcWithTypes/move() but:
    #  - never rolls RNG; iterates the 16 damage rolls (0.85..1.00 in 1/100 steps) or uses min/avg/max
    #  - crit: critProb from move.critChance/abilities/items; koProb mixes crit and non-crit
    #  - accuracy from getEffectiveAccuracy (weather/gravity/abilities/items) ; never-miss → 1.0
    #  - multi-hit: expected hits via getNumHits(); per-hit damage where hits scale bp
    #  - Sturdy/Focus Sash/False Swipe/Endure cap: koProb = 0 when they'd survive
    #  - returns usable=false / immune=true instead of sentinels
    #  - Counter/Mirror Coat/Metal Burst: return usable, damage 0 (handled by simulateTurn via damageTaken)
```
Parity requirement (T1): `calcRange.avg` must match the mean of many `move()` simulations under `SimPolicy.averageRoll=false` (random rolls) within tolerance for a broad random sample of mons/moves/fields.

Implementation guidance: refactor `calcWithTypes` so `calcRange` and the existing method share one internal `computeDamage(attacker, defender, move, state, RollMode)`. **No copy-paste.** This is what stops future drift (B6, B7).

### 7.4 Action generation

```
genAIActions(root, cfg):
    A = []
    for m in self.getValidMoveset():                       # already honors PP, Disable, Choice lock, Taunt, Torment...
        A += Action(MOVE, m)
    if self.trainer.canSwitch(foe):
        switchTargets = candidateBench(root, cfg)              # answers ∪ sack candidates (§7.14.2)
        voluntaryOK = switchRowsAllowed(root, cfg)               # §7.13.1: difficulty gate, Perish-in-1, Struggle-only, dead turn
        if voluntaryOK:
            for slot in switchTargets: A += Action(SWITCH, slot)
        for m in pivot moves where isUsefulPivot(...):         # pivots are legal at EVERY difficulty
            for slot in switchTargets: A += Action(MOVE_THEN_SWITCH, m, slot)   # cap: best 3 answers + all sack candidates
    return A

genPlayerActions(root, cfg):
    P = []
    for m in playerMovesKnown(cfg.infoMode):               # FULL → getValidMoveset(); REVEALED_ONLY → seen + plausible
        P += Action(MOVE, m)
    if playerCanSwitch:
        for slot in aliveBenchSlots(player) capped to cfg.maxPlayerSwitchCols: P += Action(SWITCH, slot)
        for pivot moves similarly
    return P
```
`considerPlayerSwitches` is true at every difficulty. NORMAL removes the AI's own plain-switch rows (§1.1); it does not remove the player's switch columns.

AI switch rows come from `candidateBench` (§7.14.2): the top-N answers plus the sack candidates. Only the answers part is capped; sack candidates are never pruned away.
Cap rule for switch columns: rank bench mons by cheap "matchup vs AI's move types" and keep the top N; always keep any bench mon that resists/is immune to the AI's likely move (that is exactly the pivot the AI must cover). When the AI threatens the player's active, also keep the player's lowest-value bench mon(s) as columns, since a human may sack (§7.14.2).

### 7.5 Turn simulation

```
List<Branch> simulateTurn(root, aAct, pAct, cfg):
    s = root.fork(touched = slotsInvolved(aAct, pAct))
    branches = [Branch(1.0, s)]

    # 1. Switches happen before moves. If both switch, faster switches first (matters for Intimidate etc.)
    for act in orderedBySpeed(switchesOf(aAct, pAct)):
        performSwitch(s, act)               # outgoing: clear volatiles via swap(); incoming: swapIn(foe, hazards=true, field)
        resolveFaintsInline(s)              # hazards can KO the incoming mon

    # 2. Moves in priority/speed order (getFaster, Trick Room aware, priority via move.getPriority)
    for (attacker, move) in orderedMoves(s, aAct, pAct):
        if attacker fainted or has no action: continue
        branches = expandBranches(branches, attacker, move, cfg)   # accuracy hit/miss, KO-roll (see below), speed ties
        for br in branches:
            executeMove(br.state, attacker, move)       # Tier 1: applyDamageOnly(calcRange)   Tier 2: move() on sim clones
            if move is pivot and attacker alive:  performSwitch(br.state, pivotTarget)   # BEFORE opponent's move
            resolveFaintsInline(br.state)               # Destiny Bond, recoil, etc. handled inside move()
            handleForcedSwitch(br.state)                # Red Card / Eject Button / Roar / Dragon Tail: 7.7

    # 3. End of turn: per active mon in speed order: mon.endOfTurn(opponent)
    #    plus field-level ticks the real loop performs (weather/terrain countdown, Future Sight, Wish,
    #    Healing-Wish-style effects, Perish handled inside endOfTurn)
    for br in branches: endOfTurnPhase(br.state); resolveFaintsInline(br.state)

    # 4. Replacement for fainted actives (see 7.6)
    for br in branches: resolveReplacements(br.state)

    return mergeSimilar(branches, cfg.branchBudget)
```

Branching policy (keeps cost bounded):
- **Accuracy**: branch hit/miss when `0.05 < acc < 0.95` for moves whose outcome matters.
- **KO-roll**: if `0.05 < koProb < 0.95` → two branches: KO'd (prob koProb) and survives at HP after an average non-KO roll.
- **Speed ties**: two branches.
- **Secondary status/flinch** (Phase 4+): branch only when chance in [0.1, 0.9] *and* the effect is status/flinch/flinch-like; otherwise expected value.
- Cap total branches at `cfg.branchBudget` (e.g. 4). Merge lowest-probability branches into their nearest sibling.

`expected HP` is otherwise carried as an integer computed from the average non-KO roll. Never sample.

**Endpoint**: a branch is a *start-of-next-turn* state.

`endOfTurnPhase` must mirror what the real battle loop does. **Action item**: locate the real turn loop and either extract a shared `resolveEndOfTurn(state)` used by both engine and simulator, or write the simulator's version with a test that compares both on recorded battles (T5).

### 7.6 Faints and replacement inside the simulation

```
resolveReplacements(state):
    for side in [ai, player]:
        if side.active fainted and side has alive bench:
            slot = side == ai ? aiReplacementHeuristic(state)
                              : playerReplacementHeuristic(state, cfg)   # worst-for-AI among top 2 by cheap matchup; or model-weighted
            performSwitch(state, slot)          # takes hazards, triggers Intimidate etc.
```
`aiReplacementHeuristic` / `playerReplacementHeuristic` are cheap and **non-recursive**: type-matchup + HP + hazards damage (can reuse `chooseSwitchInSlot`'s idea without `evaluateSwitchInScore`). If a side has no alive mons, the state is terminal. The AI's version must be the same function the real battle uses to pick the AI's forced replacement (§7.14.3).

### 7.7 Random forced switches (Red Card, Eject Button, Roar, Whirlwind, Dragon Tail, Circle Throw)

The random target must not be sampled. Use the expected value:

```
handleForcedSwitch(state):
    candidates = eligible bench of the affected side
    if |candidates| == 1: switch to it
    else: produce up to 2 sub-branches: (a) average of candidates' post-switch eval, (b) worst-for-AI candidate
          # cheap approximation: pick the candidate with median cheap-matchup score, weight 1.0
```
Eject Button / Eject Pack / Slipstream / pivot moves: the *owner* chooses the target: AI side uses `aiReplacementHeuristic`, player side uses `playerReplacementHeuristic`.

### 7.8 State evaluation (`eval`)

Return a value in "roughly HP-points" units from the AI's perspective (100 ≈ one full healthy mon). Start weights are placeholders; tune with self-play (§12 Phase 8).

```
eval(s, style):
    if s.player has no alive mons: return +WIN
    if s.ai     has no alive mons: return -WIN

    v  = style.wMaterial * (material(s.ai) - material(s.player))
    v += style.wMatchup  * activeMatchup(s)
    v += style.wHazard   * (hazardPain(s.player) - hazardPain(s.ai))
    v += style.wStatus   * (benchStatusValue(s.player) - benchStatusValue(s.ai))
    v += style.wField    * fieldValue(s)
    v += style.wTempo    * tempo(s)
    v += forcedTurnPenalty(s)                        # recharge / charging / locked states cost or gain a turn
    return v

material(side) = Σ over alive mons: 100 * hpFrac(mon) * monWeight(mon)  +  ALIVE_BONUS * aliveCount
                 # monWeight = computeMonWeights (§7.14.1): HP-independent team-value weight, mean 1.0, computed once per decision

activeMatchup(s):
    myR   = bestRange(s.ai.active,     s.player.active)    # over calcRange, expected + koProb
    foeR  = bestRange(s.player.active, s.ai.active)
    iAmFaster = getFaster(...) == s.ai.active               # with post-turn stages, para, Trick Room, priority
    return matchupScore(myR.frac, foeR.frac, iAmFaster)     # existing formula, inputs are now damage fractions / KO odds
                                                            # both sides use CURRENT stages/status/items/field, so boosts, burn,
                                                            # screens, weather, retyping etc. are "seen" automatically

hazardPain(side)  = Σ alive bench mons: expected switch-in damage % (reuse calcHazardTeamValue)
benchStatusValue  = Σ alive bench mons: status penalty × hpFrac (sleep/para/toxic/burn/frostbite; Natural Cure aware)
fieldValue(s)     = screens/weather/terrain/Trick Room/tailwind-like effects × min(turnsLeft, HORIZON) × per-turn benefit
                    (reuse scoreScreenValue / scoreWeatherValue as per-turn benefits)
tempo(s)          = +small if AI holds initiative (faster, or opponent must recharge/is trapped)
```

Why this replaces the effect-diffing code: a +1 SpA boost changes `bestRange` and `koProb` only when it crosses a threshold; a Defense boost matters only when `foeR` changes; Toxic Spikes lowers `hazardPain` in proportion to the bench size; screens change `foeR` and have a duration. Things the calc cannot see (Wish, Healing Wish, Aromatherapy targets, future-turn value of Perish Song) get explicit terms (§9).

### 7.9 Building the payoff matrix

```
buildMatrix(root, A, P, cfg):
    switchInCache = {}                                     # (side, slot) → post-switch-in SimState pieces (hazards, Intimidate, etc.)
    M = new double[|A|][|P|]
    for a in A:
        for p in P:
            branches = simulateTurn(root, a, p, cfg)       # uses switchInCache where possible
            M[a][p] = Σ br.prob * eval(br.state, cfg.style)
    return M
```

Tiering (implement AFTER correctness, guided by profiling):
- **Tier 0 (pruning before the matrix)**: drop rows/cols weakly dominated on a *cheap* proxy (e.g. two moves of the same type where B's damage ≥ A's against every plausible target and A adds nothing else); drop hazard rows if `!isHazardUseful`.
- **Tier 1 (fast cell)**: both actions are plain damaging moves or switches with no ability/item/secondary that matters: use `calcRange` + `applyDamageOnly`, no clone of `move()`.
- **Tier 2 (full cell)**: anything with `cat==2`, `secondary != 0`, pivots, Red Card/Eject, abilities that trigger (Intimidate, Rough Skin, etc.), Counter-family, charge/recharge. Runs `move()` in sim mode.
- Optionally solve twice: coarse (Tier 1 for everything) → refine only rows/cols with meaningful probability using Tier 2 → re-solve.

Caches: switch-in states depend on `(side, slot)` and hazards but not on the opposing action, so compute them **once per decision** and reuse across all cells. Damage ranges depend on `(attackerState, defenderState, move)`, so memoize by fingerprint within the decision.

### 7.10 Solver

Zero-sum assumption: player's payoff = −AI's payoff (good enough; the eval is a shared "who's winning" measure).

Regret matching (simple, tiny matrices, no LP dependency):

```
solveZeroSum(M, iters = 500):
    n = rows; m = cols
    Rr = zeros(n); Rc = zeros(m); Sx = zeros(n); Sy = zeros(m)
    for t in 1..iters:
        x = positivePartNormalized(Rr, fallback = uniform)
        y = positivePartNormalized(Rc, fallback = uniform)
        rowPay = M · y                       # AI payoff of each pure row vs y
        colPay = -(xᵀ · M)                   # player's payoff of each pure column vs x
        Rr += rowPay - (x · rowPay)
        Rc += colPay - (y · colPay)
        Sx += x; Sy += y
    return (Sx/iters, Sy/iters)              # average strategies converge to equilibrium
```

Smooth/"human" variant (quantal response): replace positive-part normalization with `softmax(temperature * cumulativePayoff)`. Use when `cfg.temperature` is finite. Scale payoffs so `temperature` means the same at every difficulty (normalize `M` by its range).

Sanity tests (T7): matching pennies → 50/50; rock-paper-scissors → 1/3 each; a strictly dominated row → weight 0; a matrix with a dominant row → weight 1.

### 7.11 Player model and exploitation

```
predict(M, root, cfg):
    y_struct = solveZeroSum(M).y                       # what a rational player mixes
    if !cfg.useHistoryModel: return y_struct
    sit  = situationBucket(root)                       # e.g. {player active threatened?, player has good back?, HP band, AI outspeeds?}
    hist = counts[sit]                                 # action-class distribution from past turns
    w    = n / (n + K)                                 # shrinkage weight from observation count (K ≈ 6)
    classProb = w * hist + (1 - w) * classMass(y_struct)      # classes: STAY_ATTACK_BEST, STAY_OTHER, SWITCH_BEST_BACK, SWITCH_OTHER
    return spreadClassOverColumns(classProb, y_struct)         # within a class, keep y_struct's relative weights

finalStrategy(M, y_hat, cfg):
    x_eq      = solveZeroSum(M).x
    x_exploit = softmax(cfg.temperature_exploit * (M · y_hat))         # or argmax at high difficulty
    x         = cfg.alpha * x_eq + (1 - cfg.alpha) * x_exploit
    return x
```
`alpha` shrinks toward 1 (pure equilibrium) when the model has few observations, so a wrong read can't be catastrophic.

History source: `Player.recordTurn` / `recordSwitchIn` (see §13). If they don't record enough, add a per-battle log of `(turn, playerActionClass, situationBucket)` there.

### 7.12 Shaping and sampling

```
shape(x, cfg):
    x = x / sum(x)
    x[x < cfg.minProb] = 0; renormalize                    # kills "hedge 2% on a junk move"
    if cfg.epsilon > 0: x = (1 - eps) * x + eps * uniformOverLegalRows      # deliberate mistakes (difficulty knob)
    return x

sample(x): draw with Rng.next()
```
Guard: if all entries zero after cutoff, fall back to argmax of `x` before cutoff.

Reporting: derive `simBattleUI.p1Moves/p2Moves` probabilities (moves only, renormalized) and `p1Switch/p2Switch` reasons from `x` (e.g. `"[Matrix: switch to Hueduu 31%]"`), so existing debug UI and probability displays keep working.

### 7.13 `forcedAction` pre-checks (kept from existing code)

Return early, no matrix, when:
- script battle turn 0 (`this.script`, `gp.currentMap == 160` → status move, else `moveset[0]`).
- all moves 0 PP (Struggle only): if the AI can switch, it swaps at **every difficulty** (existing behavior). The target comes from the matrix restricted to SWITCH rows (sack candidates included). If trapped, Struggle.
- `Status.RECHARGE`, `CHARGING`, `SEMI_INV`, `LOCKED`, `ENCORED` mid-sequence, forced Sleep Talk, etc.: action is forced; but still let `SWITCH` compete when legal (Outrage/Encore lock the *move*, not the switch).
- Trapped (`!canSwitch`): switch rows omitted.
- NORMAL (`allowVoluntarySwitch = false`): plain SWITCH rows are removed **unless** `switchRowsAllowed` (§7.13.1) says otherwise. Pivot moves are always legal.

#### 7.13.1 `switchRowsAllowed` and `deadTurn`

```
switchRowsAllowed(root, cfg):
    if cfg.allowVoluntarySwitch:              return true     # HARD / EXTREME
    if self.perishCount == 1:                 return true     # will faint to Perish Song this turn
    if validMoves is empty (Struggle only):   return true     # forced swap at every difficulty (see above)
    if deadTurn(root):                        return true     # nothing this mon can do matters right now
    return false                                              # NORMAL and the mon can still act: stay and fight

deadTurn(root):        # deliberately checks only the ACTIVE foe (no back-check)
    baseline = eval(simulate(root, AI: PASS, player: PASS))              # end-of-turn effects only
    for m in validMoves:
        if isChoiceHolderNotYetLocked(self) and m.cat == 2 and !m.isMagicBounceEffected(...):
            continue          # clicking a status move would lock us in; not a real action (existing rule)
        after = simulate(root, AI: MOVE m, player: PASS)
        if eval(after) - baseline > DEAD_EPS:          # damage dealt, a useful status/stat/field change, ...
            return false
    return true
```

Notes:
- This is the same idea as the existing `hasRealAction` check, which passes `null` for the likely switch-in and therefore evaluates against the active foe only. Preserve that: an AI that would only be useful if the player switched is still in a dead turn.
- Typical triggers: Choice-locked into a move the foe is immune to, every move immune or blocked, Taunt leaving only useless status moves, Struggle.
- Bug B2 disappears with the rewrite: `deadTurn` evaluates every move in `validMoves`, not the foe's `strongestMove`.
- Choice items: `move()` sets `choiceMove` when a Choice holder acts. `eval` needs a lock-cost term (locked into a move that does nothing against the active foe and the likely switch-ins), so the matrix itself discourages clicking a status move into a Choice lock.
- When a dead turn unlocks switching in NORMAL, the matrix decides among all rows. It may still click a move to punish a predicted switch. To keep today's forced 100% swap instead, set `deadTurnForcesSwitch = true`, which removes the MOVE rows in that case (§13 item 18).


### 7.14 Sacking logic

**Goal.** When the AI is in a bad position, it should not just let a valuable active mon faint. If a *less valuable* bench mon can take the hit instead, the AI sacks it. The sack comes in, absorbs the threatened move (usually fainting), and the AI gets a **free replacement** at end of turn (no incoming hit). That preserves the more valuable mon *and* lets the AI bring out the best answer safely.

**Typical situation.** The player's mon threatens a move the AI's active can't survive, and no bench mon is a good safe switch-in. Today the AI stays in and loses its active mon.

**Why the current code never sacks**
- `evaluateSwitchInScore` returns `Integer.MIN_VALUE + 1` when the switch-in faints ("terrible switch"). That rule forbids sacks outright.
- Every mon counts the same in the comparison (`scorePokemon` compares survivability and matchup, not team value).
- Switch candidates are ranked as *answers*, and a weak mon is never an answer.

**Key idea: a sack is not a special move.** It is an ordinary `SWITCH` row (or a `MOVE_THEN_SWITCH` pivot row) whose simulated outcome is "target absorbs the hit and faints, and the fainted side gets a free replacement." When the player attacks:
- **Stay**: the active takes the hit and faints, so we lose `value(active)`, then a free replacement comes in.
- **Sack**: the target takes the hit and faints, so we lose `value(target)`, then a free replacement comes in. The active is still alive and can be that replacement, or wait for a better moment.

```
payoff(sack) - payoff(stay)  ≈  value(active) - value(target) + replacementQualityGain
```

The matrix computes this automatically **if** four conditions hold. The sack module exists to guarantee them:

1. **Mon values are real** (§7.14.1).
2. **Sack candidates are always in the row set** (§7.14.2).
3. **Fainting on switch-in is not punished by a sentinel**, and the post-faint replacement is chosen well and *consistently with the real game* (§7.14.3).
4. **The player's likely action is modeled.** A sack only pays if the player attacks. If the model or equilibrium says they'll switch or set up, the sack row loses in those columns and its weight drops. No separate prediction logic is needed.

#### 7.14.1 Mon values: `computeMonWeights`

Computed once per decision and cached in the root snapshot. **Not** recomputed inside matrix cells (no recursion). Weights are HP-independent (evaluated as if at full HP), because HP enters `eval` separately through `hpFrac` (§7.8). This avoids double counting.

```
computeMonWeights(root) -> { ai: double[], player: double[] }
    for m in aiAlive, q in playerAlive:
        # 1v1 matchup at full HP with base stats/current items/abilities (no stages, no status)
        edge[m][q] = matchupScore( bestFrac(m→q), bestFrac(q→m), fasterAtBase(m,q) ) / 90     # ≈ [-1, +1]

    # how dangerous each opposing mon is to the whole team
    threat[q]  = BASE_T + mean over m of max(0, -edge[m][q])

    # how much of the opposing team a mon handles, weighted by danger
    contribution[m] = Σ_q threat[q] * squash(edge[m][q])

    # bonus for being the ONLY good answer to a dangerous mon
    unique[m] = Σ_q threat[q] * [ m is best answer to q  AND  edge[m][q] - secondBestEdge[q] > GAP ]

    utility[m] = small additive terms: hazard setter/remover (if the matchup makes them useful),
                 status absorber, speed control, screens, Perish/utility users, Healing Wish user, etc.

    raw[m] = BASE_W + contribution[m] + UNIQUE_W * unique[m] + utility[m]
    weights = raw normalized so the mean over the side's alive mons is 1.0, clamped to [0.25, 3.0]
    (player mons use the mirrored formula so `material()` is symmetric)
```

- Optional per-trainer `aceMultiplier` (from trainer data) scales a designated ace for boss trainers.
- Fainted mons are excluded. Weights are recomputed at the start of every decision because the team composition changes as mons faint.
- The **weighted value** of a mon is `weight * hpFrac`. That quantity drives both `material()` in `eval` and the sack candidate rule below.
- Lead selection (§7.15) reuses this function.

#### 7.14.2 Sack candidate generation and row-set rules

```
sackCandidates(root, cfg):
    valueOf(m) = weights.ai[m] * hpFrac(m)
    C = alive, switchable bench mons where valueOf(m) < cfg.sackRatio * valueOf(root.ai.active)     # strictly less valuable than the active
    sort C ascending by valueOf
    return first 2

candidateBench(root, cfg) =
      topN "answers" (cheap matchup vs the player's active, N = cfg.maxAISwitchRows - 2)
    ∪ sackCandidates(root, cfg)
    ∪ any bench mon that is immune or resistant to everything the player's active is known to have
# Only the "answers" part is capped. Sack candidates are never dropped by pruning.
```

- `genAIActions` builds `SWITCH` rows from `candidateBench` when `allowVoluntarySwitch` is true, and `MOVE_THEN_SWITCH` rows (any difficulty) from the same set.
- **Min-gain guard.** Before solving, drop any sack row that doesn't beat the Stay row by at least `SACK_MIN_GAIN` in the *threat columns* (the player's move columns whose damage to our active has KO probability ≥ ~0.3). Without this, a sack could get a small mixed probability in situations where it throws a mon away for nothing.
- **Player-side sacks.** `genPlayerActions` also keeps the player's lowest-value bench mon(s) as switch columns when the AI threatens the player's active. A good human sacks too, and predicting that lets the AI punish it (for example by setting up into the sack).

#### 7.14.3 Replacement quality after a sack

A sack only works if the replacement that comes out is the one the AI planned.

```
aiReplacementHeuristic(state):
    for each alive bench mon r:
        entry = clone r, apply entry hazards and entry abilities (simulateSwitchIn)
        score[r] = matchupScore(entry vs the player's POST-turn active: its HP, stages, status),
                   minus expected hazard damage, plus a small weight for r's future value
    return argmax score[r]
```

- It is non-recursive and cheap (no `decide()` or `evaluateSwitchInScore`).
- The former active mon can be chosen as its own replacement if it is still the best answer.
- **Consistency requirement:** every difficulty already picks the AI's forced replacement with a strong switch-in evaluation. That logic must be **one non-recursive function, `chooseReplacement(side, state)`** (`aiReplacementHeuristic` above is this function), shared by the real battle and the simulator (§13 item 15, test T22). It replaces the recursive `evaluateSwitchInScore` / `chooseSwitchInSlot` path. If the real game used a different rule than the simulator, the sack plan (and every "lose the matchup, then bring the answer" line in NORMAL) would silently break.
- The `Integer.MIN_VALUE + 1` "fainted on switch-in" rule dies with `evaluateSwitchInScore`. A switch-in that faints is now just a cell whose end state has less material and a free replacement.

#### 7.14.4 Classification and reporting

- After solving, label a `SWITCH` row as a **sack** when, over the predicted player distribution, `P(target faints) ≥ 0.5`. Debug/UI line: `[Sack: <target> for <active> | replacement plan: <r> | P(target faints) = 82%]`.
- Populate `simBattleUI.p1Switch/p2Switch` with that reason so sacks are visible in the sim UI.
- Log the sack candidates, their weighted values, and the payoff gap vs Stay for each decision in debug mode.

#### 7.14.5 Difficulty and special cases

- **NORMAL**: no plain switches, so no plain sacks. A pivot move into a low-value teammate can still act as a sack if the pivot is legal and the simulation says it pays (a slower pivot user that gets KO'd first never pivots, and the simulator handles that). Dead-turn and Struggle-only switches use the same candidate set, so a low-value mon can be sacked there too.
- **Perish Song** (`perishCount == 1`): the active faints at end of turn regardless. Switching is allowed at every difficulty, and the matrix decides between preserving the active on the bench and getting one last attack.
- **Trapped** (`!canSwitch`): no switch rows, so no sack.
- **Two mons left**: handled by the same math (`material` and free replacement). No special case.

#### 7.14.6 Worked example (illustrative numbers)

Material units are `100 * hpFrac * weight`. The AI's active is **Ace** (weight 1.6, 100% HP), and the player's mon threatens a move that KOs Ace. Bench: **Answer1** (w 1.2, 100% HP) dies to the move; **Answer2** (w 1.0, 100% HP) survives with 15% HP and is crippled; **Scrub** (w 0.4, 60% HP) dies to the move.

When the player attacks:

| AI row | Result | Material change |
|---|---|---|
| Stay (Ace) | Ace faints; free replacement | about -160 |
| Switch Answer1 | Answer1 faints; free replacement | about -120 |
| Switch Answer2 | Answer2 left at 15%, in a bad spot | about -85, plus a poor position |
| **Switch Scrub (sack)** | Scrub faints; Ace stays alive; free replacement | **about -24** |

When the player switches instead, the sack costs a bit of tempo and little else. So the sack row's weight rises with `P(player attacks)` and falls as the model expects a switch.

### 7.15 Lead selection (EXTREME only)

**Rules.** At battle start the AI examines the player's team and picks its best lead. Both sides choose without knowing the other's pick. This is a one-shot simultaneous game, so the same matrix machinery applies: rows are the AI's leads, columns are the player's leads.

**What exists today.** `Trainer.pickLead(foe)` calls `predictPlayerLeads(foe)` and then `weightedRandomSelection(scores)`. It runs at battle initiation, before the player's lead-select screen, so it cannot peek at the player's choice. It predicts the player's lead as "the player mon with the best average switch-in score against our team", scores each of our mons against those predicted leads, and samples with weight `(shifted score)² + 10`.

**Problems with the current approach**
1. **One-sided prediction.** It assumes the player leads with their best-on-average mon. A player who counter-leads that predicted lead wins. It is not a solution to the simultaneous game.
2. **All-negative bug.** In `pickLead`, only player mons with `likelihood > 0` contribute. If every player mon's average score is ≤ 0, every AI mon ends up with a score of 0.0 and the lead becomes uniformly random.
3. **Candidate sets don't match the UI.** Player candidates skip null and fainted mons but not `Egg`s; AI candidates skip null but not fainted mons. The player can only select alive mons (`!isFainted()`).
4. **Sampling has no game-theoretic meaning.** Squared shifted scores mean a middling lead is chosen far more often than an equilibrium strategy would allow, or less often than it should.
5. **Depends on code being deleted.** Both helpers call `evaluateSwitchInScore(foe, field)` (§3). Migrate them before that overload is removed.
6. Uses `Math.random()` and `System.out` (`Rng` and `Print.debug` needed).

**What is already right (keep)**
- Timing: the AI's lead is computed at battle initiation, before the player selects.
- `if (!foe.playerOwned()) return current;`: AI-vs-AI sim battles keep their fixed leads unless the self-play harness asks otherwise.
- The result is applied by setting `current` (verify whether anything also assumes `team[0]` is the lead).
- **The team-preview and lead-select UI already exists** (`drawFoeTeamPreview`, `drawUserTeamPreview`). The player sees the AI's full team and sets, then picks with `W`, which swaps the chosen mon into `team[0]` and `current`. No new UI is needed.

**Information rule.** The player sees all of the AI's set information (stats, items, moves, abilities) in preview, so the AI may use all of the player's. What it must never use is the player's lead choice. Default `infoMode = FULL`.

**Signature (enforces "no peeking").**

```
Trainer.chooseLead(playerTrainer, cfg, Rng) -> Pokemon
    # Takes the player's TEAM only, never a chosen lead. Must run before the player's selection is committed.

Trainer.pickLead(foe):                     # existing call site stays unchanged
    if (!foe.playerOwned()) return current
    return chooseLead(foe.trainer, cfg, Rng)    # use foe ONLY to reach foe.trainer; never foe as "the player's lead"
```

**Algorithm.**

```
chooseLead(playerTrainer, cfg, Rng):
    aiCands     = alive, non-null AI mons
    playerCands = { p in player team : isSelectableLead(p) }      # ONE shared predicate used by both this AI and the UI's select handler
                                                                   # (alive, non-null, not an Egg)

    # 1. Battle-start state: the REAL current HP / status / PP of both teams (the player usually arrives damaged),
    #    clean field, no hazards
    weights = computeMonWeights(startState)                         # §7.14.1 (also breaks ties)

    # 2. Pairwise lead payoffs (cheap pass)
    for i in aiCands, j in playerCands:
        entry = simulateEntry(i, j)
        L[i][j] = staticLeadValue(entry)

    # 3. Refine the plausible region
    for i in top K_ai rows, j in top K_p columns:                   # K ≈ 3 each
        L[i][j] = gameValue( buildMatrix(entry(i,j), genAIActions, genPlayerActions, cfgLite) )
        # gameValue = solveZeroSum(matrix).value: captures "my lead gets countered, so I pivot",
        # hazard-setting leads, Fake Out / Sash leads, entry-ability leads

    # 4. Solve and sample
    (x, y) = solveZeroSum(L)
    x = shape(x, cfg)
    chosen = sample(x, Rng)
    current = chosen                                                # mirrors the existing behavior
    return chosen

simulateEntry(i, j):
    clone both leads (with real HP/status) on a clean field; enter them in speed order using swapIn(foe, hazards=false)
    (Intimidate, weather/terrain setters, Trace, Anticipation, etc. all trigger)
    return a start-of-turn-1 SimState (nothing has moved yet)

staticLeadValue(entry) = eval(entry) - eval(neutralBaseline)        # activeMatchup after entry effects, weather, stat drops
```

**Design notes**
- **Not counterable.** The choice is a mixed equilibrium strategy, so the player can't reliably counter-lead. If one lead strictly dominates, the AI picks it with near-certainty.
- **Player model.** The default is the structural equilibrium `y`. If the game persists per-player lead history across battles, it can serve as an optional prior (same shrinkage rule as §7.11).
- **Scripted/story trainers.** A `fixedLead` on the trainer bypasses `chooseLead`.
- **Cost.** About 36 static pair evaluations plus about 9 refined sub-matrices, once at battle start (budget ≤ 500 ms).
- **Replaces:** the bodies of `pickLead`, `predictPlayerLeads` and `weightedRandomSelection` (the latter two are deleted; `pickLead` stays as the wrapper).

---

## 8. Difficulty and tuning knobs

### 8.1 What difficulty changes (only two gates)

| Config field | NORMAL | HARD (default) | EXTREME |
|---|---|---|---|
| `allowVoluntarySwitch` | **false** for switching out of unfavorable matchups (pivot moves, Perish-in-1, Struggle-only and dead turns are always allowed, §7.13.1) | true | true |
| `selectLead` | false | false | **true** |

Everything else, including move evaluation, player prediction, the solver, sacking and all tuning knobs below, is identical at every difficulty. See §1.1 for the player-facing definition.

Consequences:
- Sacking (§7.14) is a HARD/EXTREME behavior by construction, since it needs voluntary switching. NORMAL can sack only via a pivot move, or when a dead turn or Struggle-only unlocks switching.
- Lead selection (§7.15) runs once before turn 1 (EXTREME only).
- A regression test (T27) asserts that the configs for NORMAL and HARD differ **only** in `allowVoluntarySwitch`.

### 8.2 Shared tuning knobs (same defaults at every difficulty)

These are developer/trainer-style controls, **not** difficulty levers. Defaults are starting points to tune in Phase 8.

| Knob | Default | Notes |
|---|---|---|
| `considerPlayerSwitches` | true | Player switch columns are always in the matrix |
| `maxPlayerSwitchCols` / `maxAISwitchRows` | 4 / 5 | Caps apply to "answer" candidates; sack candidates are never capped out (§7.14.2) |
| `temperature` | medium | Softness of the quantal-response solver |
| `alpha` | 1.0 until the player model exists, then about 0.7 | Weight of equilibrium vs exploit (§7.11) |
| `minProb` | 0.06 | Cutoff for tiny probabilities |
| `epsilon` | 0 | Deliberate-mistake rate. Keep at 0 unless a specific trainer needs it |
| `useHistoryModel` | false until Phase 6, then true | |
| `infoMode` | FULL | `REVEALED_ONLY` available. FULL everywhere, including lead selection: the player also sees the AI's full sets in team preview |
| `branchBudget` | 3 | |
| `useFullSim` | true | Tier 2 on |
| `deadTurnForcesSwitch` | false | NORMAL only: a dead turn forces a swap instead of unlocking switch rows for the matrix (§7.13.1) |
| `sackRatio` | 0.8 | A sack candidate must be worth less than `sackRatio * value(active)` |
| `SACK_MIN_GAIN` | tuned | Minimum payoff advantage over Stay in threat columns |
| `style` | balanced | Per-trainer `EvalWeights` profile (aggressive / stall / hyper-offense) |

If a specific trainer should feel weaker or stronger, use a `style` profile or a per-trainer override, not a difficulty mode.

---

---

## 9. Special-move coverage checklist

Each row: existing heuristic → how the new system must handle it. A move isn't "done" until its test passes.

| Move / mechanic | Existing handling | New handling |
|---|---|---|
| Counter / Mirror Coat / Metal Burst | `score += foeMax% * k` if not KOing | Simulator: `move()` uses `damageTaken` from the earlier move in the same turn. Damage cell handles it; no heuristic. |
| Destiny Bond, Endure | fixed bonuses | `move()` sets `BONDED`/`ENDURE`; KO-branch logic already respects Endure; Destiny Bond shows up when AI would KO'd. |
| Disable / Torment / Taunt / Encore | hard-coded ± scores | Sim applies the status; `eval` sees the reduced action sets via `forcedTurnPenalty` and the foe's `getValidMoveset()` in `bestRange`. May need an "action-set restriction" term if not visible. |
| Trick / Switcheroo | +25 if trickable | Item swap in sim; `calcRange` sees the new item (Choice lock, etc.). |
| Roar / Whirlwind / Dragon Tail / Red Card / Eject Button | `calcStatBoostScore` | §7.7; value = eval of post-forced-switch state (boost stripping automatically visible). |
| Rapid Spin / Defog / Field Flip | `calcHazardTeamValue × layers` | Sim removes hazards; `hazardPain` term drops. |
| Healing Wish / Lunar Dance | max benefit over bench | Add `eval` term for pending wish effects on side; sacrifice appears as a MOVE row that KOs self. |
| Aromatherapy / Heal Bell | status count × 50 | `benchStatusValue` drops after sim. |
| Protect family (Spiky Shield, etc.) | +30 conditional bonuses | Sim: Protect vs predicted attack = free turn for AI plus residual; contact punishments handled by `move()`. |
| Pivot moves | +25 if useful | `MOVE_THEN_SWITCH` action; ordering in §7.5 (pivot happens before opponent's move if AI is faster). |
| Charge moves (Solar Beam, Skull Bash, etc.), Hyper Beam family | `-30` unless Power Herb | Sim leaves `CHARGING` / `RECHARGE` state; `forcedTurnPenalty` in `eval`. Weather-skip rules must be identical in the sim (they live in `move()`). |
| Fake Out / First Impression | `-50` when unusable | `calcRange.usable=false` → row not generated. |
| Sleep Talk / Snore | +250 when asleep | Sim handles sleep; row is useful only if it acts; `usable` logic in `getValidMoveset` or generation filter. |
| Metronome / random-move users | +25 | Constant expected-value cell (average of `eval` over a small fixed sample of "typical" moves), cached per decision. |
| Hazards (Stealth Rock, Spikes, Toxic Spikes, Sticky Web, Floodlight) | `calcHazardTeamValue` | `hazardPain` term plus hazard rows pruned if `!isHazardUseful`. |
| Screens, weather, terrain, Trick Room | `scoreScreenValue`, `scoreWeatherValue`, ±30 | `fieldValue` duration-weighted term; `calcRange` reflects the effect on damage. |
| Recoil / self-KO moves | -45 if would die | Sim faints the user; `eval` sees the material loss. |
| Setup moves (boost self) | `statChangeIsUseful` | Post-turn `activeMatchup` (thresholds) plus tempo; large payoff in the "player switches" column. |
| Stat drops on foe | same | Same (foe's `bestRange` changes only if it matters). |
| Speed control (para, Icy Wind, Trick Room) | speed heuristics | `iAmFaster` recalculated post-turn in `activeMatchup`. |
| Ability/type change moves | `evaluateAbilityOverwrite`, `evaluateRetype` | `calcRange` sees the new type/ability. Keep an explicit term only for abilities with non-damage value. |
| Sack switch (low-value mon absorbs the hit) | `Integer.MIN_VALUE + 1` for a switch-in that faints, so it was never chosen | Ordinary SWITCH row; `computeMonWeights`, guaranteed sack candidates, free-replacement modeling (§7.14) |
| Perish Song at count 1 | NORMAL rule branch: 100% swap | Switch rows always allowed at `perishCount == 1` (all difficulties); the matrix decides which |
| Lead selection (EXTREME) | none (team order) | §7.15 lead matrix and `chooseLead` |
| Dead turn (nothing does anything to the active foe) | NORMAL rule branch `hasRealAction`, 100% swap (with bug B2) | `deadTurn` (§7.13.1); unlocks switch rows in NORMAL; HARD/EXTREME handled by the matrix |
| Struggle only | 100% swap if it can switch | Forced-action pre-check at every difficulty (§7.13) |
| Choice item lock | `howUseful = 0` for status moves when holding a Choice item; `choiceMove` set in `move()` | `move()` in sim sets the lock; `eval` adds a lock cost when the locked move does nothing vs the active foe and likely switch-ins; `deadTurn` ignores status moves for an unlocked Choice holder |
| Forced replacement after a faint | `chooseSwitchInSlot` / `evaluateSwitchInScore` | Shared `chooseReplacement` (§7.14.3) |

---

## 10. Performance plan

Baseline: about 1.3 s per turn in one observed debug run (nested clone recursion). Target: **≤ 150 ms per decision** at HARD on the dev machine, 0 recursion.

1. Delete nested evaluation (`scorePokemon → analyzeDefensiveResponse → evaluateSwitchInScore → scorePokemon`).
2. One snapshot per decision; per-cell copy-on-write forks of only touched mons.
3. Cache switch-in states per `(side, slot)` per decision. Compute `monWeights` once per decision (at most 36 pairs × 2 directions).
4. Memoize `calcRange` per decision by `(attackerFingerprint, defenderFingerprint, move)`.
5. Prune columns to the plausible top-N switch targets; prune dominated rows.
6. Tier 1 fast path for plain damage cells.
7. Guard `Print.debug` string building behind an `AI_DEBUG` boolean.
8. Log timing per phase of `decide()` in debug mode, so regressions are visible.

Matrix size budget: rows ≤ ~12 (4 moves + ≤5 switches + ≤4 pivot combos), cols ≤ ~9 (4 moves + ≤5 switches).

---

## 11. Test plan

**Unit / scenario tests** (build a small `AITestHarness` that constructs two mons, a field, and calls `decide()` N times with fixed seeds, reporting action frequencies):

| ID | Test | Expectation |
|---|---|---|
| T1 | Damage parity: `calcRange` vs mean of sim `move()` over random mons/moves | Within 1–2% avg; KO prob within tolerance; any mismatch reveals a divergence bug |
| T2 | `endOfTurn` residuals are simulated | AI at 6% HP with poison won't consider "staying and getting healed"; switching a poisoned/low-HP mon in is scored as a loss when poison KOs at end of turn |
| T3 | **Real-state invariance**: fingerprint of real teams, real field, `trainer.current` before/after `decide()` | Identical (must pass with Red Card / Roar / Dragon Tail / pivot moves in movesets) |
| T4 | Determinism: same seed and same state give the same `M` and the same sampled action | Yes |
| T5 | Turn-order parity: simulator vs real battle loop on recorded scenarios | Same HP/status outcomes |
| T6 | Poison/Fighting scenario (§1): AI is Poison/Fighting with one Poison + one Fighting move; player active Ghost, back Steel | Both moves have nonzero probability; frequency of Fighting rises as switch-to-Steel becomes more likely; not 100% Poison |
| T7 | Solver sanity: matching pennies, RPS, dominated row, dominant row | As stated in §7.10 |
| T8 | Liquidation vs Aqua Jet: both kill 1 HP foe, AI faster | Liquidation ≈ 100%. If AI is slower than the foe, or the foe has priority, Aqua Jet gets meaningful weight |
| T9 | SpA boost when active moves already KO and no bench need | Boost row payoff ≈ attack row's (or lower); probability ≈ 0 |
| T10 | SpA boost when a likely switch-in survives currently but not boosted | Boost row payoff rises; the higher the predicted switch chance, the more probability |
| T11 | Defense boost when foe KOs regardless | Row gets no boost-related advantage |
| T12 | Perish counter 1 | Switch row dominates at every difficulty (NORMAL via the Perish exception); no rule beyond the gate |
| T13 | "No move does anything to active foe" (all immune) | HARD/EXTREME: switching rows dominate *unless* the player is likely to switch (then a move into the predicted switch-in appears). NORMAL: no plain switch is available, so it clicks the best available move (or pivots) |
| T14 | Pivot pricing: AI faster with U-turn vs slower with U-turn | Value differs (incoming mon takes vs dodges the hit) |
| T15 | Shed Skin, Magic Reflect (Brick Break), Merciless, Neuroforce, Leech Seed Big Root | Fixed behaviors |
| T16 | Difficulty gate | Over 1000 seeded decisions in positions where the AI has at least one move that does something (not a dead turn), NORMAL never selects a plain SWITCH row (except at `perishCount == 1`); pivot moves stay available; HARD selects switches when the matrix favors them; NORMAL still predicts player pivots (T6 passes in NORMAL) |
| T17 | Perish exception in NORMAL | At `perishCount == 1` with a healthy bench, NORMAL switches; at count 2 it does not |
| T18 | Sack scenario | HARD: active Ace (high weight) is threatened with a KO; bench has one low-weight mon that would faint and no safe answer; the sack row gets the majority of probability. NORMAL: never plain-switches |
| T19 | Sack needs a value gap | Same position, but the candidate's weighted value ≥ 0.8× the active's: sack probability ≈ 0; the AI stays in |
| T20 | No sack without a threat | Player's active can't KO or seriously threaten: Stay dominates; sack probability ≈ 0 |
| T21 | Sack vs predicted player switch | With a player model that expects a switch, sack probability falls sharply vs the expected-attack case |
| T22 | Replacement consistency | After a simulated sack faints, the replacement the real game picks equals `aiReplacementHeuristic`'s choice |
| T23 | Lead no-peek | Same seed and teams, different player leads: identical AI lead; `chooseLead` takes no player-lead argument |
| T24 | Lead not counterable | Team where one lead beats most of the player's team but is countered by one player mon: distribution isn't degenerate (no lead above about 80%); when one lead strictly dominates, it is picked almost always |
| T25 | Lead entry effects | An Intimidate/weather-setting lead scores differently from the static baseline once entry effects apply |
| T26 | Mon weights | The only good answer to the player's biggest threat outweighs a mon that answers nothing; weights average 1.0 per side; fainted mons excluded; HP does not change a weight |
| T27 | Config diff | NORMAL and HARD configs differ only in `allowVoluntarySwitch`; EXTREME differs from HARD only in `selectLead` |
| T28 | Dead turn unlocks switching in NORMAL | Choice-Band Earthquake user vs a Flying-type foe (also: all moves immune; Taunted with only useless status moves): NORMAL's action set includes SWITCH rows and it switches (or the matrix decides, per `deadTurnForcesSwitch`). Same position with one move that does something: no SWITCH rows |
| T29 | Struggle-only swap | At every difficulty, with only Struggle available and `canSwitch`: it swaps and no Struggle row exists; if trapped, Struggle |
| T30 | NORMAL keeps fighting in a bad matchup | AI mon is threatened or outclassed but has a working move: NORMAL stays (no SWITCH rows); after it faints, the replacement is chosen by `chooseReplacement`, the same function HARD uses |
| T31 | Choice-lock into a status move | Unlocked Choice-item holder: `eval` penalizes locking into a status move, and `deadTurn` doesn't count it as a real action |
| T32 | Lead candidate sets | The AI's lead candidates are its alive mons; the player's columns are exactly the mons the preview UI lets the player select (alive, not eggs), via the shared `isSelectableLead` |
| T33 | Lead uses real battle-start state | A player team arriving damaged/statused changes lead payoffs; regression test for the old all-negative-scores bug in `pickLead` (uniformly random lead) |

**Self-play harness** (Phase 0): in `SIM_BATTLE_STATE`, run K battles (e.g. 200) legacy-vs-new with fixed seeds and a team pool; record win rate, average turns, average think time, and crashes. Every phase after Phase 3 must not regress win rate vs the previous phase.

---

## 12. Implementation phases

Each phase lists tasks, the acceptance criteria, and what to upload.

### Phase 0: Baseline, harness, persistent bug fixes
Tasks
- Introduce `interface TrainerAI { MoveDecision decide(...) }` with `LegacyAI` (existing `bestMove2` logic untouched) so old and new can run side-by-side; `bestMove2` dispatches by a flag.
- Introduce `Rng` and replace `Math.random()` / `new Random()` in AI and battle code (§7.1). This is what makes self-play reproducible.
- Build the self-play tournament runner (§11).
- Build parity test T1 skeleton.
- Fix persistent bugs B3, B4, B6, B7, B9, B11, B12 (and B10 partially: document the shared-trainer risk; full fix is in Phase 2).
Acceptance: harness runs 200 battles unattended and prints win rate / turns / time; T1 reports current mismatches; bug fixes each have a scenario test (T15).
Upload: `Pokemon.java`, `Trainer.java`, sim battle state / `SimBattleUI` and how a sim battle is started, `Player.java` (recorders), `Task.java`.

### Phase 1: Deterministic primitives
Tasks
- Refactor `calcWithTypes` into shared `computeDamage(..., RollMode)`; implement `calcRange` and `DamageRange` (§7.3).
- Remove sentinel ints in new code (`usable`, `immune`).
- Add `SimContext` (depth counter) and `SimPolicy`; make `move()`, `endOfTurn()`, `swapIn()` respect it (no Tasks, no player stat recording, policy-driven randomness).
Acceptance: T1 passes within tolerance; `move()` under `SimPolicy` is deterministic; Tasks are not created in sim.
Upload: `Pokemon.java`, `Task.java`, `Move.java`, `Ability` enum (as needed for `getEffectiveAccuracy` and `getNumHits`).

### Phase 2: SimState and simulateTurn (moves + switches only)
Tasks
- `Trainer.simShell`, `SimState.snapshot/fork/fingerprint` (§7.2). Remove the `oldCloned` hack in `swapRandom` (B10).
- `simulateTurn` with switches, hazards, priority/speed order, `move()` in sim mode, `endOfTurn`, faint + replacement heuristics, forced switches by expected value, branch policy (accuracy, KO-roll, speed ties).
- Extract or mirror the real end-of-turn loop; T5.
Acceptance: T2, T3, T5, T14; simulation of 1000 random cells produces no exceptions and leaves real state fingerprint unchanged.
Upload: `Trainer.java`, `Field.java`, the real battle turn loop (`BattleUI` / wherever turns resolve), `Pokemon.fullClone()`.

### Phase 3: `eval`, matrix, solver, sampling (NORMAL and HARD behind a flag)
Tasks
- `eval` (§7.8) with placeholder weights; adapt `matchupScore` inputs.
- `genAIActions`, `genPlayerActions`, pruning, `buildMatrix`, `solveZeroSum`, `shape`, `sample`, `toMoveDecision`, UI reporting.
- Difficulty gate in action generation: `switchRowsAllowed` (§7.13.1). NORMAL removes plain SWITCH rows unless a pivot move, Perish-in-1, Struggle-only or a dead turn applies. NORMAL and HARD both run the new engine.
- `computeMonWeights` v1 (§7.14.1) feeding `material()`, plus `sackCandidates` / `candidateBench` (§7.14.2).
- Legacy code stays available behind the flag until Phase 6.
Acceptance: T4, T6, T7, T8, T12, T13, T16, T17, T28, T29, T30, T31; win rate of the new engine vs legacy ≥ legacy on both NORMAL and HARD; think time within budget.
Upload: sim battle UI hooks (`p1Moves`, `p1Switch` usage), `MoveDecision`, whatever prints probabilities in `NPC_Mine`.

### Phase 4: Full move coverage; delete effect-scoring code
Tasks
- Work through §9 checklist; add `eval` terms where the simulator alone can't express value (wish effects, forced-turn penalties, duration-weighted field effects).
- Delete `analyzeMoveEffect`, `EffectChange`, `scoreEffectUsefulness`, `statChangeIsUseful`, `scoreMove`, `scorePokemon`, `analyzeDefensiveResponse`, `applyAggression`, etc.
- Before deleting any method, grep for callers outside `bestMove2`. Known: `pickLead` / `predictPlayerLeads` call the convenience overload `evaluateSwitchInScore(foe, field)`. Keep a thin non-recursive replacement until Phase 7.
- Add secondary-effect branching where it changes decisions.
Acceptance: T9, T10, T11; every §9 row has a passing scenario; legacy scoring code deleted from the new path.
Upload: files as requested per move discovered.

### Phase 5: Sacking logic
Tasks
- Finalize `computeMonWeights` (§7.14.1): `unique` (only-answer bonus), `utility` terms, optional `aceMultiplier`.
- Finalize `sackCandidates` / `candidateBench` (§7.14.2), the `SACK_MIN_GAIN` guard, and player-side sack columns.
- Replacement consistency (§7.14.3): make the real battle's forced-replacement logic and the simulator share one non-recursive `chooseReplacement`.
- Classification and reporting (§7.14.4): sack label, replacement plan, UI reason string.
- Confirm no sentinel rules remain that penalize a switch-in fainting.
Acceptance: T18, T19, T20, T21, T22, T26; in self-play, HARD with sacking beats HARD with sack candidates disabled (measure it); no sack occurs when the active isn't threatened.
Upload: the battle code that picks the AI's next mon after a faint, trainer data (for `aceMultiplier`), sim UI switch-reason display.

### Phase 6: Player model, information model, legacy removal
Tasks
- `PlayerModel` (history buckets, shrinkage), `infoMode`, `alpha`/`temperature`/`epsilon` wiring (§7.11, §8.2).
- Delete the legacy NORMAL rule branch and the non-NORMAL switch branch. NORMAL is now HARD with plain switching gated by `switchRowsAllowed` (§1.1, §7.13.1). Verify pivot moves, Perish-in-1, Struggle-only and dead-turn switching still work in NORMAL.
- Trainer style profiles (`EvalWeights`).
Acceptance: T16, T17 and T27 pass; no deterministic 100% rules remain except forced actions and the Perish exception; measurable exploitation of a scripted "always switch after being hit" opponent; in self-play, HARD beats NORMAL (voluntary switching and sacking are worth something).
Upload: `Player.java` recorders; trainer definitions where difficulty/style is set; where the difficulty constants are defined.

### Phase 7: EXTREME lead selection
Tasks
- Replace the internals of `Trainer.pickLead` with `chooseLead(playerTrainer, cfg, Rng)` (§7.15). Keep `pickLead(foe)` as a thin wrapper so call sites don't change. Delete `predictPlayerLeads` and `weightedRandomSelection`.
- Migrate off `evaluateSwitchInScore(foe, field)`, which the old lead code called.
- Add one shared `isSelectableLead(Pokemon)` predicate used by both the preview UI's select handler and the AI's candidate sets (alive, non-null, not an Egg).
- `simulateEntry`, static and refined lead matrices using the real current HP/status of both teams.
- Gate on `selectLead`; honor `fixedLead`; keep `if (!foe.playerOwned()) return current`.
- No new UI: the team-preview and lead-select screens already exist. Only confirm the AI's lead is computed before the player's selection is committed.
Acceptance: T23, T24, T25, T32, T33; the AI's lead is computed independently of the player's selection; decision time within budget; non-EXTREME battles are unchanged.
Upload: `Trainer.java` (`pickLead` and its callers), the battle-initiation code that calls `pickLead`, how `Trainer` orders its team and sets `current`, and the preview UI code (already shared).

### Phase 8: Tuning, performance, cleanup
Tasks
- Tune `EvalWeights`, `matchupScore` constants, `computeMonWeights` parameters (`BASE_W`, `UNIQUE_W`, `GAP`), `sackRatio`, `SACK_MIN_GAIN`, and the lead-refinement `K` values via self-play (coordinate ascent or CMA-ES over win rate).
- Tier 0/1 fast paths, caching, profiling to reach ≤ 150 ms per decision.
- Remove dead code; update the AI guide document.
Acceptance: performance target met; win rate ≥ Phase 6/7 baselines; no state-invariance failures across 10k simulated cells.

---

## 13. Open questions / code I need to see

Ask for these when the relevant phase starts. Do not guess.

1. `Trainer` class: fields (`team`, `current`, per-side effect lists), `canSwitch`, `hasValidMembers`, `swap`, and how hazards/screens are stored (per mon via `getFieldEffects()`, or on the trainer).
2. `Pokemon.fullClone()` and `cloned` semantics; exactly which fields are deep-copied.
3. `Field` class: `clone()`, `fieldEffects`, `weather/terrain` turn counters, `turns`, `getHazards`, `getLayers`.
4. `Task` class: does `createTask == false` make `Task.createTask/insertTask/addTask` all no-ops? (`move()` uses `insertTask` with indices into `gp.battleUI.tasks`.)
5. `calc(..., mode)`: exact semantics of `mode` (0/1/-1) and the roll formula.
6. `Player` class: what `recordTurn`, `recordSwitchIn`, `recordDamageDealt` store.
7. The real battle turn loop: order of switch/move/end-of-turn, where weather/terrain/Future Sight/Wish ticks happen, where `Status.SWITCHING`/`TEMP_SWITCHING`/`SWAP` are consumed.
8. `getFaster(foe, myPriority, foePriority, field)` full rules (Trick Room, paralysis, Tailwind-like effects, ties).
9. `getValidMoveset()`: exactly which restrictions it applies (PP, Disable, Taunt, Torment, Choice lock, Heal Block, Mute, Encore).
10. `MoveDecision` constructors and how `first`/`pendingSwitch` are consumed by the battle flow.
11. `Ability` enum `useful` codes (used by `evaluateAbilityOverwrite`).
12. How a `SIM_BATTLE_STATE` battle is created and torn down (needed for the self-play harness).
13. Are `headbuttCrit`, `tailCrit`, `spaceEat` static or instance counters?
14. Where difficulty is stored and read (`Player.NORMAL` exists; how are HARD and EXTREME represented?), and every place difficulty currently changes behavior. There should end up being exactly two: the switch gate and the lead gate.
15. The function the real battle uses to pick the AI's forced replacement after a faint (you described it as the strong switch-in AI used at every difficulty; probably `chooseSwitchInSlot` / `evaluateSwitchInScore`). It must become the shared non-recursive `chooseReplacement` (§7.14.3).
16. Battle start: where `pickLead` is called and whether it is gated to EXTREME today; whether anything assumes the AI's `team[0]` is the lead (the existing code only sets `current`). The team-preview and lead-select UI already exists (`drawFoeTeamPreview` / `drawUserTeamPreview`; the player's pick swaps into `team[0]` and `current`).
17. Trainer data: is there (or should there be) a per-trainer `fixedLead` and `aceMultiplier`?
18. Decision (recommended default): when a dead turn unlocks switching in NORMAL, should the matrix decide (it may still click a move to punish a predicted switch), or force a 100% swap as today (`deadTurnForcesSwitch`)? Struggle-only swapping is confirmed to stay at every difficulty.

---

## 14. Risks and mitigations

| Risk | Mitigation |
|---|---|
| `eval` mis-values a mechanic, so the matrix optimizes the wrong thing | Self-play A/B after every phase; scenario tests per §9; keep `eval` terms few and inspectable; log per-term contribution in debug |
| Simulator diverges from the real engine | Reuse `move()`/`endOfTurn()`/`swapIn()` directly; shared `computeDamage`; T1 and T5 parity tests |
| Shared-trainer mutation corrupts real battles | Trainer shells (§7.2); T3 invariance test in CI on every phase |
| Performance regression | Per-phase timing logs; matrix size caps; caches; Tier 1 path |
| Exploiting a wrong player model | `alpha` shrinkage, minimum observations, bounded exploit weight |
| Solver output feels "too random" or "too perfect" | Shared tuning knobs (`temperature`, `minProb`, `epsilon`, `alpha`) and per-trainer `style` profiles, not difficulty modes; tune with player feedback |
| Hidden-info fairness (AI knowing the full moveset and team) | `infoMode` knob: default FULL at every difficulty; `REVEALED_ONLY` available (§8.2, §7.15) |
| Scope creep | One phase per session; §12 acceptance criteria are the definition of done |
| Sacking throws away mons for nothing | `computeMonWeights` (real values), `sackRatio`, `SACK_MIN_GAIN` guard, tests T19–T21, self-play with vs without sacking |
| Sack plan breaks because the real game picks a different replacement | One shared replacement function for simulator and battle (§7.14.3), test T22 |
| Lead selection peeks at the player's choice, or can be counter-picked | No-peek signature, mixed-strategy lead matrix, tests T23–T24 |
| Deleting `evaluateSwitchInScore` breaks `pickLead` / `predictPlayerLeads` (and possibly the real replacement chooser) | Grep external callers before every deletion (Phase 4); keep a thin non-recursive replacement until Phase 7 |
| Dead-turn false positives or negatives (unlocking or blocking switches wrongly in NORMAL) | `deadTurn` checks the active foe only; `DEAD_EPS` tuned; tests T28, T31 |

---

## 15. Ready-to-paste prompts

### 15.1 Session bootstrap (start of every session)

> I'm implementing the Trainer AI overhaul described in the attached `TRAINER_AI_OVERHAUL_SPEC.md`. I've also attached `Pokemon.java` and: `[list of additional files]`.
> We are on **Phase N**. Please:
> 1. Restate the Phase N tasks and acceptance criteria in your own words and list anything you need to see that isn't attached (see §13).
> 2. Propose the exact list of methods/classes you'll add, change, or delete, before writing any code.
> 3. Then implement it method by method, following §0's rules. Give me tests (from §11) and manual verification steps at the end.
> Don't move on to the next phase.

### 15.2 Phase 0

> Phase 0: baseline and harness. Implement `TrainerAI`/`LegacyAI` dispatch, the `Rng` class and its call-site replacement plan (list every `Math.random()`/`new Random()` you'd change, grouped by file), the self-play runner design, and the persistent bug fixes B3, B4, B6, B7, B9, B11, B12 with a scenario test each. Tell me which files you need to see to do the self-play runner.

### 15.3 Phase 1

> Phase 1: deterministic primitives. Refactor `calcWithTypes` into a shared `computeDamage` used by both the existing method and a new `calcRange`. Add `SimContext`/`SimPolicy` and make `move()`, `endOfTurn()`, `swapIn()` respect them. Show me the parity test (T1) design and the list of mismatches you expect to find.

### 15.4 Phase 2

> Phase 2: `SimState`, trainer shells and `simulateTurn` (moves and switches only). Before coding, tell me what you need to see of `Trainer`, `Field`, `fullClone()`, and the real battle turn loop. Then implement `snapshot/fork/fingerprint`, `simulateTurn` (§7.5) with hazards, end-of-turn, replacements and the branch policy, and the invariance test T3.

### 15.5 Phase 3

> Phase 3: `eval`, payoff matrix, solver and sampling behind an `AI_V2` flag for **NORMAL and HARD** (NORMAL = HARD with plain switching gated by `switchRowsAllowed`, §1.1 and §7.13.1). Implement §7.4–7.12, the difficulty gate and `deadTurn` in action generation, `computeMonWeights` v1 and sack-candidate generation (§7.14.1–7.14.2), and UI reporting. Give me the debug output format so I can read matrices and strategies in the console. Verify T4, T6, T7, T8, T12, T13, T16, T17, T28–T31.

### 15.6 Phase 4

> Phase 4: coverage and deletion. Walk the §9 checklist row by row: tell me which rows the simulator already handles, which need an `eval` term, and which need simulator changes. Implement in that order, then list the legacy methods now safe to delete.

### 15.7 Phase 5

> Phase 5: sacking logic (§7.14). Before coding, tell me how the real battle picks the AI's forced replacement after a faint (I'll upload that code) and whether it can share the simulator's `aiReplacementHeuristic`. Then finalize `computeMonWeights`, `sackCandidates`/`candidateBench`, the min-gain guard, player-side sack columns, and sack reporting. Verify T18–T22 and T26, and add a self-play comparison of HARD with vs without sack candidates.

### 15.8 Phase 6

> Phase 6: player model, information model and legacy removal. Implement §7.11 and §8.2. Show me the situation-bucket design and what `Player` needs to record. Delete the legacy NORMAL/non-NORMAL branches, confirm that pivot moves and Perish-in-1 still work in NORMAL, and add the T27 config-diff test. Include a scripted-opponent test proving the exploit works and that `alpha` shrinkage protects against a wrong read.

### 15.9 Phase 7

> Phase 7: EXTREME lead selection (§7.15). I'll upload `Trainer.java` (`pickLead`, `predictPlayerLeads`, `weightedRandomSelection`), the battle-initiation code that calls `pickLead`, and the preview UI. Tell me what else you need, then implement `chooseLead` with the no-peeking signature, the shared `isSelectableLead` predicate, `simulateEntry`, and the static and refined lead matrices using the real battle-start HP/status. Keep `pickLead(foe)` as a wrapper and honor `fixedLead`. No new UI is needed. Verify T23–T25, T32, T33.

### 15.10 Phase 8

> Phase 8: tuning and performance. Propose the self-play tuning procedure for `EvalWeights`, `matchupScore` constants, `computeMonWeights` parameters, `sackRatio`, `SACK_MIN_GAIN` and the lead-refinement K values. Profile `decide()` phase by phase and implement the Tier 0/1 fast paths and caches needed to hit the time budget.
