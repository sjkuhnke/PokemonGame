# Phase 4 change list (full move coverage; legacy effect-scoring code deleted)

Every change is tied to spec §7.5 / §9 / §12-Phase-4 / §15.6, a Phase 3 deferral, or a bug found while doing it.
Apply from your source root (the folder that contains `src/`):

    patch -p1 --binary < phase4.patch          # or: git apply --ignore-whitespace phase4.patch
    git rm src/test/IsUsefulSecondaryTest.java # not uploaded, so it can't be in the patch; see "Deleted"

The patch was dry-run against the exact files you uploaded (CRLF preserved). **Nothing in this phase has been compiled or
run**: the authoring environment has no game classpath and no `javac`. Every changed file was syntax-checked with a Java
parser, every enum constant and member the new code uses was grepped against your sources, but expect a compile fix or two.

## Decisions taken (yours)
1. Delete the whole legacy stack **including `LegacyAI` / `legacyBestMove`** (option b).
2. Forced replacement after a faint (`Trainer.getNext2`) now uses the non-recursive scorer.

## New files (package `pokemon`)
| File | Purpose |
|---|---|
| `SwitchInScorer` | Thin non-recursive replacement for `evaluateSwitchInScore`: post-switch-in `matchupScore` in a sim shell + `SimContext` |
| `SecondaryKinds` | Which secondaries are status/flinch (generated from `Pokemon.secondaryEffect`), plus `effectiveChance` mirroring `move()`'s `secChance` chain |
| `Phase4Tests` | 19 tests, see "Tests" |
| `tools/gen_secondary_kinds.py` | Regenerates `SecondaryKinds`' two sets from `Pokemon.java` |

## Deleted
| What | Where |
|---|---|
| `LegacyAI.java`, `Pokemon.legacyBestMove` | (b) |
| `scorePokemon`, `scoreMove`, `analyzeMoveEffect`, `EffectChange`, `EffectAnalysisResult`, `scoreEffectUsefulness`, `statChangeIsUseful`, `detectStatStageChanges`, `scoreInflictedStatus`, `evaluateRetype`/`retypeEff`, `evaluateAbilityOverwrite`, `findAddedFieldEffect`, `findAddedVolatileStatus`, `vStatusesEqual`, `hasAccCheckedMoves`, `calcStatBoostScore`, `isImmuneToMoveEffect` | `Pokemon.java` |
| `analyzeDefensiveResponse` x2, `calculateDefensiveResponse`, `DefensiveResponseResult`, `applyAggression`, `findStrongestMove`, `findFoeStrongestMove`, `FoeMoveResult`, `chooseSwitchInSlot`, `simulateSwitchIn`, `hasPivotMove`, `debugIndent`, `owner`, the `debugDepth` field, `STAT_STAGE_UNIT_VALUE`, `PLACEHOLDER_VALUE`, and the imports `util.Print`, `java.util.Collections`, `java.util.List` | `Pokemon.java` |
| `src/test/IsUsefulSecondaryTest.java` | it only exercised `analyzeMoveEffect` / `scoreMove`; the same mechanics are covered by `Phase4Tests`. Delete with `git rm` |

`Pokemon.java`: 13,234 -> 11,540 lines. The list was computed from a call graph (a method was removed only when every
remaining reference to it was inside already-removed code, and no other uploaded file referenced it), not by eye.
Kept on purpose because `Evaluator`/`ActionGen`/`matchupScore` use them: `matchupScore` (+ its constants),
`calcHazardTeamValue`, `hazardMoveForEffect`, `isHazardUseful`, `scoreWeatherValue`, `scoreScreenValue`, `isUsefulPivot`.
Your grep found the only outside callers (`Phase0Tests` B9, `Trainer` x3, `IsUsefulSecondaryTest`); all are handled.
The non-private methods `getDamagingMoveset`, `getType1/2`, etc. that my scan lists as "no callers in the uploaded files"
were **not** touched: other packages (UI) may call them.

