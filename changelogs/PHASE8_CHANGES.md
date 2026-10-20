# Phase 8: performance, tuning, and the bugs self-play found

Status: applied and tested. Phase 3, 5, 6 and 8 suites pass, and the 10,000-trial invariance check passes (20 matchups x 500 trials, T3).
Final defaults: `D_SWITCH_TAX = 25` (waived while the foe threatens a KO), `D_LAST_DITCH_BONUS = 20`. All other tunables are at their original values.

## 1. Performance

| Item | Result |
|---|---|
| `Pokemon.clone()` | 0.1-0.8 us (was milliseconds: the constructor decoded a PNG and drew about 9 `Rng` numbers per clone) |
| Decision time (HARD, random 6v6) | avg 4-10 ms, max about 150 ms with debug flags on |
| EXTREME lead selection, 6v6 | 135 ms (static pass 16 ms, refinement 86 ms); budget was 500 ms |
| Cache hit rates | edge cache 98%, range cache 80% |
| Cache VERIFY | 83,096 hits re-checked, 0 mismatches; cold == warm; determinism replays identical |

Changes:
- `Pokemon(boolean fieldCopyOnly)` constructor: `clone()` no longer runs `setSprites()` or draws from `Rng`.
- `SimCache`: content-hash edge cache (no HP/status/stages in the key) and range cache (full key), both bounded at 32k entries. VERIFY mode recomputes every hit and compares. `SimCache.clear()` runs at the start of every self-play battle.
- `MonWeights.duel` computes both directions of a pair from one pair of best-range calls. Baselines are built lazily, once per mon.
- `Evaluator.bestRange` goes through `SimCache`; `bestRangeRaw` is the uncached computation.
- `BattleSimulator.expandDamageBranches` / `splitSecondary`: the surviving branch reuses its parent instead of forking, which halves fork count.
- `getResistances` / `getWeaknesses` return shared cached arrays. No caller may modify them.
- `getFaster` ties inside `MonWeights` resolve with the deterministic id tiebreak in a `SimContext` scope, not a real `Rng` draw.
- `Perf`: section timers and counters, off by default. `Perf.reset()` is called once per run, not per pair.
- Seeded results no longer match pre-Phase-8 runs, because `clone()` stopped consuming `Rng`. Compare Phase 8 against Phase 8.

Decisions kept: no wall-clock budget for lead refinement (it would make leads machine-dependent); cache keys are content hashes with VERIFY; weights stay exact and fresh per call (switching the simulator to root-snapshot weights would break T22).

## 2. Debug gating and the SWAP leak

- `AIV2.dbg()` = `AI_DEBUG && !Print.isDebugSuppressed()`. All diagnostics (branch detail, sack log, matrix log, eval breakdown, sack classification) are skipped when output is suppressed. They re-simulate, so this was a real cost in self-play.
- `Trainer.simShell` / `ownSlot` strip `Status.SWAP` and `Status.TEMP_SWITCHING` from every cloned mon. A decision already made this turn no longer leaks into the opponent's snapshot (this was a §1 peek violation, and the source of the "had swap at the end of the turn (bad)" spam).

## 3. Tuning infrastructure

- `Tunables`: reflection-based registry of the placeholder constants (24 parameters: Evaluator, MonWeights, ReplacementChooser, AIConfig defaults, EvalWeights terms, switch tax, last-ditch bonus). `apply(double[])` installs a vector, `apply(null)` restores defaults, `describe` prints differences. The constants are non-final statics.
- `Tuner`: coordinate ascent with seat-swapped self-play and Wilson intervals. Settings: `chunk`, `minBattles` (no verdict before this), `maxBattles`, `futilityAfter` / `futilityUpper` (stop hopeless candidates early), `z`, `passes`, `maxSeconds`, `only`.
- `SelfPlay.Config.paramsA/paramsB`; `playBattle` takes per-seat vectors and applies them immediately before each seat's decision and forced replacement.
- `Phase8Tests`: `cloneFidelity`, `cacheTransparency`, `selfPlayVerify`, `timing`, `switchRates`, `taxMechanism`, `replayStory`, `withTax`.

## 4. Tuning results

- Full run: 2 passes over all parameters, `chunk 50`, `minBattles 200`, `maxBattles 1500`, `z 2.8`. Every test was inconclusive except one accept (`D_TEMP_EXPLOIT` 6 -> 7.5, 54.4% over 1050) and one reject (`D_W_TEMPO` x1.25, 43.2%).
- Validation on fresh seeds: the `D_TEMP_EXPLOIT` accept scored 49.9% on random teams and 49.9% on story trainers (2000 battles each). It was noise. Earlier short screens produced the same kind of false accept (`FUTURE_W` 10: 61.7% over 60 battles, then 48.8% over 1000; `D_SACK_MIN_GAIN` 20). All defaults stay.
- Reading: the engine is insensitive to every tunable at about 3-4 points of resolution. The Phase 3-7 placeholders were already on a flat optimum.
- `D_ALPHA` and `D_TEMP_EXPLOIT` cannot show a benefit in mirror self-play, because the player model has nothing to exploit there. They only matter against opponents with habits.
- Long runs need sleep disabled. A suspended machine put hours into single-decision timings and burned `maxSeconds`.

