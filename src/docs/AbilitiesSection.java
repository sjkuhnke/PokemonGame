package docs;

import java.awt.Color;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import pokemon.Ability;

/**
 * "Abilities" sheet of the master workbook. This is the reference pattern
 * for the other sections: shared styles via ctx, register the entity's home
 * cell for hyperlinks, report progress per row.
 */
public class AbilitiesSection implements DocSection {

	// Chars-per-line estimate for wrapped description rows (11pt, column below)
	private static final int DESC_COL_CHARS = 100;

	@Override
	public String label() {
		return "Writing abilities...";
	}

	@Override
	public int weight() {
		return 2;
	}

	@Override
	public void write(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Abilities");
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 23000);
		sheet.createFreezePane(0, 1);

		StyleSpec headerStyle = DocStyles.header(12, HorizontalAlignment.LEFT);
		Row header = sheet.createRow(0);
		header.setHeightInPoints(20);
		ctx.text(header, 0, "Ability", headerStyle);
		ctx.text(header, 1, "Description", headerStyle);

		Ability[] all = Ability.values();
		for (int i = 0; i < all.length; i++) {
			Ability a = all[i];
			int rowIndex = i + 1;
			Color bg = rowIndex % 2 == 0 ? new Color(235, 235, 235) : Color.WHITE;
			Row row = sheet.createRow(rowIndex);

			StyleSpec nameStyle = DocStyles.text(true, 11, HorizontalAlignment.LEFT, VerticalAlignment.TOP).fill(bg);
			StyleSpec descStyle = DocStyles.wrapped(11).fill(bg);

			Cell nameCell = ctx.text(row, 0, a.toString(), nameStyle);
			ctx.text(row, 1, a.desc, descStyle);
			ctx.links.register(LinkRegistry.Kind.ABILITY, a, sheet, rowIndex, nameCell.getColumnIndex());

			DocUtils.setWrappedRowHeight(row, a.desc, DESC_COL_CHARS, 14f);
			ctx.progress.update(i + 1, all.length, a.toString());
		}
	}
}