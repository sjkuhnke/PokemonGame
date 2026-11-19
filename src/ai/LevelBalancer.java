package ai;

import java.io.*;
import java.util.*;
import pokemon.*;
import pokemon.Set;
import util.Print;
import util.Rng;

public final class LevelBalancer {
	private LevelBalancer() {}

	private static final class Row {
		int[] a = new int[3], b = new int[3];   // set indices per side
		int[] la = new int[3], lb = new int[3]; // levels actually played
		double y;                                // side A's score (1 / 0.5 / 0)
	}

	public static void run(int passes, int pairsPerPass, int jitter, double damping, String outFile) {
		if (Pokemon.sets.isEmpty()) Pokemon.loadCompetitiveSets();
		List<Set> all = new ArrayList<>();
		for (ArrayList<Set> l : Pokemon.sets.values()) all.addAll(l);
		Map<Set, Integer> idx = new IdentityHashMap<>();
		for (int i = 0; i < all.size(); i++) idx.put(all.get(i), i);
		int n = all.size();

		boolean wasSeeded = Rng.isSeeded(); long prevSeed = Rng.getSeed();
		boolean prevSupp = Print.isDebugSuppressed();
		Print.setDebugSuppressed(true);
		try {
			for (int pass = 0; pass < passes; pass++) {
				List<Row> rows = new ArrayList<>();
				int[] count = new int[n];
				long t0 = System.nanoTime();

				for (int k = 0; k < pairsPerPass; k++) {
					long seed = 20261112L + pass * 1_000_003L + k * 7919L;
					Rng.setSeed(seed);
					Random r = Rng.asRandom();

					Set[][] picks = new Set[2][3];
					int[][] lv = new int[2][3];
					for (int side = 0; side < 2; side++) {
						List<Integer> used = new ArrayList<>();
						for (int j = 0; j < 3; j++) {
							Set s = Pokemon.pickCompetitiveSet(used, r);
							used.add(s.getId());
							picks[side][j] = s;
							int l = s.getLevel() + (jitter > 0 ? r.nextInt(2 * jitter + 1) - jitter : 0);
							lv[side][j] = Math.max(1, Math.min(100, l));
						}
					}
					Trainer x = build("A", 1, picks[0], lv[0]);
					Trainer y = build("B", 2, picks[1], lv[1]);

					SelfPlay.BattleResult r1 = SelfPlay.playBattle(x, y, AIV2.INSTANCE, AIV2.INSTANCE, Player.HARD, Player.HARD, seed, 300);
					SelfPlay.BattleResult r2 = SelfPlay.playBattle(y, x, AIV2.INSTANCE, AIV2.INSTANCE, Player.HARD, Player.HARD, seed + 1, 300);
					if (r1.error != null || r2.error != null) continue;

					double s1 = r1.winner < 0 ? 0.5 : (r1.winner == 0 ? 1 : 0);   // x was seat 1
					double s2 = r2.winner < 0 ? 0.5 : (r2.winner == 1 ? 1 : 0);   // x was seat 2

					Row row = new Row();
					row.y = (s1 + s2) / 2;
					for (int j = 0; j < 3; j++) {
						row.a[j] = idx.get(picks[0][j]); row.la[j] = lv[0][j];
						row.b[j] = idx.get(picks[1][j]); row.lb[j] = lv[1][j];
						count[row.a[j]]++; count[row.b[j]]++;
					}
					rows.add(row);
					if ((k + 1) % 200 == 0) System.out.printf("[balance] pass %d: %d/%d pairs, %.0fs%n", pass, k + 1, pairsPerPass, (System.nanoTime() - t0) / 1e9);
				}

				double[] w = fit(rows, n, 1e-3);
				double B = w[n];
				if (B <= 1e-4) { System.out.println("B=" + B + " is not positive: need more data or more jitter"); return; }

				double rmin = Double.MAX_VALUE;
				for (int i = 0; i < n; i++) if (count[i] >= 100) rmin = Math.min(rmin, w[i]);

				double[] eff = new double[n]; int m = 0; double mean = 0;
				for (int i = 0; i < n; i++) {
					if (count[i] < 100) continue;
					eff[i] = w[i] + B * (all.get(i).getLevel() - 100);
					mean += eff[i]; m++;
				}
				mean /= m;
				double var = 0;
				for (int i = 0; i < n; i++) if (count[i] >= 100) var += (eff[i] - mean) * (eff[i] - mean);
				System.out.printf("[balance] pass %d: B=%.4f logit/level, effective-rating sd=%.3f logit%n", pass, B, Math.sqrt(var / m));

				for (int i = 0; i < n; i++) {
					if (count[i] < 100) continue;      // not enough data: leave alone
					Set s = all.get(i);
					double target = 100 - (w[i] - rmin) / B;
					double next = s.getLevel() + damping * (target - s.getLevel());
					s.setLevel((int) Math.round(Math.min(100, Math.max(40, next))));   // floor of 40 is a tunable
				}
				save(all, outFile);   // checkpoint after every pass
			}
		} finally {
			Print.setDebugSuppressed(prevSupp);
			if (wasSeeded) Rng.setSeed(prevSeed); else Rng.randomize();
		}
	}

	private static Trainer build(String name, int seat, Set[] picks, int[] lv) {
		Pokemon[] team = new Pokemon[3]; Item[] items = new Item[3];
		for (int i = 0; i < 3; i++) { team[i] = picks[i].makePokemon(lv[i]); items[i] = team[i].item; }
		return new Trainer(name, team, items, 0, seat);
	}

	/** Adam on the logistic loss with soft labels. w[0..n-1] = R_i, w[n] = B. */
	private static double[] fit(List<Row> rows, int n, double l2) {
		double[] w = new double[n + 1], m = new double[n + 1], v = new double[n + 1], g = new double[n + 1];
		double lr = 0.05, b1 = 0.9, b2 = 0.999, N = rows.size();
		for (int it = 1; it <= 3000; it++) {
			Arrays.fill(g, 0);
			for (Row r : rows) {
				double z = 0, dl = 0;
				for (int j = 0; j < 3; j++) {
					z += w[r.a[j]] - w[r.b[j]];
					dl += (r.la[j] - 100) - (r.lb[j] - 100);
				}
				z += w[n] * dl;
				double e = 1 / (1 + Math.exp(-z)) - r.y;
				for (int j = 0; j < 3; j++) { g[r.a[j]] += e; g[r.b[j]] -= e; }
				g[n] += e * dl;
			}
			for (int i = 0; i <= n; i++) {
				g[i] = g[i] / N + (i < n ? 2 * l2 * w[i] : 0);
				m[i] = b1 * m[i] + (1 - b1) * g[i];
				v[i] = b2 * v[i] + (1 - b2) * g[i] * g[i];
				w[i] -= lr * (m[i] / (1 - Math.pow(b1, it))) / (Math.sqrt(v[i] / (1 - Math.pow(b2, it))) + 1e-8);
			}
		}
		return w;
	}

	private static void save(List<Set> all, String file) {
		try (PrintWriter pw = new PrintWriter(file)) {
			Map<Integer, Integer> seen = new HashMap<>();
			for (Set s : all) {
				int k = seen.merge(s.getId(), 1, Integer::sum) - 1;   // index within species
				pw.println(s.getId() + "," + k + "," + s.getLevel() + "  # " + Pokemon.getName(s.getId()));
			}
		} catch (IOException e) { e.printStackTrace(); }
	}
}