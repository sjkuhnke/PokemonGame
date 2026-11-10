package docs;

import java.awt.Color;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import pokemon.PType;
import pokemon.Pokemon;

/**
 * The single "Type Chart" sheet (attacker down the rows, defender across).
 * The old per-file Excel docs each added their own copy; the master workbook
 * only needs one. Row/column labels link to the type's Defensive Typings row.
 */
public class TypeChartSection implements DocSection {

	@Override
	public String label() {
		return "Writing type chart...";
	}

	@Override
	public int weight() {
		return 1;
	}

	@Override
	public void write(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Type Chart");
		PType[] types = TypingsDoc.getUsableTypes();

		Pokemon atkMon = new Pokemon(1, 5, true, false);
		Pokemon defMon = new Pokemon(4, 5, true, false);

		sheet.setColumnWidth(0, 3600);
		for (int c = 0; c < types.length; c++) {
			sheet.setColumnWidth(c + 1, 1500);
		}

		Row headerRow = sheet.createRow(0);
		headerRow.setHeightInPoints(70);

		ctx.text(headerRow, 0, "ATK \\ DEF",
				StyleSpec.plain().bold().size(9).textColor(Color.WHITE).fill(new Color(50, 50, 50))
						.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER));

		for (int c = 0; c < types.length; c++) {
			Cell cell = ctx.text(headerRow, c + 1, types[c].toString().toUpperCase(),
					typeLabel(types[c].getColor(), true));
			ctx.links.link(cell, LinkRegistry.Kind.TYPE, types[c]);
		}

		for (int r = 0; r < types.length; r++) {
			PType atkType = types[r];
			Row row = sheet.createRow(r + 1);
			row.setHeightInPoints(16);

			Cell rowLabel = ctx.text(row, 0, atkType.toString().toUpperCase(),
					typeLabel(atkType.getColor(), false));
			ctx.links.link(rowLabel, LinkRegistry.Kind.TYPE, atkType);

			for (int c = 0; c < types.length; c++) {
				double m = computeMultiplier(atkMon, defMon, atkType, types[c]);
				writeMultiplierCell(ctx, row, c + 1, m);
			}
			ctx.progress.update(r + 1, types.length, atkType.toString());
		}

		sheet.createFreezePane(1, 1);
	}

	// `test` throws the attack, `foe` takes on the defending type, and
	// getImmune/getResistances/getWeaknesses do the lookup against the game's
	// type data (same logic the TXT type docs use).
	private static double computeMultiplier(Pokemon test, Pokemon foe, PType atkType, PType defType) {
		foe.type1 = defType;
		foe.type2 = null;

		if (test.getImmune(foe, atkType)) return 0.0;

		double multiplier = 1.0;
		for (PType t : test.getResistances(atkType)) {
			if (defType == t) multiplier /= 2;
		}
		for (PType t : test.getWeaknesses(atkType)) {
			if (defType == t) multiplier *= 2;
		}
		return multiplier;
	}

	private StyleSpec typeLabel(Color bg, boolean rotated) {
		StyleSpec s = StyleSpec.plain().bold().size(8).fillWithContrastText(bg)
				.align(HorizontalAlignment.CENTER)
				.valign(rotated ? VerticalAlignment.BOTTOM : VerticalAlignment.CENTER);
		return rotated ? s.rotate(90) : s;
	}

	// 2x -> bullseye, 0.5x -> triangle, 0x -> X, 1x blank (same glyphs as before)
	private void writeMultiplierCell(DocContext ctx, Row row, int col, double m) {
		String text;
		Color bg;
		Color fg = new Color(30, 30, 30);
		if (m == 0.0) {
			text = "✖";
			bg = new Color(70, 70, 70);
			fg = Color.WHITE;
		} else if (m >= 2.0) {
			text = "◎";
			bg = new Color(150, 215, 150);
		} else if (m > 0 && m < 1.0) {
			text = "▲";
			bg = new Color(235, 175, 175);
		} else {
			text = "";
			bg = new Color(248, 248, 248);
		}
		ctx.text(row, col, text, StyleSpec.plain().bold().size(11).textColor(fg).fill(bg)
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER));
	}
}