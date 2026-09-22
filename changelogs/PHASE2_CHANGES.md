# Phase 2 change list (trainer shells, SimState, simulateTurn)

Every change is tied to spec 7.2 / 7.5 / 7.6 / 7.7 / 12-Phase-2, a bug ID, or a test.

## New files (package pokemon)
| File | Purpose |
|---|---|
| `ActionKind` | `enum { MOVE, SWITCH, MOVE_THEN_SWITCH }` |
| `Action` | Immutable row/column/cell input: `kind`, `move`, `slot` (0-based team index) |
| `SideState` | Wraps a shell `Trainer`; `active()`, `bench()`, `slot(i)`, `forkTouched(...)` |
| `Branch` | `{prob, state, policy, likelyHit}` — one outcome of a matrix cell |
| `SimState` | `{field, ai, player, turn}`; `snapshot`, `fork`, `fingerprint` |
| `AIConfig` | `branchBudget` only this phase; every other field arrives with the phase that first reads it |
| `BattleSimulator` | `simulateTurn` and all its helpers (spec 7.5-7.7) |
| `Phase2Tests` | `fingerprintReal`, `runInvarianceCheck` (T3), `runSmokeTest` (1000-cell crash check) |

## Trainer.java
| Where | Change | Ties to |
|---|---|---|
| new `simShell()` | Full-team shell: every slot cloned, all pointed at one new shell Trainer | §7.2 |
| new `simShell(Set<Integer> touchedSlots)` | Copy-on-write shell: touched slots + the active slot cloned, the rest shared with the source | §7.2 |
| `swapRandom` | Deleted the `oldCloned` shortcut that repointed `current` directly instead of doing a full `swap()` | B10 |
| `swapRandom` | Now checks `SimContext.consumeForcedSwitch()` before drawing, so the simulator can steer a specific pick instead of getting one arbitrary draw from the isolated stream | §7.7 |

B10 is fixed *because* of the shells: a simulated caller now always has its own `team[]`/`current` to mutate, so the old "don't really swap, this is just AI analysis on a shared trainer" shortcut is no longer needed or correct.

## SimContext.java
| Where | Change | Ties to |
|---|---|---|
| new `forceNextSwitch(int teamIndex)` / `consumeForcedSwitch()` | Static hook, consumed-and-cleared by the next `swapRandom` call | §7.7 |

Needed because Whirlwind, Roar, Dragon Tail, Circle Throw, and Red Card all call `Trainer.swapRandom` **synchronously inside `move()`** (not deferred via `Status.SWITCHING` the way U-turn/Eject Button are), drawing from the isolated `Rng` stream. Branching over "every bench mon this move could send out" has to happen *before* `moveInit` runs, once per candidate, each nudged to a specific outcome — there's no result to branch on afterward.

## No changes to Pokemon.java or Field.java this phase
`move()`, `moveInit()`, `swapIn()`, `endOfTurn()`, `calcRange()`, `getFaster()`, `Field.clone()`, `Field.endOfTurn()` are all called as-is. `Pokemon.field`/`Pokemon.gp` being `public static` (not per-instance) is why `BattleSimulator.simulateTurn` saves and restores `Pokemon.field` around the whole turn, the same discipline as B9's `createTask` save/restore.

