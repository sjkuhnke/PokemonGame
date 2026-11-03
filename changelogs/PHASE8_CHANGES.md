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
| "current is not in team" in `simShell` | Slot index 9 became `swapOut2` argument 10 == `NORMAL_SWITCH`, so the chooser ran instead of the planned slot | Not reachable in a real battle (teams have at most 6 mons, so slot arguments stay below 10), so `performSwitch` is unchanged. Self-play avoids it by skipping teams longer than 6 (next row) |
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

## 11. Delayed effects: Yawn, sleep and Future Sight
 
Status: written, **not yet compiled or run**. Fill in the results lines below after `Phase8DelayedEffectTests.diagnose()` and `runAll()`.
 
### Problem
The trainer never clicked Yawn or Future Sight. Both rows scored the same as doing nothing, so `AIV2.dominatedRows` removed them every time. Reading the code, the cause is not only the one-turn horizon:
 
| Gap | Where | Effect |
|---|---|---|
| Yawn leaves only `DROWSY` after the turn (`YAWNING` becomes `DROWSY` inside the same `endOfTurn`; the sleep lands at the end of the NEXT turn) | `Pokemon.endOfTurn` | No eval term reads `DROWSY` / `YAWNING`. |
| A sleeping ACTIVE mon is not valued at all | `calcRange` has no check for the attacker's own sleep; `Evaluator.statusPenalty` is applied to bench mons only | Even two turns later, a landed sleep scores 0. This also hurts any sleep move (Spore, Hypnosis). |
| Future Sight is a `FUTURE_SIGHT` field effect on the target's side, carrying bp / stat / mod / level / crit / ability | `Pokemon.move` | `pendingValue` only read `HEALING_WISH` / `LUNAR_DANCE` / `WISH`. |
| `YAWN` in `statusEffect` checks only `YAWNING` / `DROWSY` / `ASLEEP` | `Pokemon.statusEffect` | A sleep-immune target (Psychic, Insomnia, Safeguard, Electric Terrain, a Lum/Chesto holder) "becomes drowsy" and then nothing happens. The simulator copies that, so the eval has to apply the same gates. |
 
### Fix (evaluator only, no change to move code)
- `Evaluator.canFallAsleep(p, field)`: runs the real `sleep()` on `p.clone()` and checks `status == ASLEEP`, so there is no second copy of the sleep gates to keep in step (no change to `Pokemon.java`). It installs `Pokemon.field` for the call (`sleep()` reads the static for Safeguard, terrain and `isGrounded()`), runs inside a `SimContext` scope (`setSleepCounter` draws from `Rng`; the scope keeps that off the real stream and makes the Task calls inert), and passes the clone as its own `foe` (a null foe skips the Lum / Chesto cure, and the real foe would be hit by Synchronize).
- `Evaluator.sleepCost(side, field)`, added to `evalCore` as `W_SLEEP * (sleepCost(player) - sleepCost(ai))`:
  - `ASLEEP` with turns left: `SLEEP_TURN_VALUE (25) * SLEEP_MEAN_TURNS (2.0) * SLEEP_HOLD (0.7) * hpFrac`, halved for a Sleep Talk user (`SLEEP_TALK_FACTOR`).
  - `DROWSY` / `YAWNING` and `canFallAsleep`: the same times `DROWSY_LAND (0.8)`. Full-HP value is 28 points.
  - The mean (2 turns) is used instead of the real `sleepCounter`: the counter is hidden information (no-peek, spec section 1), and the simulator only draws one sample of it.
