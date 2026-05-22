package docs;

import pokemon.*;
import pokemon.Nursery.EggGroup;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.util.CellRangeAddress;
import java.io.FileOutputStream;
import java.util.ArrayList;

import java.nio.file.Path;

public class PokemonDoc {
	public static void writePokemonToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Pokemon Info");
	 
	    // ── Shared reusable styles ─────────────────────────────────────────────────
	    // Section label style (e.g. "Type:", "Ability:", "Base Stats:")
	    XSSFCellStyle labelStyle = makePokemonStyle(wb,
	            new java.awt.Color(50, 50, 50), new java.awt.Color(220, 220, 220),
	            true, false, 10);
	    labelStyle.setAlignment(HorizontalAlignment.LEFT);
	    labelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    setBorder(labelStyle, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex());
	 
	    // Plain data value style
	    XSSFCellStyle valueStyle = makePokemonStyle(wb,
	            new java.awt.Color(30, 30, 30), new java.awt.Color(255, 255, 255),
	            false, false, 10);
	    valueStyle.setAlignment(HorizontalAlignment.LEFT);
	    valueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    valueStyle.setWrapText(true);
	    setBorder(valueStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
	 
	    // Move list style (slightly indented look, italic)
	    XSSFCellStyle moveStyle = makePokemonStyle(wb,
	            new java.awt.Color(60, 60, 80), new java.awt.Color(245, 245, 255),
	            false, true, 9);
	    moveStyle.setAlignment(HorizontalAlignment.LEFT);
	    moveStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    moveStyle.setWrapText(true);
	    setBorder(moveStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
	 
	    // Misc info row style (weight / catch rate / egg data)
	    XSSFCellStyle miscStyle = makePokemonStyle(wb,
	            new java.awt.Color(30, 30, 30), new java.awt.Color(235, 245, 235),
	            false, false, 10);
	    miscStyle.setAlignment(HorizontalAlignment.LEFT);
	    miscStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    setBorder(miscStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
	 
	    // Evolution row style
	    XSSFCellStyle evoStyle = makePokemonStyle(wb,
	            new java.awt.Color(80, 50, 130), new java.awt.Color(240, 235, 255),
	            false, true, 10);
	    evoStyle.setAlignment(HorizontalAlignment.LEFT);
	    evoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    setBorder(evoStyle, BorderStyle.THIN, IndexedColors.GREY_25_PERCENT.getIndex());
	 
	    // ── Column widths ──────────────────────────────────────────────────────────
	    // Col 0: label (e.g. "Type:")       Col 1: value (spans rest of card width)
	    sheet.setColumnWidth(0, 14 * 256);   // ~14 chars
	    sheet.setColumnWidth(1, 38 * 256);   // value / move list
	 
	    // ── Build Pokemon ID list (mirrors writePokemon logic exactly) ─────────────
	    int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE + 2];
	    int counter = 0;
	    for (Pokemon p : Player.pokedex1) {
	        ids[counter] = p.getID();
	        counter++;
	        if (p.getID() == 150) ids[counter++] = 237;
	        if (p.getID() == 290) ids[counter++] = 291;
	    }
	    for (Pokemon p : Player.pokedex2) { ids[counter] = p.getID(); counter++; }
	    for (Pokemon p : Player.pokedex3) { ids[counter] = p.getID(); counter++; }
	    for (Pokemon p : Player.pokedex4) { ids[counter] = p.getID(); counter++; }
	 
	    int rowIndex = 0;
	 
	    for (int id : ids) {
	        if (id == 0) continue;
	        Pokemon p = new Pokemon(id, 5, false, false);
	 
	        // ── Type1 drives the header color ──────────────────────────────────
	        java.awt.Color headerBg = p.type1.getColor();
	        // Decide white vs dark text based on luminance
	        double lum = 0.2126 * headerBg.getRed() + 0.7152 * headerBg.getGreen() + 0.0722 * headerBg.getBlue();
	        java.awt.Color headerFg = lum > 140 ? new java.awt.Color(20, 20, 20) : java.awt.Color.WHITE;
	 
	        XSSFCellStyle headerStyle = makePokemonStyle(wb, headerFg, headerBg, true, false, 13);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);
	        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        setBorder(headerStyle, BorderStyle.MEDIUM, IndexedColors.BLACK.getIndex());
	 
	        // ── Header row: "#001 - Twigle  [001]" ────────────────────────────
	        Row headerRow = sheet.createRow(rowIndex++);
	        headerRow.setHeightInPoints(22);
	        String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
	        String idTag = Pokemon.getFormattedDexNo(p.getID()).replace('#', '[') + "]";
	        Cell headerCell = headerRow.createCell(0);
	        headerCell.setCellValue(dexNo + " - " + p.name() + "   " + idTag);
	        headerCell.setCellStyle(headerStyle);
	        sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
	 
	        // ── Type row ───────────────────────────────────────────────────────
	        String typeVal = p.type2 == null
	                ? p.type1.toString() + " / None"
	                : p.type1.toString() + " / " + p.type2.toString();
	        rowIndex = addInfoRow(sheet, rowIndex, "Type", typeVal, labelStyle, valueStyle);
	 
	        // ── Ability row ────────────────────────────────────────────────────
	        p.setAbility(0);
	        Ability ability1 = p.ability;
	        p.setAbility(1);
	        String ab2 = (p.ability == ability1) ? "None" : p.ability.toString();
	        p.setAbility(2);
	        String abH = (p.ability == Ability.NULL || p.ability == ability1) ? "None" : p.ability.toString();
	        String abilityVal = ability1 + " / " + ab2 + " / (" + abH + ")";
	        rowIndex = addInfoRow(sheet, rowIndex, "Ability", abilityVal, labelStyle, valueStyle);
	 
	        // ── Base Stats row ─────────────────────────────────────────────────
	        StringBuilder statsBuilder = new StringBuilder();
	        for (int j = 0; j < p.baseStats.length; j++) {
	            statsBuilder.append(p.getBaseStat(j)).append(" ").append(Pokemon.getStatType(j, false)).append("/ ");
	        }
	        statsBuilder.append(p.getBST()).append(" BST");
	        rowIndex = addInfoRow(sheet, rowIndex, "Base Stats", statsBuilder.toString(), labelStyle, valueStyle);
	 
	        // ── Level-Up Moves ─────────────────────────────────────────────────
	        Node[] movebank = p.getMovebank();
	        StringBuilder movesBuilder = new StringBuilder();
	        for (int j = 0; j < movebank.length; j++) {
	            Node n = movebank[j];
	            while (n != null) {
	                if (movesBuilder.length() > 0) movesBuilder.append(",  ");
	                movesBuilder.append(j == 0 ? "E" : j).append(" - ").append(n.data.toString());
	                n = n.next;
	            }
	        }
	        rowIndex = addInfoRow(sheet, rowIndex, "Level Up", movesBuilder.toString(), labelStyle, moveStyle);
	 
	        // ── Evolutions (only if applicable) ───────────────────────────────
	        if (p.canEvolve()) {
	            rowIndex = addInfoRow(sheet, rowIndex, "Evolutions", p.getEvolveString(), labelStyle, evoStyle);
	        }
	 
	        // ── Misc info (weight, catch rate, egg data) ───────────────────────
	        ArrayList<EggGroup> eggGroups = Pokemon.getEggGroup(id);
	        String eggGroupStr = eggGroups.get(0).equals(eggGroups.get(1))
	                ? eggGroups.get(0).toString()
	                : eggGroups.get(0) + ", " + eggGroups.get(1);
	 
	        String miscVal = String.format("%.1f lbs   |   Catch Rate: %d   |   Egg Cycles: %d   |   Egg Group(s): %s",
	                p.weight, p.catchRate, Egg.computeEggCycles(p.getFinalEvolution()), eggGroupStr);
	        rowIndex = addInfoRow(sheet, rowIndex, "Misc", miscVal, labelStyle, miscStyle);
	 
	        // ── Spacer row between entries ─────────────────────────────────────
	        Row spacer = sheet.createRow(rowIndex++);
	        spacer.setHeightInPoints(6);
	        XSSFCellStyle spacerStyle = makePokemonStyle(wb,
	                java.awt.Color.WHITE, new java.awt.Color(200, 200, 200), false, false, 8);
	        Cell spacerCell = spacer.createCell(0);
	        spacerCell.setCellStyle(spacerStyle);
	        sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
	    }
	 
	    // ── Freeze the top row and write the file ─────────────────────────────────
	    sheet.createFreezePane(0, 0);
	 
	    Path outPath = dir.resolve("PokemonInfo.xlsx");
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	 
	// ── Helper: write a two-cell label/value row, returns next rowIndex ───────────
	private static int addInfoRow(Sheet sheet, int rowIndex,
	        String label, String value,
	        CellStyle labelStyle, CellStyle valueStyle) {
	    Row row = sheet.createRow(rowIndex++);
	    row.setHeightInPoints(16);
	 
	    Cell lbl = row.createCell(0);
	    lbl.setCellValue(label);
	    lbl.setCellStyle(labelStyle);
	 
	    Cell val = row.createCell(1);
	    val.setCellValue(value);
	    val.setCellStyle(valueStyle);
	 
	    return rowIndex;
	}
	 
	// ── Helper: create a fully configured XSSFCellStyle ───────────────────────────
	private static XSSFCellStyle makePokemonStyle(Workbook wb,
	        java.awt.Color fgColor, java.awt.Color bgColor,
	        boolean bold, boolean italic, int fontSize) {
	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) fontSize);
	    font.setBold(bold);
	    font.setItalic(italic);
	    font.setFontName("Arial");
	    font.setColor(new XSSFColor(fgColor, null));
	 
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFont(font);
	    style.setFillForegroundColor(new XSSFColor(bgColor, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    return style;
	}
	 
	// ── Helper: apply a uniform border to all four sides of a style ───────────────
	private static void setBorder(XSSFCellStyle style, BorderStyle borderStyle, short color) {
	    style.setBorderTop(borderStyle);
	    style.setBorderBottom(borderStyle);
	    style.setBorderLeft(borderStyle);
	    style.setBorderRight(borderStyle);
	    style.setTopBorderColor(color);
	    style.setBottomBorderColor(color);
	    style.setLeftBorderColor(color);
	    style.setRightBorderColor(color);
	}
}