## 5. Switch tax (`D_SWITCH_TAX = 25`)

- Observation: HARD vs NORMAL scored 48-50% over four runs without the tax, while HARD made about 4.4 plain switches per battle against NORMAL's 0.6. The extra switching was not paying.
- A flat point cost on `SWITCH` rows (`MatrixBuilder`, now `CellAdjustments.switchTax`) gave: vs NORMAL 53.6% (tax 20) and 51.7% (tax 25); HARD vs HARD 51.6 / 51.9 / 50.7% at tax 20 / 30 / 40 (2000 battles each, none significant alone); story trainers 50.3-50.9%; scripted opponents unchanged within noise.
- Mechanism check: taxed side swaps fall monotonically (5.3 -> 3.4 -> 2.1 per battle at tax 0 / 20 / 40) and pivots stay flat.
- A flat tax failed T16 ("HARD selects switches when the matrix favors them"), so the tax is waived while the foe's active threatens a KO on the AI's active (`SackAnalysis.threatens`). T16 passes with the waiver.
- Evidence is a real but small effect (about 1-2 points) that no single run proved. The pooled direction is consistent.

## 6. Last-ditch priority bonus (`D_LAST_DITCH_BONUS = 20`)

- Trigger: a non-lethal priority move that really acts first, in a player column where the player has a fast likely KO (koChance >= 0.5 and outspeeds). It adds `bonus * ditchP * (fraction of foe HP removed)` to that cell, so it stays inside the mixed strategy.
- Sweep (1000 battles each, bonus 15 to 50): 47.7, 53.5 (20), 50.8, 48.9, 48.2, 50.5, 49.4, 47.6%. No monotonic trend. Kept at 20 as a nudge for human-facing behavior, with no measured self-play effect.
- T32 finding: the matrix already rewarded the priority move in the columns the player plays. The AI declined it because switching beat every attack in that position. The T32 scenario now removes the bench so the test is about attacks only.

## 7. Behavior changes outside tuning

- `AIV2.dominatedRows` removes rows weakly dominated by another row. Exact duplicates are never dropped; `equalizeDuplicates` splits their mass evenly after solving, so move-list order cannot matter. The solve, the player-model blend and `Shaper` run on the reduced matrix, and `x` / `eq.x` are expanded back to full length with zeros so `A`, `M` and `x` stay aligned. This fixed T8 (a never-better priority move was getting about 22% from regret-matching noise).
- `MatrixBuilder`: post-eval adjustments (switch tax, last-ditch bonus) live in a private `CellAdjustments` class. `buildMatrix` is back to the plain simulate / evaluate / store loop.
- `SelfPlay` pivot handling matches `BattleUI`: when the AI moves first, the planned pivot slot (`getStatusNum(Status.SWITCHING)`) is used; when it moves second, the free-switch chooser picks. The free-switch override is intentional (the AI can answer a player's hard switch).

## 8. Bugs found by self-play (all fixed)

| Bug | Cause | Fix |
|---|---|---|
| `StackOverflowError`, two Trace mons | `swapIn` traced the other mon's Trace endlessly | Skip tracing when the other mon also has Trace |
| `StackOverflowError`, story trainers | `stat()` and Empathic Link looped | Loop guard in `stat()` |
| `ArrayIndexOutOfBounds` in `swapRandom` | A forced switch index left over from a move that never consumed it | Clear the forced switch after `moveInit`; `swapRandom` validates it |
| NPE in `ActionGen.choiceLockSkip` | `item != null` but `getItem(field)` is null under Magic Room | Check `getItem(field)` |
| "current is not in team" in `simShell` | Slot index 9 became `swapOut2` argument 10 == `NORMAL_SWITCH`, so the chooser ran instead of the planned slot | `performSwitch` passes a negative slot; clear error on mismatch |
| 18-mon teams in self-play | `SelfPlay.buildPool()` used raw multi-badge trainer lists | Skip teams longer than 6 |
| T27 failure | `EvalWeights` had no value equality | Added `equals` / `hashCode` |
| `Perf` reported one pair | `Perf.reset()` inside the pair loop | Moved above the loop |

## 9. Tests

All pass: Phase 3 (T4-T32), Phase 5, Phase 6, Phase 8, invariance (10,000 trials), and a 2000-battle story-trainer batch with 0 crashes. T8 passes with duplicate-equalized dominance pruning. T16 passes with the tax waiver. T32 is the last-ditch priority test.

## 10. Open items

- D2: `Perf` showed `MonWeights.compute` calls well below the decision count. Probably forced-action pre-checks; confirm with `Perf.report()` after one normal-position `bestMove2`.
- `Pokemon.evaluateSwitchInScore` has no production caller. Delete it together with the test uses.
- Optional: model the free-switch override in the simulator (the matrix prices a planned pivot slot even when the AI moves second).
- Not done, no measured need: `SwitchInScorer` / `shallowClone` cost, Tier 1 fast cells.
- The HARD vs NORMAL criterion is only weakly demonstrated in self-play (about 51-53% with the tax). Scripted-opponent exploitation is flat at the ceilings tried; mirror-team scripted runs would be a cleaner test.