- `Evaluator.pendingDelayedDamage(defSide, atkSide, defWeights, field)`, added to `pendingValue` as `+pending(player side) - pending(ai side)`: each queued `FUTURE_SIGHT` effect is run through `takeFutureSight` in its deterministic modes (1 and -1, no tasks, no RNG), averaged, capped at the target's HP, times 100 / maxHP * mon weight * `FUTURE_SIGHT_HOLD (0.75)`. Immune targets come out 0 from the same damage code. `Pokemon.field` is installed for the call because `takeFutureSight` reads the static.
- All constants are non-final statics in `Evaluator`, like the other placeholders. `W_SLEEP = 0` and `FUTURE_SIGHT_HOLD = 0` give the old behavior (this is what `diagnose()` uses).
- Because the matrix includes the player's switch columns, the escape is priced by the solver: a foe that switches out clears `DROWSY`, and a Yawn used into a switch hits the switch-in (pre-move switches run first).
### Side effects to watch
- **Rest** now pays the sleep cost for the AI's own mon (about 35 points, 17 with Sleep Talk). Rest is still chosen when the heal is worth more; if a Rest-related test flips, lower `W_SLEEP` rather than special-casing Rest.
- **`deadTurn`** (NORMAL) evaluates with the same `evalCore`, so Yawn, Future Sight and sleep moves now count as "doing something" and can keep a NORMAL trainer from switching on a turn that used to be dead.
- **Unchanged and out of scope:** the evaluator still cannot see a plan across turns (Yawn then Protect, stalling out a Future Sight). It prices the state after one turn, with the delayed effect booked at a discount.
### Tests (`Phase8DelayedEffectTests`)
`diagnose()` prints the Yawn and Future Sight rows against a do-nothing row with both terms off (expect differences near 0). `runAll()`: Yawn becomes `DROWSY` after turn 1 and `ASLEEP` after turn 2 in the simulator; `canFallAsleep` gives the right answer for seven gates (healthy, burned, Insomnia, Psychic, Safeguard, Electric Terrain, Lum Berry) and leaves the real mon, its item and `Pokemon.field` untouched; eval prices `DROWSY`, `YAWNING`, `ASLEEP`, Sleep Talk, Insomnia and wounded mons; Future Sight is queued, priced, and 0 into a Dark target; the Yawn and Future Sight rows beat a pass row in every foe-move column; Yawn into an Insomnia foe is not rewarded; `DROWSY` does not follow a mon through a switch. `dump()` prints three full mixed strategies.
 
Results:
- `diagnose()` (terms off): Yawn and Future Sight tie with a pass row in every column (diff 0.00; one Future Sight switch column -1.77). Confirms the diagnosis.
- `runAll()`: 10/10 pass (after fixing two test-setup bugs: terrain is set with `Field.setTerrain`, not `setEffect`, and inside a `SimContext` scope because it announces itself through `Task.addTask`).
- `dump()` (terms on), Yawn + Tackle vs Tackle: Yawn row +28.0 over the terms-off value (exactly the `DROWSY` value), Future Sight +10.5; both now dominate Tackle in every column. The mixed strategy was 100% `SWITCH slot 2` because that bench mon beats every move in every column in the test fixture, so `dump()` now also prints AI-move-rows-only and strong-attack (Earthquake) scenarios.
- Self-play, sleep + Future Sight terms ON vs OFF, 400 battles on matchups containing Yawn / Future Sight / Rest: 53.1% (95% CI 48.2 to 58.0%), no significant difference.
- `Tuner` on `SLEEP_TURN_VALUE`, `SLEEP_HOLD`, `DROWSY_LAND`, `FUTURE_SIGHT_HOLD` (one step up and down each, 520 to 920 battles per step): every step inconclusive, defaults kept. The engine is insensitive to these constants, as in Phase 8; the shipped defaults stand.
### Assumptions to confirm
1. `Field.FieldEffect` has `bp`, `stat`, `mod`, `level`, `crit` and `ability` with types matching `takeFutureSight`'s parameters (they are assigned in `move()`, so they exist).
2. Future Sight lands on whoever is out when it resolves; the code values it on the mon that is out now.
3. The switch-out reset that removes `YAWNING` / `DROWSY` (the block around `Pokemon.java:8064`) runs for a sim switch (`swapOut2`).
4. `Phase4Tests.mk` / `duel` / `Duel` stay package-private statics.
6. `Pokemon.clone()` keeps `trainer` (confirmed in the code), so Safeguard on the side's field-effect list is visible to the clone.
5. `Pokemon.type1` / `type2` and `ability` are assignable from the test.
 
Status: written and measured. `W_BUFFER = 0` is the shipped default, so none of the buffer code is active.
 
### Problem
Healing moves (Recover and similar) were picked almost every time the mon was hurt, especially when slower, even against foes that hit weakly.
 