## `simulateTurn` (spec 7.5), in order
1. **Fork** the root `SimState` for this cell (`SimState.fork`), swap `Pokemon.field` to the clone.
2. **Pre-move switches** — plain `SWITCH` actions only; pivot moves switch after their move, not here. If both sides chose a plain switch, the faster outgoing mon enters first (`BattleUI.java:1763-1887` ordering, matters for Intimidate).
3. **Moves**, in priority/speed order via the real `getFaster`. For each mover:
   - `calcRange` gives accuracy, `koProb`, and whether the move deals damage at all (status moves vs. Counter-family vs. normal damage).
   - **Accuracy branching**: split hit/miss when `0.05 < accuracy < 0.95`.
   - **KO-roll branching**: for damaging hits, split KO'd/survives when `0.05 < koProb < 0.95` (forced via `SimPolicy.withDamageRoll(1.0 / 0.85)`, since crits are already excluded under `SimContext` and tracked separately in `DamageRange.critProb`).
   - **Random-forced-switch fan-out** (§7.7): for a "likely hit" branch of Whirlwind/Roar (status, no damage) or Dragon Tail/Circle Throw (damaging) against a non-rooted, trainer-owned defender with 2+ valid bench mons, or of any damaging hit against a Red-Card-holding defender with 2+ valid bench mons on the *attacker's* side — fan out into one branch per eligible candidate, weighted `1/N`, using `SimContext.forceNextSwitch` to steer each run.
   - **Pivot self-switch**: `Status.TEMP_SWITCHING` set on the attacker before `moveInit` (same mechanism the legacy AI already used to encode "pivot then switch to slot X"); `move()`'s own U-turn/Volt Switch/Flip Turn handling turns it into `Status.SWITCHING` with the same slot, which is then resolved into an actual `swapOut2`.
   - **Deferred self-chosen replacement**: Eject Button / Slipstream leave `Status.SWITCHING` on the defender with no encoded slot — resolved with the same replacement heuristic as a faint, since the affected trainer is picking their own mon, not the attacker.
4. **End of turn**: real `endOfTurn()` on both sides in speed order, then `Field.endOfTurn()` — skipped entirely if either active already fainted, matching `BattleUI.java:1885-1887`.
5. **Replacement**: any side left with a fainted active gets `cheapReplacementSlot` — see below.
6. **Merge**: branches beyond `cfg.branchBudget` are folded into the lowest-probability one.

## Known simplifications (flagged in code, not silent)
| Item | Status |
|---|---|
| Red Card eligibility omits the `!sheer` guard (Sheer Force happening to suppress Red Card on the same hit as a suppressed secondary effect) | Documented gap; rare interaction, low priority |
| Speed ties are not explicitly branched (still resolved by `getFaster`'s own `Rng.asRandom()` draw, deterministic per isolated stream) | Deferred by request |
| `cheapReplacementSlot` (type-matchup + HP, non-recursive) stands in for `aiReplacementHeuristic`/`playerReplacementHeuristic` | Placeholder; Phase 5 unifies it with the real battle's forced-replacement path (`Trainer.getNext2`/`evaluateSwitchInScore`) into one `chooseReplacement()` |
| Multi-hit moves use `calcRange`'s existing "hits fully correlated" approximation for `koProb` | Inherited from Phase 1, documented there as a Phase 4 follow-up |

## Behaviour differences from Phase 1 (intentional)
* `Trainer.swapRandom` no longer takes the `oldCloned` shortcut for any cloned Pokemon — B10 is closed. A simulated random switch now goes through the same `swap()` path (Task no-ops aside) as a real one.
* Simulated Whirlwind/Roar/Dragon Tail/Circle Throw/Red Card no longer collapse to one arbitrary outcome from the isolated stream — `simulateTurn` explores every eligible target, weighted uniformly, matching `Trainer.swapRandom`'s real (uniform) distribution.

## Running the tests

    Phase2Tests.runSmokeTest(aiMon, playerMon, 1000);   // no exceptions across 1000 random cells
    Phase2Tests.runInvarianceCheck(aiMon, playerMon, aiActions, playerActions, 1000); // T3

`aiActions`/`playerActions` for the invariance check should include at least one switch and one pivot move per side to exercise the replacement and pivot paths; full legal-action generation (`genAIActions`/`genPlayerActions`) is Phase 3.

## Not in Phase 2 (deliberately)
`eval`, the payoff matrix, the solver, `switchRowsAllowed`, lead selection, secondary-effect branching (poison/burn/paralysis chance, flinch, stat drops) beyond what accuracy/KO already covers, speed-tie branching, and the real `chooseReplacement` unification. All Phase 3+.