## Changed files
| File | Change | Ties to |
|---|---|---|
| `Pokemon.java` | The deletions above; the Heal Bell/Aromatherapy `cloned` guard now lets sim shells through (first-run fix, below); `evaluateSwitchInScore(Pokemon, Field)` is now a one-line call to `SwitchInScorer` (the 6-arg overload is gone); the Metronome branch of `move()` takes its move from `SimContext.consumeForcedMetronome()` first (3 lines; `null` outside a sim, so real play is unchanged) | §15.6, §9 Metronome |
| `Trainer.java` | New package-private `ownSlot(int)` (sim shells only). `getNext2`/`pickLead`/`predictPlayerLeads` need no edit: the signature they call is preserved | see "Bug fixed" |
| `SimContext.java` | `forceNextMetronome(Move)` / `consumeForcedMetronome()` (same pattern as `forceNextSwitch`) | §9 Metronome |
| `BattleSimulator.java` | (1) `splitSecondary`: PROC / NO_PROC branches for status/flinch secondaries with effective chance in [0.1, 0.9]; (2) `fanOutMetronome`: a Metronome cell is the average over 4 evenly spaced moves of `Move.getAllMoves()`; (3) copy-on-write ownership before any switch-in, random-target switch, Red Card, Heal Bell/Aromatherapy, and Aurora Glow end-of-turn | §7.5, §9, bug fix |
| `Evaluator.java` | New `pendingValue` = `pendingRecovery` (Healing Wish / Lunar Dance / Wish) and `restrictionCost` (Taunt / Disable / Encore / Torment, choice items excluded); `hazardLayerScale` (Spikes 1 : 1.33 : 2, Toxic Spikes 1 : 1.67); `durationFactor` on weather and screens; Natural Cure bench mons cost nothing in `benchStatusValue`; `fracOf` is package-private | §9, Phase 3 deferrals |
| `EvalWeights.java` | `wPending` (default 1.0; the 6-arg constructor still works) | §9 |
| `ActionGen.java` | `usefulRows` filters AI and player move rows: Fake Out / First Impression / Dream Eater etc. when `calcRange.usable` is false (damaging moves only), Sleep Talk / Snore while awake, hazards `isHazardUseful` rejects. Never returns an empty list | §9 |
| `AIV2.java`, `TrainerAI.java`, `SelfPlay.java` | `getName()` says Phase 4; `SelfPlay.Config` defaults are now `AIV2.INSTANCE` for both seats; stale legacy comments | (b) |
| `Phase0Tests.java` | B9 rewritten: the legacy `simulateSwitchIn`/`analyzeMoveEffect` it guarded are gone, so it now asserts `evaluateSwitchInScore` never changes `Pokemon.createTask` | (b) |

## Bug fixed (pre-existing, found while doing Phase 4)
`Trainer.simShell(touched)` shares every untouched bench `Pokemon` with its source **and leaves its `trainer` pointing at
the source shell**, and `Trainer.swapOut2` does not clone the mon it brings in. So any simulated switch-in of an untouched
bench mon (every post-KO replacement, Whirlwind/Roar/Red Card fan-out, Heal Bell) mutated an object shared with the root
and with sibling branches (hazard damage on entry, stat changes, cures), and read hazards from the wrong shell. Effect: cells
contaminated each other and the root, so a matrix depended on cell order. T3 did not catch it because it fingerprints the
REAL state, not the root shell. Fix: copy-on-write at the point of use (`Trainer.ownSlot`, ownership test `p.trainer == shell`).
**Expect Phase 3 matrix values, and self-play results, to shift** in games with hazards, KOs and team-wide moves.
Regression test: `ownershipRegression`.

