package docs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import pokemon.Move;
import pokemon.PType;
import pokemon.Pokemon;

/**
 * "Moves" sheet (grouped by type) and "Hidden Power" sheet.
 * Registers every move as a MOVE link target; type banners and Hidden Power
 * type cells link to the TYPE target (registered by TypingsSection).
 */
public class MovesSection implements DocSection {

	// name | category | bp | acc | pp | description
	private static final int COL_NAME = 0, COL_CAT = 1, COL_BP = 2, COL_ACC = 3, COL_PP = 4, COL_DESC = 5;
	private static final int LAST_COL = COL_DESC;
	private static final int DESC_COL_CHARS = 85;

	@Override
	public String label() {
		return "Writing moves...";
	}

	@Override
	public int weight() {
		return 8;
	}

	@Override
	public void write(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Moves");
		sheet.setColumnWidth(COL_NAME, 5200);
		sheet.setColumnWidth(COL_CAT, 2800);
		sheet.setColumnWidth(COL_BP, 1800);
		sheet.setColumnWidth(COL_ACC, 1800);
		sheet.setColumnWidth(COL_PP, 1800);
		sheet.setColumnWidth(COL_DESC, 20000);
		sheet.createFreezePane(0, 1);

		Map<PType, List<Move>> movesByType = MovesDoc.groupMovesByType();
		List<PType> sortedTypes = new ArrayList<>(movesByType.keySet());
		sortedTypes.sort(Comparator.comparing(PType::toString));

		int total = Move.values().length;
		int done = 0;
		int rowIndex = 0;
		for (PType type : sortedTypes) {
			rowIndex = writeTypeHeader(ctx, sheet, type, rowIndex);
			rowIndex = writeColumnLabels(ctx, sheet, rowIndex);
			for (Move m : movesByType.get(type)) {
				rowIndex = writeMoveRow(ctx, sheet, m, rowIndex);
				ctx.progress.update(++done, total, m.toString());
			}
			rowIndex++; // spacer row between type blocks
		}

		writeHiddenPowerSheet(ctx);
	}

	private int writeTypeHeader(DocContext ctx, Sheet sheet, PType type, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(26);
		StyleSpec style = DocStyles.badge(type.getColor(), 13, HorizontalAlignment.CENTER);

		// icon lives in the first column, label in the rest
		ctx.text(row, COL_NAME, "", style);
		Cell nameCell = ctx.text(row, COL_NAME + 1, type.toString().toUpperCase(), style);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, COL_NAME + 1, LAST_COL));
		// UNKNOWN (Struggle) has no row on the Typings sheet, so there is nothing to link to
		if (type != PType.UNKNOWN) ctx.links.link(nameCell, LinkRegistry.Kind.TYPE, type);

		ctx.image(sheet, "type:" + type.name(), type::getImage2, COL_NAME, rowIndex, 1, 1, 0.6, 0.6);
		return rowIndex + 1;
	}

	private int writeColumnLabels(DocContext ctx, Sheet sheet, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		String[] labels = new String[6];
		labels[COL_NAME] = "Move";
		labels[COL_CAT] = "Category";
		labels[COL_BP] = "BP";
		labels[COL_ACC] = "Acc";
		labels[COL_PP] = "PP";
		labels[COL_DESC] = "Description";
		StyleSpec style = DocStyles.columnLabel(10);
		for (int c = 0; c <= LAST_COL; c++) {
			ctx.text(row, c, labels[c], style);
		}
		return rowIndex + 1;
	}

	private int writeMoveRow(DocContext ctx, Sheet sheet, Move m, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		StyleSpec left = DocStyles.text(true, 10, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		StyleSpec center = DocStyles.text(false, 10, HorizontalAlignment.CENTER, VerticalAlignment.TOP);

		ctx.text(row, COL_NAME, m.toString(), left);
		ctx.links.register(LinkRegistry.Kind.MOVE, m, sheet, rowIndex, COL_NAME);

		ctx.text(row, COL_CAT, m.getCategory().toString(), center);

		// set values directly: formatbp/getAccuracy/pp keep whatever type they have today
		Cell bpCell = row.createCell(COL_BP);
		bpCell.setCellValue(m.formatbp(null, null, Pokemon.field));
		bpCell.setCellStyle(ctx.style(center));

		Cell accCell = row.createCell(COL_ACC);
		accCell.setCellValue(m.getAccuracy(null, null, Pokemon.field));
		accCell.setCellStyle(ctx.style(center));

		Cell ppCell = row.createCell(COL_PP);
		ppCell.setCellValue(m.pp);
		ppCell.setCellStyle(ctx.style(center));

		String desc = m.getDescription(null);
		ctx.text(row, COL_DESC, desc, DocStyles.wrapped(10));
		DocUtils.setWrappedRowHeight(row, desc, DESC_COL_CHARS, 13f);

		return rowIndex + 1;
	}

	// ----------------------------------------------------------------------
	// IVs -> Hidden Power type
	// ----------------------------------------------------------------------
	private void writeHiddenPowerSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Hidden Power");
		sheet.setColumnWidth(0, 7000);
		sheet.setColumnWidth(1, 4000);
		sheet.createFreezePane(0, 1);

		Row header = sheet.createRow(0);
		header.setHeightInPoints(18);
		StyleSpec labelStyle = DocStyles.columnLabel(10);
		ctx.text(header, 0, "IVs (HP/Atk/Def/SpA/SpD/Spe)", labelStyle);
		ctx.text(header, 1, "Hidden Power Type", labelStyle);

		StyleSpec plain = DocStyles.text(false, 10, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		Pokemon test = new Pokemon(1, 5, true, false);
		for (int i = 0; i < 64; i++) {
			int[] ivs = new int[6];
			for (int j = 0; j < 6; j++) {
				ivs[j] = (i & (1 << j)) != 0 ? 31 : 30;
			}
			test.ivs = ivs;
			PType hpType = test.determineHPType();

			Row row = sheet.createRow(i + 1);
			ctx.text(row, 0, Arrays.toString(ivs), plain);
			Cell typeCell = ctx.text(row, 1, hpType.toString().toUpperCase(),
					DocStyles.badge(hpType.getColor(), 13, HorizontalAlignment.CENTER));
			ctx.links.link(typeCell, LinkRegistry.Kind.TYPE, hpType);
		}
	}
}