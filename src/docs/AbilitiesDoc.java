package docs;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pokemon.Ability;

public class AbilitiesDoc {

	// Chars-per-line estimate used to size wrapped description rows -
	// tuned to the description column width set below at 11pt font.
	private static final int DESC_COL_CHARS = 100;

	public static void writeAbilitiesToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Abilities");

	    sheet.setColumnWidth(0, 6000);
	    sheet.setColumnWidth(1, 23000);
	    sheet.createFreezePane(0, 1);

	    Row header = sheet.createRow(0);
	    header.setHeightInPoints(20);
	    Cell nameHeader = header.createCell(0);
	    nameHeader.setCellValue("Ability");
	    nameHeader.setCellStyle(headerStyle(wb));
	    Cell descHeader = header.createCell(1);
	    descHeader.setCellValue("Description");
	    descHeader.setCellStyle(headerStyle(wb));

	    Ability[] allAbilities = Ability.values();
	    int rowIndex = 1;
	    for (Ability a : allAbilities) {
	        boolean shaded = rowIndex % 2 == 0;
	        Row row = sheet.createRow(rowIndex);

	        Cell nameCell = row.createCell(0);
	        nameCell.setCellValue(a.toString());
	        nameCell.setCellStyle(nameStyle(wb, shaded));

	        Cell descCell = row.createCell(1);
	        descCell.setCellValue(a.desc);
	        descCell.setCellStyle(descStyle(wb, shaded));

	        DocUtils.setWrappedRowHeight(row, a.desc, DESC_COL_CHARS, 14f);

	        rowIndex++;
	    }

	    Path outPath = dir.resolve("AbilitiesInfo.xlsx");
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeAbilities); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeAbilitiesToTxt(Path dir) {
	    try {
	        Path outPath = dir.resolve("AbilitiesInfo.txt");
	        FileWriter writer = new FileWriter(outPath.toFile());
	        writer.write("--------------------------------------\n");
	        writer.write("Abilities Info:\n");
	        writer.write("--------------------------------------\n");
	        Ability[] allAbilities = Ability.values();
	        for (Ability a : allAbilities) {
	            String ability = a.toString();
	            while (ability.length() < 18) {
	                ability += " ";
	            }
	            String desc = a.desc;
	            writer.write(String.format("%s | %s\n", ability, desc));
	        }

	        writer.close();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Styles
	// ----------------------------------------------------------------------
	private static CellStyle headerStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 12);
	    font.setColor(new XSSFColor(Color.WHITE, null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle nameStyle(Workbook wb, boolean shaded) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(shaded ? new Color(235, 235, 235) : Color.WHITE, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.TOP);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 11);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle descStyle(Workbook wb, boolean shaded) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(shaded ? new Color(235, 235, 235) : Color.WHITE, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.TOP);
	    style.setWrapText(true);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) 11);
	    style.setFont(font);

	    return style;
	}
}