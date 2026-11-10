package docs;

import java.awt.Color;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import pokemon.PType;
import pokemon.Pokemon;

/**
 * "Defensive Typings" and "Offensive Typings" sheets. The mono-type rows
 * ("FIRE - NONE") of the defensive sheet are the TYPE link targets; every
 * type name mentioned elsewhere links there.
 *
 * The calculation helpers stay in TypingsDoc (shared with the TXT writers).
 */
public class TypingsSection implements DocSection {

	private static final int LIST_COL_CHARS = 45;

	@Override
	public String label() {
		return "Writing type matchups...";
	}

	@Override
	public int weight() {
		return 4;
	}

	@Override
	public void write(DocContext ctx) {
		PType[] types = TypingsDoc.getUsableTypes();
		int total = types.length * types.length * 2;
		int[] done = {0};

		writeDefensive(ctx, types, total, done);
		writeOffensive(ctx, types, total, done);
	}

	private void writeDefensive(DocContext ctx, PType[] types, int total, int[] done) {
		Sheet sheet = ctx.newSheet("Defensive Typings");
		setupColumns(sheet);
		writeHeader(ctx, sheet, "Immune to", "Weak to (!! = 4x)", "Resists (!! = 1/4x)");

		Pokemon test = new Pokemon(1, 5, true, false);
		Pokemon foe = new Pokemon(4, 5, true, false);

		int rowIndex = 1;
		for (PType type1 : types) {
			for (PType type2 : types) {
				foe.type1 = type1;
				foe.type2 = type2;
				Map<PType, Double> eff = TypingsDoc.computeDefensiveMap(test, foe, types);

				String immune = TypingsDoc.joinTypes(eff, v -> v == 0.0, type1, type2, false);
				String weak = TypingsDoc.joinTypes(eff, v -> v >= 2.0, type1, type2, true);
				String resist = TypingsDoc.joinTypes(eff, v -> v < 1.0 && v != 0.0, type1, type2, true);

				Cell typingCell = writeRow(ctx, sheet, rowIndex, type1, type2, immune, weak, resist);
				if (type1 == type2) {
					ctx.links.register(LinkRegistry.Kind.TYPE, type1, sheet, rowIndex, 0);
				}
				rowIndex++;
				ctx.progress.update(++done[0], total, type1 + " / " + type2);
			}
		}
	}

	private void writeOffensive(DocContext ctx, PType[] types, int total, int[] done) {
		Sheet sheet = ctx.newSheet("Offensive Typings");
		setupColumns(sheet);
		writeHeader(ctx, sheet, "Deals 2x to (!! = 4x)", "Deals 1/2x to (!! = 1/4x)", "Deals 0x to");

		Pokemon test = new Pokemon(1, 5, true, false);
		Pokemon foe = new Pokemon(4, 5, true, false);

		int rowIndex = 1;
		for (PType type1 : types) {
			for (PType type2 : types) {
				Map<PType, Double> eff = TypingsDoc.computeOffensiveMap(test, foe, type1, type2, types);

				String deals2x = TypingsDoc.joinTypes(eff, v -> v >= 2.0, type1, type2, true);
				String dealsHalf = TypingsDoc.joinTypes(eff, v -> v < 1.0 && v != 0.0, type1, type2, true);
				String deals0x = TypingsDoc.joinTypes(eff, v -> v == 0.0, type1, type2, false);

				writeRow(ctx, sheet, rowIndex, type1, type2, deals2x, dealsHalf, deals0x);
				rowIndex++;
				ctx.progress.update(++done[0], total, type1 + " / " + type2);
			}
		}
	}

	private void setupColumns(Sheet sheet) {
		sheet.setColumnWidth(0, 5200);
		sheet.setColumnWidth(1, 8500);
		sheet.setColumnWidth(2, 8500);
		sheet.setColumnWidth(3, 8500);
		sheet.createFreezePane(0, 1);
	}

	private void writeHeader(DocContext ctx, Sheet sheet, String col1, String col2, String col3) {
		Row header = sheet.createRow(0);
		header.setHeightInPoints(20);
		String[] labels = {"Typing", col1, col2, col3};
		StyleSpec style = DocStyles.header(11, HorizontalAlignment.CENTER);
		for (int c = 0; c < labels.length; c++) {
			ctx.text(header, c, labels[c], style);
		}
	}

	private Cell writeRow(DocContext ctx, Sheet sheet, int rowIndex, PType type1, PType type2,
			String col1Text, String col2Text, String col3Text) {
		Row row = sheet.createRow(rowIndex);

		String label = (type1 == type2) ? type1.toString().toUpperCase() + " - NONE"
				: type1.toString().toUpperCase() + " - " + type2.toString().toUpperCase();
		Cell typingCell = ctx.text(row, 0, label,
				DocStyles.badge(type1.getColor(), 10, HorizontalAlignment.CENTER).wrap());

		ctx.text(row, 1, col1Text, listStyle(new Color(225, 225, 225)));
		ctx.text(row, 2, col2Text, listStyle(new Color(240, 205, 205)));
		ctx.text(row, 3, col3Text, listStyle(new Color(205, 230, 205)));

		String longest = col1Text.length() > col2Text.length()
				? (col1Text.length() > col3Text.length() ? col1Text : col3Text)
				: (col2Text.length() > col3Text.length() ? col2Text : col3Text);
		DocUtils.setWrappedRowHeight(row, longest, LIST_COL_CHARS, 13f);
		return typingCell;
	}

	private StyleSpec listStyle(Color bg) {
		return DocStyles.wrapped(10).fill(bg);
	}
}