### Diagnosis (`Evaluator.breakdown` + `Phase8HealingDiagnosis`)
`breakdown` splits an eval into its weighted terms (`TERMS`: material, matchup, hazard, status, field, tempo, forced, charge, pending, sleep, buffer; `runAll` checks the terms sum to `eval`). `Phase8HealingDiagnosis.diagnose()` gives the AI Recover + Earthquake against a Tackle or Flamethrower foe, faster and slower, at 100/70/50/30% HP, in three setups (real weights with full benches, all mon weights forced to 1.0, 1v1 with every bench mon fainted). Findings:
- A heal at full HP is never rewarded (no phantom value).
- With full benches the matchup term is pinned at -60 in every weak-foe cell: the foe's best bench answer has edge about -1, and `-90 + ESCAPE_COST (30) = -60`. The cap's escape table is built from full-HP duels, so it cannot see the AI's current HP. That leaves linear `material`, where a heal is worth `heal% * weight` and an attack `damage% * foe weight`, whether or not the foe threatens anything.
- Mon weights decide it when the AI is faster (real weights: AI active 0.95, foe active 0.44; Recover 100% at 50% HP; with both forced to 1.0 Earthquake wins about 82%).
- When slower, Recover is the steady row against the foe's switch columns (Earthquake can hit a resistant switch-in), so minimax prefers it even at equal weights.
- In 1v1 (no cap) the evaluator behaves well: Earthquake wins at every HP, and a slower mon at 30% HP correctly heals (matchup +76). So the matchup term already prices danger and progress; it goes blind only when the foe has a bench.
- Not fixed by anything here: slower vs a strong foe at 100% HP still stalls (Recover -128 vs Earthquake -147 in a two-row matrix), and the solver's preference for rows that do not depend on the foe's choice.
### Buffer v1 (removed)
`W_BUFFER`: discount the part of each active mon's HP the foe cannot take before the duel is decided (needed HP sized from hits-to-win and speed), applied to both sides. It fixed the target rows but taxed progress: damaging the foe shortened the race, lowered the AI's needed HP and discounted the AI's HP, and discounting the foe's HP cancelled the credit for removing it (Earthquake vs a switch-in fell from +49.9 to -14.5; a correct faster / Flamethrower / 30% HP row flipped to Recover). Self-play, 400 battles on heal-move teams: 38.5% (CI 33.9 to 43.4%), significantly worse.
 
### Buffer v2 (kept, off)
One-sided (AI's own active mon only) and independent of the foe's HP and speed: `needed = BUFFER_FLOOR (0.10) + BUFFER_TURNS (1.5) * foeHit * BUFFER_SAFETY (1.25)`, HP above it discounted by `BUFFER_DISCOUNT (0.75)`. `runAll()` asserts the target rows (no heal when unthreatened), the row v1 broke, and that damaging the foe is worth exactly the same with the buffer on or off. `diagnoseBuffer()` matches the predictions (switch columns back to baseline, Earthquake wins every faster Tackle row, Flamethrower rows unchanged, the slower stall unchanged).
 
Self-play, `confirmBuffer(2000)`, buffer ON vs OFF:
| Teams | Score | 95% CI | |
|---|---|---|---|
| Matchups containing Recover / Rest / Wish (1000 found, 0 fell through) | 50.6% | 48.5 to 52.8% | no significant difference |
| Plain random competitive teams | 47.0% | 44.8 to 49.2% | significantly worse |
 
Not adopted. Reading: the buffer is neutral where it is supposed to act and costs about 3 points elsewhere, so the loss comes from non-heal decisions. Suspected cause: the need is sized from the foe's ACTIVE mon only, so a fresh mon in front of a weak hitter is docked up to about 40 to 47 points while the same mon in front of a strong hitter is not, which biases switching, sacking and replacement, and treats HP as spare when the foe's bench will threaten it later. The target effect and the harm scale together (the target rows only flip at about 0.5 weight or more), so lowering `W_BUFFER` / `BUFFER_DISCOUNT` does not separate them. `tuneBuffer` was not run for that reason.
 
### Next, if the feel problem is worth more work
Scope the adjustment to heal rows only (a `MatrixBuilder` cell adjustment, so it cannot touch other decisions) and size the threat from the foe's whole team, not its active mon. The tools for it are in place: `W_BUFFER`, `BUFFER_*` and `BUFFER_TURNS` are registered in `Tunables`; `DelayedEffectTuning` (`confirmBuffer`, `tuneBuffer`, `FilteredTeams`) runs the A/B and the tuner.