## Found on first run (Phase4Tests output: 15 passed, 3 FAILED, 1 skipped)
| Test | Cause | Resolution |
|---|---|---|
| Heal Bell / Aromatherapy | **Simulator gap, mine to own.** `move()` starts that case with `if (this.cloned) return;`. Every simulated mon is a clone, so both moves have always been no-ops in the sim. I had classed the row as "Sim already handles it" without reading the case. It is the only silent-skip `cloned` guard in `Pokemon.java` (swept) | Guard is now `this.cloned && !(SimContext.active() && trainer != null && trainer.cloned)`: analysis clones still can't cure the real team, sim shells (which own their team, and are made unshared by `ownSlot`) now run it. Real and self-play battles are unchanged (`Trainer.clone()` sets `cloned=false`) |
| T9 | **Bad test premise.** Foe at 1 HP: the material term values a 1 HP mon at ~alive-bonus (20), so KOing it gains little, and the KO hands the foe a fresh replacement whose matchup can be worse for the AI than the doomed 1 HP mon's. The boost row (87.5) beat the attack row (0.8) because of the matchup term, not a simulator error | Scenario is now a FULL-HP foe that Flamethrower OHKOs and the AI outspeeds (`ohkoDuel`); asserted only on stay-in columns (against a switch, the attack hits a possible resist, so boost can legitimately win). Use `Phase4Debug.t9(true/false)` to see the term breakdown |
| T10 | **I specified it wrongly.** The spec's T10 is about probabilities under a *predicted* switch chance (`predict()`/`finalStrategy()`, Phase 6). My payoff-form version assumed the placeholder eval already values a boost more than chip damage on a switch-in. Measured: edge over best attack is -18.0 vs a switch, +10.0 vs an attack, so it does not | Now prints both deltas and SKIPs until Phase 6. A weight/horizon question for Phase 8 (eval only sees a 1-turn matchup, so setup value is under-counted), not a bug |
| Protect | Test bug: `duelWithDamagedFoe` checks `aiMoves[0]`, and I had put Protect first | Flamethrower first |

## Behaviour changes you will notice in the real game
* The AI's pick after a faint (all difficulties) now comes from `SwitchInScorer`: matchup after entry hazards/abilities, no
  lookahead. It no longer models the hit the candidate would take this turn. Leads (`pickLead`) use the same scorer.
* Nothing else in real play changes; the rest is decision quality inside `AIV2`.

## §9 checklist, final classification
"Sim" = handled by the real `move()`/`endOfTurn()` inside `simulateTurn`. "Test" = behaviour asserted. "Smoke" = runs inside the
sim without throwing, probabilities sum to 1, root untouched, every branch evaluates finite (`rowSmoke`).

| Row | Bucket | Coverage |
|---|---|---|
| Counter / Mirror Coat / Metal Burst | Sim | Smoke |
| Destiny Bond, Endure | Sim | Smoke |
| Disable / Torment / Taunt / Encore | Sim + **eval term** (`restrictionCost`) | Test (Taunt cost), Smoke |
| Trick / Switcheroo | Sim | Smoke (no assertion that the item moved) |
| Roar / Whirlwind / Dragon Tail / Red Card / Eject | Sim (Phase 2 fan-out) + ownership fix | Smoke, `ownershipRegression` |
| Rapid Spin / Defog / Field Flip | Sim + `hazardLayerScale` | Test (hazardPain drops), Smoke |
| Healing Wish / Lunar Dance / Wish | **eval term** (`pendingRecovery`) | Test (unit), Smoke |
| Aromatherapy / Heal Bell | **sim fix** (see "Found on first run") + Natural Cure in `benchStatusValue` | Test |
| Protect family | Sim | Test (Protect), Smoke (Spiky Shield) |
| Pivot moves | Sim (Phase 2) | not re-tested this phase (T14) |
| Charge / Hyper Beam family | Sim + existing `forcedTurnPenalty` | Test |
| Fake Out / First Impression | **row filter** | Test |
| Sleep Talk / Snore | **row filter** | Test |
| Metronome | **sim change** (fan-out) | Test (invariants only) |
| Hazards (rows) | **row filter** + `hazardPain` layer scale | Test |
| Screens, weather, terrain, Trick Room | **eval** (`durationFactor`); `calcRange` for the rest | Test (math only) |
| Recoil / self-KO | Sim | Test (Explosion material) |
| Setup moves; stat drops; speed control | Sim + post-turn `activeMatchup` | T9, T10, T11 |
| Ability/type change moves | Sim (`calcRange` sees the new type/ability) | not tested this phase |
| Secondary status/flinch | **sim change** (branching) | Test |
| Sack switch, Perish at 1, dead turn, Struggle, Choice lock | Phase 3 / 5 | unchanged |
| Forced replacement, lead selection | Phases 5 / 7 | `SwitchInScorer` is the stopgap |

