package docs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pokemon.Move;
import pokemon.PType;
import pokemon.Pokemon;

public class MovesDoc {

	static Map<PType, List<Move>> groupMovesByType() {
		ArrayList<Move> moves = new ArrayList<>(Arrays.asList(Move.values()));
		Map<PType, List<Move>> movesByType = new HashMap<>();
		for (Move m : moves) {
			movesByType.computeIfAbsent(m.mtype, k -> new ArrayList<>()).add(m);
		}
		for (List<Move> typeMoves : movesByType.values()) {
			typeMoves.sort(Comparator.comparing(Move::toString));
		}
		return movesByType;
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeMoves); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeMovesToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("MovesInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());

			Map<PType, List<Move>> movesByType = groupMovesByType();

			List<PType> sortedTypes = new ArrayList<>(movesByType.keySet());
			sortedTypes.sort(Comparator.comparing(PType::toString));

			for (PType type : sortedTypes) {

				writer.write("=============================================================================================================\n");
				writer.write("												  " + type.toString() + "\n");
				writer.write("=============================================================================================================\n");

				List<Move> typeMoves = movesByType.get(type);
				for (Move m : typeMoves) {
					String move = " " + m.toString();
					while (move.length() < 20) {
						move += " ";
					}
					String cat = " " + m.getCategory();
					while (cat.length() < 10) {
						cat += " ";
					}
					String bp = " " + m.formatbp(null, null, Pokemon.field);
					while (bp.length() < 5) {
						bp += " ";
					}
					String acc = " " + m.getAccuracy(null, null, Pokemon.field);
					while (acc.length() < 5) {
						acc += " ";
					}
					String pp = " " + m.pp + " PP ";
					while (pp.length() < 7) {
						pp += " ";
					}
					writer.write(String.format("%s|%s|%s|%s|%s: %s\n", move, cat, bp, acc, pp, m.getDescription(null)));
				}
			}

			writer.write("\n=================================\n");
			writer.write("IVs | Hidden Power Type");
			writer.write("\n=================================\n");

			Pokemon test = new Pokemon(1, 5, true, false);

			for (int i = 0; i < 64; i++) {
				int[] ivs = new int[6];
				for (int j = 0; j < 6; j++) {
					ivs[j] = (i & (1 << j)) != 0 ? 31 : 30;
				}

				test.ivs = ivs;
				PType type = test.determineHPType();
				writer.write(String.format("%s | %s\n", Arrays.toString(test.ivs), type.toString()));
			}

			writer.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}