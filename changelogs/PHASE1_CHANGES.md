# Phase 1 change list (deterministic primitives)

Every change is tied to spec 7.1 / 7.3 / 12-Phase-1, a bug ID, or a test.

## New files (package pokemon)
| File | Purpose |
|---|---|
| `RollMode` | RANDOM / MIN / MAX / AVG roll modes; `roll(i)` = the 16 rolls |
| `DamageMode` (pkg-private) | Splits the old int `mode` into: engine rules, roll, crit, Chromo Beam, accuracy roll |
| `DamageResult` (pkg-private) | `computeDamage` result: `Kind` (OK / UNUSABLE / IMMUNE / NO_DAMAGE / ACC_FAIL) plus the legacy sentinel pair |
| `DamageRange` | Public result of `calcRange`: flags instead of sentinels, per-hit min/avg/max, crit mixture, accuracy, hits, `koProb`, `expected`, `expectedCapped` |
| `SimPolicy` | Accuracy / secondary / damage roll / seed policy (immutable) |
| `SimContext` | Depth-counter scope (`try (SimContext.Scope s = SimContext.enter(policy))`) |
| `SimContextTest`, `Phase1Tests` | Standalone and in-game tests |

## Pokemon.java (method level)
| Method | Action | Why |
|---|---|---|
| `calc(a, d, bp, level, mode)` | Replace | In a sim: policy roll, no draw. Otherwise unchanged (still draws even for mode +-1, so legacy RNG consumption is identical) |
| `calcRoll(a, d, bp, level, roll)` | Add | The formula at an exact roll; `calc` and `computeDamage` both end here |
| `calcWithTypes(..., mode, crit, ..., checkAcc)` | Replace by a 2-line wrapper | Calls `computeDamage(DamageMode.legacy(...))`, returns the same `Pair` |
| `computeDamage(foe, move, first, field, DamageMode)` | Add (the old body, edited in place) | One implementation for legacy and `calcRange` (no copy-paste; B6 / B7 cannot drift again) |
| `applyCritStage` | Add; `move()` and `computeDamage` both call it | Shared crit stage (status, Super Luck, Scope Lens, Merciless) |
| `critProbability(stage, script)` | Add (static) | Exactly what `critCheck` rolls |
| `calcRange`, `rangeAccuracy`, `rollDamages` | Add | Spec 7.3 |
| `hit`, `critCheck`, `checkSecondary` | Edit: one line each | Policy hooks; nothing changes outside a SimContext |
| `recordsStats()` | Add (`!cloned && !SimContext.active()`) | Single predicate for "this is real" |
| `move`, `swapIn`, `faint`, `heal`, `takeFutureSight`, `awardExp`, `announceMoveText` | Edit guards (see below) | No player recording, counters, pokedex / save-scum, UI hooks or task lists in a sim |
| `endOfTurn` | No direct edit | Its only outside effects are Task calls and RNG draws, both handled centrally |

Guard sites: 7 recorder guards (`!this.cloned` -> `recordsStats()`), `field.misses / crits / superEffective / knockouts` (5 sites, incl. `takeFutureSight`),
`faint` (`setBattled`, `gp.saveScum`, two `pokedex[..] = 2`), `swapIn` (`pokedex[this.id]`), the move-announcement text that reads `gp.simBattleUI.p1Moves` / `p2Moves` (gated so a simulated move never touches those sim-UI fields), `heal()` (early-return before it reaches `battleUI.tasks` / `simBattleUI.tasks`), `awardExp`.

## Task.java
`addTask` (both overloads), `addSwapInTask`, `addSwapOutTask`, `setTask`, `getTask`, `insertTask`, `addMoveAnimTask`, `addProtectAnimTask`:
return early (or a detached Task where the caller uses the return value) when `SimContext.active()`. `Pokemon.createTask` is untouched, so the legacy AI's toggling still works.

## Move.java
`getNumHits` rewritten on top of new `hitProbabilities` (single source of truth) and `expectedHits`. Was `Math.random()` (not seedable; a Phase 0 leftover).
Same 35/35/15/15 and 50/50 (Loaded Dice) distribution; deterministic in a sim.

## Rng.java
`pushIsolated(seed)` / `popIsolated()` / `isIsolated()` / `isolatedAccesses()`: a sim's unpoliced draws never touch the real stream. `asRandom()` returns the isolated stream while a scope is open (fetch at point of use).

## Behavior changes visible to the LEGACY AI (intentional)
1. Crit stage in `calcWithTypes` now includes Super Luck and Scope Lens (it already did in `move()`).
2. Everything else is bit-for-bit: same sentinels, same RNG consumption.

## Expected T1 residuals (found by reading calcWithTypes against move())
| Item | Status |
|---|---|
| Scope Lens / Super Luck crit stage missing from calc | Fixed (shared `applyCritStage`) |
| Anticipation and resist berries applied only in estimate modes | calcRange applies them (matches move()) |
| calcWithTypes mode 0 never crits at stage 0 (`critChance >= 1`), move() does (6%) | Legacy mode 0 gap stays; calcRange uses move()'s table |
| Protean: calc always grants STAB, move() does not | By design (prediction); may show as a gap for Protean mons |
| Metronome item: calc predicts `(metronome + 1) * 0.2`, move() uses the updated counter | By design |
| Counter / Mirror Coat / Metal Burst: calc returns 0 (damage comes from `damageTaken`) | calcRange reports NO_DAMAGE; Phase 2 simulateTurn handles them |
| Explosion / Future Sight coin-flips inside calcWithTypes (AI noise) | Removed from calcRange |
| Multi-hit koProb treats the hits as fully correlated | Documented approximation, Phase 4 |

## Not in Phase 1 (deliberately)
Trainer shells, SimState, simulateTurn, `swapRandom` / B10, secondary-effect branching, hazards. Legacy `simulateSwitchIn` / `analyzeMoveEffect` still use `createTask` and do not enter a SimContext.