## Tests (`Phase4Tests.runAll()`, in-game)
Output is PASSED / FAILED / SKIPPED per test. SKIPPED = a synthetic scenario found no species with the needed property in a
short id scan; it means "not exercised".

`hazardLayerScale`, `durationFactor` (pure math); `pendingRecovery`, `restrictionCost`, `naturalCureBenchStatus` (eval terms);
`rowFilter`; `teamCure`, `hazardRemoval`, `chargeAndRecharge`, `selfKoMaterial`, `protectBlocks`, `rowSmoke` (28 moves x every
player action), `secondaryBranching`, `metronomeFanOut`; `ownershipRegression`, `switchInScorer`; **T9, T10, T11** in matrix
form. T10 asserts on payoffs, not sampled probability: the "higher predicted switch chance, more probability" half needs the
Phase 6 player model.

## Known limits, flagged rather than hidden
| Item | Note |
|---|---|
| `branchBudget` is still 4 | `mergeSimilar` folds dropped branches' probability into a kept branch's state. Secondary splitting adds branches, so this bias can now bite more often. Not raised here (it multiplies matrix cost); a Phase 8 tuning item |
| `SecondaryKinds` is generated | New status/flinch secondaries need adding, or re-run `tools/gen_secondary_kinds.py`. Stat-drop secondaries stay on `SimPolicy.MAJORITY` (>= 50% procs), the spec's expected-value stand-in |
| Fang moves | Their body flips a coin internally after the 10% gate; the branch covers the gate, not the coin |
| Metronome | 4-move deterministic sample, same for every cell; it is an estimate, not the true mean |
| `restrictionCost` / `pendingRecovery` weights | Placeholders (max 12 points; wish value in HP-points), tuned in Phase 8 with everything else |
| Future Sight | Not in the §9 table and not modeled as pending damage |
| Pivot / ability-change rows | No new dedicated test |
| Regression vs Phase 3 | The spec's "no win-rate regression" check needs Phase 3 to be runnable. It compared against `LegacyAI`, now gone, and the ownership fix changes matrix values on purpose. If you did not record the Phase 3 `AIV2` vs `LegacyAI` HARD/NORMAL numbers before applying this patch, tag the pre-patch commit and re-run them there |

## Manual verification
1. Apply the patch, `git rm` the test file, compile. Likeliest breaks: a `Trainer(String, Pokemon[], int)`/`Moveslot(Move)`
   signature used by `Phase4Tests`; `Field.FieldEffect.stat` typing in `Evaluator.pendingRecovery`; an unseen class that
   referenced a deleted method (your grep covered the ten headline names only, so grep for the rest of the deleted list).
2. `Phase4Tests.runAll();` then `Phase3Tests.runAll();` and `Phase0Tests.runAll();`. Phase 3 tests that scanned the roster may
   now land on different scenarios; investigate any newly failing one before assuming it is a test problem.
3. T3 by hand with the new features: `Phase2Tests.runInvarianceCheck(...)` with HEAL_BELL and METRONOME in a moveset.
4. `AIV2.AI_DEBUG = true` on a battle with Stealth Rock up and a KO: replacement payoffs should be identical when the same
   position is decided twice (the ownership fix).
5. Self-play: `SelfPlay.run` with the defaults (AIV2 vs AIV2, both seats): no crashes, determinism check clean, think time per
   decision noted. The secondary/Metronome fan-out and `usefulRows`' extra `calcRange` calls change cost per cell.
6. Faint a trainer mon in a real battle and confirm the replacement is sensible (this is the `getNext2` behaviour change).

## Not in Phase 4 (deliberately)
Sacking logic, `SACK_MIN_GAIN`, the real `chooseReplacement` unification (Phase 5); player model, `predict()`,
`finalStrategy()` (Phase 6); lead selection (Phase 7); Tier 0/1 pruning, caching, tuning of every placeholder weight (Phase 8).
