package docs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import pokemon.PType;
import pokemon.Pokemon;

/**
 * Draws a classic single-type-vs-single-type effectiveness chart (attacking
 * type down the rows, defending type across the columns). The underlying
 * multiplier grid is identical whether you're framing it as "what does this
 * type hit hard" (offensive) or "what hits this type hard" (defensive), so
 * both TypingsDoc excel writers call the one public method here to add it
 * as their second sheet.
 */
public class TypeChart {

	public static void writeTypeChart(Workbook wb, String sheetName) {
	    Sheet sheet = wb.createSheet(sheetName);

	    PType[] types = getUsableTypes();

	    Pokemon atkMon = new Pokemon(1, 5, true, false);
	    Pokemon defMon = new Pokemon(4, 5, true, false);

	    sheet.setColumnWidth(0, 3600);
	    for (int c = 0; c < types.length; c++) {
	        sheet.setColumnWidth(c + 1, 1500);
	    }

	    Row headerRow = sheet.createRow(0);
	    headerRow.setHeightInPoints(70);

	    Cell corner = headerRow.createCell(0);
	    corner.setCellValue("ATK \\ DEF");
	    corner.setCellStyle(cornerStyle(wb));

	    for (int c = 0; c < types.length; c++) {
	        Cell cell = headerRow.createCell(c + 1);
	        cell.setCellValue(types[c].toString().toUpperCase());
	        cell.setCellStyle(typeStyle(wb, types[c].getColor(), true, (short) 90));
	    }

	    for (int r = 0; r < types.length; r++) {
	        PType atkType = types[r];
	        Row row = sheet.createRow(r + 1);
	        row.setHeightInPoints(16);

	        Cell rowLabel = row.createCell(0);
	        rowLabel.setCellValue(atkType.toString().toUpperCase());
	        rowLabel.setCellStyle(typeStyle(wb, atkType.getColor(), false, (short) 0));

	        for (int c = 0; c < types.length; c++) {
	            PType defType = types[c];
	            double multiplier = computeMultiplier(atkMon, defMon, atkType, defType);
	            Cell cell = row.createCell(c + 1);
	            writeMultiplierCell(wb, cell, multiplier);
	        }
	    }

	    sheet.createFreezePane(1, 1);
	}

	private static PType[] getUsableTypes() {
	    PType[] types = PType.values();
	    ArrayList<PType> list = new ArrayList<>(Arrays.asList(types));
	    list.remove(PType.UNKNOWN);
	    return list.toArray(new PType[0]);
	}

	// Mirrors the per-cell logic Main.java already uses to build the txt
	// docs: `test` throws the attack, `foe`'s typing is set to the
	// defending type for this cell, and getImmune/getResistances/
	// getWeaknesses do the actual lookup against the game's type data.
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

	// 2x -> filled circle-with-dot look-alike ("@"), 0.5x -> triangle-ish
	// marker ("▲"), 0x -> "X". These Unicode glyphs render fine in a cell
	// without needing an image asset; swap for DocUtils.insertImage calls
	// if you'd rather use the in-game circle/triangle/X icon sprites.
	private static void writeMultiplierCell(Workbook wb, Cell cell, double multiplier) {
	    String text;
	    Color bg;
	    Color fg = new Color(30, 30, 30);
	    if (multiplier == 0.0) {
	        text = "\u2716"; // heavy X
	        bg = new Color(70, 70, 70);
	        fg = Color.WHITE;
	    } else if (multiplier >= 2.0) {
	        text = "\u25CE"; // bullseye / circle with dot
	        bg = new Color(150, 215, 150);
	    } else if (multiplier > 0 && multiplier < 1.0) {
	        text = "\u25B2"; // triangle
	        bg = new Color(235, 175, 175);
	    } else {
	        text = ""; // neutral 1x - left blank so the chart stays readable
	        bg = new Color(248, 248, 248);
	    }
	    cell.setCellValue(text);
	    cell.setCellStyle(multiplierStyle(wb, bg, fg));
	}

	// ------------------------------------------------------------------
	// Styles
	// ------------------------------------------------------------------
	private static CellStyle cornerStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(50, 50, 50), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 9);
	    font.setColor(new XSSFColor(Color.WHITE, null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle typeStyle(Workbook wb, Color bg, boolean rotated, short rotation) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(rotated ? VerticalAlignment.BOTTOM : VerticalAlignment.CENTER);
	    if (rotated) style.setRotation(rotation);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 8);
	    font.setColor(new XSSFColor(textColorFor(bg), null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle multiplierStyle(Workbook wb, Color bg, Color fg) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 11);
	    font.setColor(new XSSFColor(fg, null));
	    style.setFont(font);

	    return style;
	}

	private static Color textColorFor(Color bg) {
	    double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
	    return luminance > 150 ? new Color(20, 20, 20) : Color.WHITE;
	}
}