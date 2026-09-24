package pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * §7.5-7.7. Turn simulation on top of SimState. Phase 4 adds: secondary-effect branching for status/flinch
 * (§7.5, {@link SecondaryKinds}), a Metronome expected-value fan-out (§9), and copy-on-write ownership of any bench
 * mon a switch brings in ({@link Trainer#ownSlot}), which fixes a Phase 2 aliasing bug (see PHASE4_CHANGES.md).
 */
public class BattleSimulator {

	private static final double ACC_LOW = 0.05, ACC_HIGH = 0.95;
	/** §7.5 Phase 4: split a status/flinch secondary only when its chance is in [0.1, 0.9]; outside that MAJORITY is close enough. */
	private static final double SEC_LOW = 0.10, SEC_HIGH = 0.90;
	/** §9 Metronome: number of evenly spaced moves from Move.getAllMoves() a Metronome cell is averaged over. */
	private static final int METRONOME_SAMPLES = 4;
	private static List<Move> metronomeSample;
	/** Status moves (no damage) whose own effect is an unconditional random switch of the
	 *  DEFENDER's side, resolved in statusEffect(): Pokemon.java ~7281. */
	private static final Set<Move> STATUS_RANDOM_SWITCH_MOVES = new HashSet<>(Arrays.asList(Move.WHIRLWIND, Move.ROAR));
	/** Damaging moves whose secondary effect (Pokemon.java ~5182, via secondaryEffect()) is a
	 *  random switch of the DEFENDER's side after a successful hit. */
	private static final Set<Move> DAMAGE_RANDOM_SWITCH_MOVES = new HashSet<>(Arrays.asList(Move.DRAGON_TAIL, Move.CIRCLE_THROW));

	public static List<Branch> simulateTurn(SimState root, Action aAct, Action pAct, AIConfig cfg) {
		Field prevField = Pokemon.field;
		try (SimContext.Scope outer = SimContext.enter(SimPolicy.DEFAULT)) {
			SimState s0 = root.fork(touchedSlots(aAct), touchedSlots(pAct));
			Pokemon.field = s0.field;

			List<Branch> branches = new ArrayList<>();
			branches.add(new Branch(1.0, s0));

			branches = performPreMoveSwitches(branches, aAct, pAct);
			branches = performMoves(branches, aAct, pAct);

			for (Branch br : branches) {
				enterField(br.state);
				endOfTurnPhase(br.state);
			}
			for (Branch br : branches) {
				enterField(br.state);
				resolveReplacements(br.state);
			}

			return mergeSimilar(branches, cfg.branchBudget);
		} finally {
			Pokemon.field = prevField;
		}
	}

	private static void enterField(SimState s) { Pokemon.field = s.field; }

	private static Set<Integer> touchedSlots(Action act) {
		Set<Integer> s = new HashSet<>();
		if (act.kind == ActionKind.SWITCH || act.kind == ActionKind.MOVE_THEN_SWITCH) s.add(act.slot);
		return s;
	}

	// ---- Step 1: pre-move switches ----

	private static List<Branch> performPreMoveSwitches(List<Branch> branches, Action aAct, Action pAct) {
		boolean aSwitches = aAct.kind == ActionKind.SWITCH;
		boolean pSwitches = pAct.kind == ActionKind.SWITCH;
		if (!aSwitches && !pSwitches) return branches;

		for (Branch br : branches) {
			enterField(br.state);
			if (aSwitches && pSwitches) {
				Pokemon aOut = br.state.ai.active();
				Pokemon pOut = br.state.player.active();
				boolean aFirst = aOut.getFaster(pOut, 0, 0, br.state.field) == aOut;
				if (aFirst) {
					performSwitch(br.state.ai, aAct.slot, pOut);
					performSwitch(br.state.player, pAct.slot, br.state.ai.active());
				} else {
					performSwitch(br.state.player, pAct.slot, aOut);
					performSwitch(br.state.ai, aAct.slot, br.state.player.active());
				}
			} else if (aSwitches) {
				performSwitch(br.state.ai, aAct.slot, br.state.player.active());
			} else {
				performSwitch(br.state.player, pAct.slot, br.state.ai.active());
			}
		}
		return branches;
	}

	private static void performSwitch(SideState side, int slot0Based, Pokemon foe) {
		side.shell.ownSlot(slot0Based); // Phase 4: never switch in a Pokemon object shared with another branch/cell/root
		side.shell.swapOut2(foe, slot0Based + 1, false, false);
	}

	/** Makes every alive bench mon of this shell its own clone (before a random-target switch may pick any of them). */
	private static void ownAllBench(Trainer shell) {
		for (int i = 0; i < shell.team.length; i++) {
			Pokemon p = shell.team[i];
			if (p != null && !p.isFainted()) shell.ownSlot(i);
		}
	}

	// ---- Step 2: moves in priority/speed order ----

	private static List<Branch> performMoves(List<Branch> branches, Action aAct, Action pAct) {
		Move aMove = aAct.kind == ActionKind.SWITCH ? null : aAct.move;
		Move pMove = pAct.kind == ActionKind.SWITCH ? null : pAct.move;
		if (aMove == null && pMove == null) return branches;

		List<Branch> result = new ArrayList<>();
		for (Branch br0 : branches) {
			enterField(br0.state);
			Pokemon aMon = br0.state.ai.active();
			Pokemon pMon = br0.state.player.active();
			boolean aCanAct = aMove != null && !aMon.isFainted();
			boolean pCanAct = pMove != null && !pMon.isFainted();
			if (!aCanAct && !pCanAct) { result.add(br0); continue; }

			int aP = aMove == null ? 0 : aMove.getPriority(aMon, br0.state.field);
			int pP = pMove == null ? 0 : pMove.getPriority(pMon, br0.state.field);
			boolean aFirst;
			if (!aCanAct) aFirst = false;
			else if (!pCanAct) aFirst = true;
			else aFirst = aMon.getFaster(pMon, aP, pP, br0.state.field) == aMon;

			List<Branch> local = new ArrayList<>();
			local.add(br0);
			if (aFirst) {
				if (aCanAct) local = executeOneMove(local, Side.AI, aAct, true);
				if (pCanAct) local = executeOneMove(local, Side.PLAYER, pAct, false);
			} else {
				if (pCanAct) local = executeOneMove(local, Side.PLAYER, pAct, true);
				if (aCanAct) local = executeOneMove(local, Side.AI, aAct, false);
			}
			result.addAll(local);
		}
		return result;
	}

	private enum Side { AI, PLAYER }

	private static List<Branch> executeOneMove(List<Branch> branches, Side mover, Action act, boolean actsFirst) {
		List<Branch> out = new ArrayList<>();
		for (Branch br : branches) {
			enterField(br.state);
			SideState moverSide = mover == Side.AI ? br.state.ai : br.state.player;
			Pokemon attacker = moverSide.active();
			if (attacker.isFainted()) { out.add(br); continue; }

			for (Branch dmgBranch : expandDamageBranches(br, mover, act.move, actsFirst)) {
				out.addAll(runMoveAndFollowUps(dmgBranch, mover, act, actsFirst));
			}
		}
		return out;
	}

	/**
	 * §7.5: branch on accuracy (hit/miss) and, for damaging moves, KO-roll. Covers three shapes:
	 * unusable/immune (nothing happens); usable-no-damage, e.g. Whirlwind/Roar (branch on
	 * accuracy only); damaging (branch on accuracy and KO). Every resulting branch is tagged
	 * likelyHit so runMoveAndFollowUps knows whether Red Card / the random-switch moves are even
	 * in play, and carries the SimPolicy that reproduces its outcome when moveInit actually runs.
	 */
	private static List<Branch> expandDamageBranches(Branch br, Side mover, Move move, boolean first) {
		SideState moverSide = mover == Side.AI ? br.state.ai : br.state.player;
		SideState otherSide = mover == Side.AI ? br.state.player : br.state.ai;
		Pokemon attacker = moverSide.active();
		Pokemon defender = otherSide.active();

		List<Branch> result = new ArrayList<>();
		DamageRange range;
		try (SimContext.Scope s = SimContext.enter(SimPolicy.DEFAULT)) {
			range = attacker.calcRange(defender, move, first, br.state.field);
		}

		if (!range.usable || range.immune) {
			Branch b = withPolicy(br, SimPolicy.DEFAULT);
			b.likelyHit = false;
			result.add(b);
			return result;
		}

		boolean splitAcc = range.accuracy > ACC_LOW && range.accuracy < ACC_HIGH;

		if (!range.dealsDamage()) {
			// Status-effect / no-direct-damage moves (Whirlwind, Roar, Counter family, ...):
			// only accuracy matters.
			if (!splitAcc) {
				SimPolicy p = SimPolicy.DEFAULT.withAccuracy(range.accuracy >= ACC_HIGH ? SimPolicy.Accuracy.HIT
						: range.accuracy <= ACC_LOW ? SimPolicy.Accuracy.MISS : SimPolicy.Accuracy.MAJORITY);
				Branch b = withPolicy(br, p);
				b.likelyHit = range.accuracy >= 0.5;
				result.add(b);
				return result;
			}
			Branch miss = withPolicy(rebranch(br, br.prob * (1 - range.accuracy)), SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.MISS));
			miss.likelyHit = false;
			Branch hit = withPolicy(rebranch(br, br.prob * range.accuracy), SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.HIT));
			hit.likelyHit = true;
			result.add(miss);
			result.add(hit);
			return result;
		}

		// Damaging moves: accuracy, then KO-roll.
		Pokemon target = range.reflected ? attacker : defender;
		double ko = range.koProb(target.currentHP);
		boolean splitKO = ko > ACC_LOW && ko < ACC_HIGH;

		if (!splitAcc && !splitKO) {
			SimPolicy p = SimPolicy.DEFAULT.withAccuracy(range.accuracy >= ACC_HIGH ? SimPolicy.Accuracy.HIT
					: range.accuracy <= ACC_LOW ? SimPolicy.Accuracy.MISS : SimPolicy.Accuracy.MAJORITY);
			Branch b = withPolicy(br, p);
			b.likelyHit = range.accuracy >= 0.5;
			result.add(b);
			return splitSecondary(result, attacker, defender, move, first, br.state.field, ko, range);
		}

		if (splitAcc) {
			Branch miss = withPolicy(rebranch(br, br.prob * (1 - range.accuracy)), SimPolicy.DEFAULT.withAccuracy(SimPolicy.Accuracy.MISS));
			miss.likelyHit = false;
			result.add(miss);
		}
		double hitProb = splitAcc ? range.accuracy : 1.0;
		SimPolicy.Accuracy accForHit = splitAcc ? SimPolicy.Accuracy.HIT : SimPolicy.Accuracy.MAJORITY;
		if (splitKO) {
			SimPolicy koPolicy = SimPolicy.DEFAULT.withAccuracy(accForHit).withDamageRoll(1.00);
			SimPolicy survivePolicy = SimPolicy.DEFAULT.withAccuracy(accForHit).withDamageRoll(0.85);
			Branch koBr = withPolicy(rebranch(br, br.prob * hitProb * ko), koPolicy);
			koBr.likelyHit = true;
			Branch surviveBr = withPolicy(rebranch(br, br.prob * hitProb * (1 - ko)), survivePolicy);
			surviveBr.likelyHit = true;
			result.add(koBr);
			result.add(surviveBr);
		} else {
			Branch hitBr = withPolicy(rebranch(br, br.prob * hitProb), SimPolicy.DEFAULT.withAccuracy(accForHit));
			hitBr.likelyHit = true;
			result.add(hitBr);
		}
		return splitSecondary(result, attacker, defender, move, first, br.state.field, ko, range);
	}

	/**
	 * §7.5 Phase 4: splits each connecting branch of a damaging move into "secondary procs" / "secondary doesn't",
	 * weighted by the effective chance, when the effect is a status or flinch ({@link SecondaryKinds}) and the chance is
	 * in [SEC_LOW, SEC_HIGH]. Not split: KO branches (the target is gone, so the effect is moot), a target that is
	 * KO'd with near certainty anyway, reflected damage, flinch when the mover goes second, and status moves.
	 */
	private static List<Branch> splitSecondary(List<Branch> in, Pokemon attacker, Pokemon defender, Move move, boolean first,
			Field field, double ko, DamageRange range) {
		if (move.cat == 2 || range.reflected || ko >= ACC_HIGH || !SecondaryKinds.isStatusOrFlinch(move)) return in;
		if (SecondaryKinds.isFlinchOnly(move) && !first) return in;
		double chance = SecondaryKinds.effectiveChance(attacker, defender, move, field) / 100.0;
		if (chance < SEC_LOW || chance > SEC_HIGH) return in;

		List<Branch> out = new ArrayList<>();
		for (Branch b : in) {
			SimPolicy base = b.policy != null ? b.policy : SimPolicy.DEFAULT;
			if (!b.likelyHit || base.damageRoll >= 1.0) { // miss, or the KO branch (damageRoll 1.00 in expandDamageBranches)
				out.add(b);
				continue;
			}
			Branch proc = withPolicy(rebranch(b, b.prob * chance), base.withSecondary(SimPolicy.Secondary.PROC));
			proc.likelyHit = true;
			Branch none = withPolicy(rebranch(b, b.prob * (1 - chance)), base.withSecondary(SimPolicy.Secondary.NO_PROC));
			none.likelyHit = true;
			out.add(proc);
			out.add(none);
		}
		return out;
	}

	private static Branch rebranch(Branch br, double prob) {
		Branch b = new Branch(prob, br.state.fork());
		return b;
	}

	private static Branch withPolicy(Branch br, SimPolicy p) {
		br.policy = p;
		return br;
	}

	/**
	 * §7.7: random-target forced switches. Both the status-move family (Whirlwind/Roar) and the
	 * damaging-move family (Dragon Tail/Circle Throw) swap the DEFENDER's side; Red Card swaps
	 * the ATTACKER's side (foe.getItem==RED_CARD, checked per Pokemon.java's hit loop — no
	 * isFainted() guard there, so it fans out on the KO branch too, matching the real code).
	 * All three call Trainer.swapRandom synchronously inside move()/its effect methods, so
	 * fan-out has to happen before moveInit, one full run per candidate.
	 *
	 * Known simplification: Red Card eligibility here omits the `!sheer` guard (Sheer Force
	 * happening to suppress Red Card alongside secondary effects on the same hit) — see
	 * PHASE2_CHANGES.md.
	 */
	private static List<Branch> runMoveAndFollowUps(Branch br, Side mover, Action act, boolean first) {
		enterField(br.state);
		SideState moverSide = mover == Side.AI ? br.state.ai : br.state.player;
		SideState otherSide = mover == Side.AI ? br.state.player : br.state.ai;
		Pokemon attacker = moverSide.active();
		Pokemon defender = otherSide.active();

		if (act.move == Move.METRONOME) return fanOutMetronome(br, mover, act, first);

		if (br.likelyHit) {
			boolean statusSwitch = STATUS_RANDOM_SWITCH_MOVES.contains(act.move) && defender.trainer != null && !defender.hasStatus(Status.ROOTED);
			boolean damageSwitch = DAMAGE_RANDOM_SWITCH_MOVES.contains(act.move) && defender.trainer != null && !defender.hasStatus(Status.ROOTED);
			if (statusSwitch || damageSwitch) {
				List<Integer> candidates = eligibleSwitchIns(otherSide, defender);
				if (candidates.size() > 1) return fanOutSwitch(br, mover, act, first, candidates);
			} else if (isRedCardEligible(attacker, defender)) {
				List<Integer> candidates = eligibleSwitchIns(moverSide, attacker);
				if (candidates.size() > 1) return fanOutSwitch(br, mover, act, first, candidates);
			}
		}
		return runOneMoveInit(br, mover, act, first, null);
	}

	/** §9: a Metronome cell is the average over a small fixed sample of moves (same sample every cell, so rows stay comparable). */
	private static List<Branch> fanOutMetronome(Branch br, Side mover, Action act, boolean first) {
		List<Move> sample = metronomeSample();
		List<Branch> out = new ArrayList<>();
		for (Move sm : sample) {
			Branch sub = rebranch(br, br.prob / sample.size());
			sub.policy = br.policy;
			sub.likelyHit = br.likelyHit;
			out.addAll(runOneMoveInit(sub, mover, act, first, null, sm));
		}
		return out;
	}

	private static List<Move> metronomeSample() {
		if (metronomeSample == null) {
			Move[] all = Move.getAllMoves();
			List<Move> s = new ArrayList<>();
			for (int i = 0; i < METRONOME_SAMPLES; i++) {
				int idx = (int) ((i + 0.5) * all.length / METRONOME_SAMPLES);
				while (all[idx] == Move.METRONOME) idx = (idx + 1) % all.length;
				s.add(all[idx]);
			}
			metronomeSample = s;
		}
		return metronomeSample;
	}

	private static boolean isRedCardEligible(Pokemon attacker, Pokemon defender) {
		return defender.getItem(Pokemon.field) == Item.RED_CARD
				&& attacker.trainer != null
				&& attacker.trainer.hasValidMembers(defender)
				&& !attacker.hasStatus(Status.ROOTED);
	}

	private static List<Branch> fanOutSwitch(Branch br, Side mover, Action act, boolean first, List<Integer> candidates) {
		List<Branch> fanned = new ArrayList<>();
		for (int c : candidates) {
			Branch sub = rebranch(br, br.prob / candidates.size());
			sub.policy = br.policy;
			sub.likelyHit = br.likelyHit;
			fanned.addAll(runOneMoveInit(sub, mover, act, first, c));
		}
		return fanned;
	}

	private static List<Branch> runOneMoveInit(Branch br, Side mover, Action act, boolean first, Integer forcedSwitchTarget) {
		return runOneMoveInit(br, mover, act, first, forcedSwitchTarget, null);
	}

	private static List<Branch> runOneMoveInit(Branch br, Side mover, Action act, boolean first, Integer forcedSwitchTarget,
			Move forcedMetronome) {
		enterField(br.state);
		SideState moverSide = mover == Side.AI ? br.state.ai : br.state.player;
		SideState otherSide = mover == Side.AI ? br.state.player : br.state.ai;
		Pokemon attacker = moverSide.active();
		Pokemon defender = otherSide.active();

		// Phase 4 copy-on-write: whichever bench mons a random-target switch / Red Card can bring in must be this shell's own.
		if (STATUS_RANDOM_SWITCH_MOVES.contains(act.move) || DAMAGE_RANDOM_SWITCH_MOVES.contains(act.move)) ownAllBench(otherSide.shell);
		if (defender.getItem(Pokemon.field) == Item.RED_CARD) ownAllBench(moverSide.shell);
		// Team-wide moves mutate every member (Heal Bell / Aromatherapy cure the whole bench).
		if (act.move == Move.HEAL_BELL || act.move == Move.AROMATHERAPY) ownAllBench(moverSide.shell);

		if (act.kind == ActionKind.MOVE_THEN_SWITCH) {
			attacker.addStatus(Status.TEMP_SWITCHING, act.slot + 1);
		}

		SimPolicy policy = br.policy != null ? br.policy : SimPolicy.DEFAULT;
		try (SimContext.Scope s = SimContext.enter(policy)) {
			if (forcedSwitchTarget != null) SimContext.forceNextSwitch(forcedSwitchTarget);
			if (forcedMetronome != null) SimContext.forceNextMetronome(forcedMetronome);
			attacker.moveInit(defender, act.move, first);
			SimContext.forceNextMetronome(null); // never leak a forced Metronome into a later cell
		}

		if (attacker.hasStatus(Status.SWITCHING) && !attacker.isFainted()) {
			int target = attacker.getStatusNum(Status.SWITCHING);
			attacker.removeStatus(Status.SWITCHING);
			if (target > 0) performSwitch(moverSide, target - 1, defender);
		}

		if (defender.hasStatus(Status.SWITCHING) && !defender.isFainted()) {
			defender.removeStatus(Status.SWITCHING);
			int slot = cheapReplacementSlot(otherSide, attacker);
			if (slot >= 0) performSwitch(otherSide, slot, attacker);
		}

		List<Branch> out = new ArrayList<>();
		out.add(br);
		return out;
	}

	private static List<Integer> eligibleSwitchIns(SideState side, Pokemon exclude) {
		List<Integer> out = new ArrayList<>();
		Pokemon[] team = side.bench();
		for (int i = 0; i < team.length; i++) {
			Pokemon p = team[i];
			if (p == null || p.isFainted() || p == exclude) continue;
			out.add(i);
		}
		return out;
	}

	// ---- Step 3: end of turn ----

	private static void endOfTurnPhase(SimState s) {
		Pokemon aMon = s.ai.active();
		Pokemon pMon = s.player.active();
		if (aMon.isFainted() || pMon.isFainted()) return;

		Pokemon faster = aMon.getFaster(pMon, 0, 0, s.field);
		Pokemon slower = faster == aMon ? pMon : aMon;
		// Aurora Glow's end-of-turn handler touches the whole team (Field.FieldEffect.handleAurora).
		for (SideState side : new SideState[] { s.ai, s.player }) {
			for (Field.FieldEffect fe : side.shell.getFieldEffectList()) {
				if (fe.effect == Field.Effect.AURORA_GLOW) ownAllBench(side.shell);
			}
		}
		try (SimContext.Scope scope = SimContext.enter(SimPolicy.DEFAULT)) {
			faster.endOfTurn(slower);
			if (!slower.isFainted() && !faster.isFainted()) slower.endOfTurn(faster);
			if (!aMon.isFainted() && !pMon.isFainted()) s.field.endOfTurn(faster, slower);
		}
	}

	// ---- Step 4: replacement for fainted actives ----

	private static void resolveReplacements(SimState s) {
		resolveReplacementsForSide(s.ai, s.player.active());
		resolveReplacementsForSide(s.player, s.ai.active());
	}

	private static void resolveReplacementsForSide(SideState side, Pokemon foe) {
		if (!side.active().isFainted()) return;
		if (!side.shell.hasValidMembers(foe)) return;
		int slot = cheapReplacementSlot(side, foe);
		if (slot >= 0) performSwitch(side, slot, foe);
	}

	/** §7.6 placeholder for aiReplacementHeuristic/playerReplacementHeuristic (non-recursive;
	 *  Phase 5 unifies this with the real battle's chooseReplacement). */
	static int cheapReplacementSlot(SideState side, Pokemon foe) {
		int best = -1;
		double bestScore = Double.NEGATIVE_INFINITY;
		Pokemon[] team = side.bench();
		for (int i = 0; i < team.length; i++) {
			Pokemon p = team[i];
			if (p == null || p.isFainted() || p == side.active()) continue;
			double hpFrac = p.currentHP * 1.0 / p.getStat(0);
			double matchup = 1.0 - Trainer.getEffective(p, foe, foe.type1, null, false);
			if (foe.type2 != null) matchup += 1.0 - Trainer.getEffective(p, foe, foe.type2, null, false);
			double score = matchup * 2.0 + hpFrac;
			if (score > bestScore) { bestScore = score; best = i; }
		}
		return best;
	}

	// ---- Merge ----

	private static List<Branch> mergeSimilar(List<Branch> branches, int budget) {
		if (branches.size() <= budget) return branches;
		branches.sort((a, b) -> Double.compare(b.prob, a.prob));
		List<Branch> kept = new ArrayList<>(branches.subList(0, budget - 1));
		double mergedProb = 0;
		for (int i = budget - 1; i < branches.size(); i++) mergedProb += branches.get(i).prob;
		Branch merged = branches.get(budget - 1);
		merged.prob = mergedProb;
		kept.add(merged);
		return kept;
	}
}