package docs;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import pokemon.PType;
import pokemon.Pokemon;

public class TypingsDoc {

	private static final int LIST_COL_CHARS = 45;

	// =====================================================================
	// Defensive
	// =====================================================================
	public static void writeDefensiveTypingsToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Defensive Typings");
	    setupTableColumns(sheet);
	    writeTableHeader(wb, sheet, "Immune to", "Weak to (!! = 4x)", "Resists (!! = 1/4x)");

	    PType[] types = getUsableTypes();
	    Pokemon test = new Pokemon(1, 5, true, false);
	    Pokemon foe = new Pokemon(4, 5, true, false);

	    int rowIndex = 1;
	    for (PType type1 : types) {
	        for (PType type2 : types) {
	            foe.type1 = type1;
	            foe.type2 = type2;

	            Map<PType, Double> effectiveness = computeDefensiveMap(test, foe, types);

	            String immune = joinTypes(effectiveness, v -> v == 0.0, type1, type2, false);
	            String weak = joinTypes(effectiveness, v -> v >= 2.0, type1, type2, true);
	            String resist = joinTypes(effectiveness, v -> v < 1.0 && v != 0.0, type1, type2, true);

	            rowIndex = writeTableRow(wb, sheet, rowIndex, type1, type2, immune, weak, resist);
	        }
	    }

	    TypeChart.writeTypeChart(wb, "Type Chart");
	    save(wb, dir, "DefensiveTypings.xlsx");
	}

	private static Map<PType, Double> computeDefensiveMap(Pokemon test, Pokemon foe, PType[] types) {
	    Map<PType, Double> map = new HashMap<>();
	    for (PType type3 : types) {
	        double multiplier = 1;
	        if (test.getImmune(foe, type3)) {
	            multiplier = 0;
	        } else {
	            for (PType t : test.getResistances(type3)) {
	                if (foe.type1 == t) multiplier /= 2;
	                if (foe.type2 == t) multiplier /= 2;
	            }
	            for (PType t : test.getWeaknesses(type3)) {
	                if (foe.type1 == t) multiplier *= 2;
	                if (foe.type2 == t) multiplier *= 2;
	            }
	        }
	        map.put(type3, multiplier);
	    }
	    return map;
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeDefensiveTypes); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeDefensiveTypingsToTxt(Path dir) {
	    try {
	        Path outPath = dir.resolve("DefensiveTypings.txt");
	        FileWriter writer = new FileWriter(outPath.toFile());

	        PType[] types = getUsableTypes();

	        Pokemon test = new Pokemon(1, 5, true, false);
	        Pokemon foe = new Pokemon(4, 5, true, false);

	        writer.write("TYPE COMBINATIONS (Defensively)\n");
	        for (PType type1 : types) {
	            for (PType type2 : types) {
	                writer.write("\n===========================\n");
	                String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
	                writer.write(combination + "\n===========================\n");

	                foe.type1 = type1;
	                foe.type2 = type2;
	                Map<PType, Double> typeEffectivenessMap = computeDefensiveMap(test, foe, types);

	                writer.write("Immune to:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() == 0.0) {
	                        writer.write(entry.getKey().toString() + "\n");
	                    }
	                }

	                writer.write("\n----------\nWeak to:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() >= 2.0) {
	                        writer.write(entry.getKey().toString());
	                        if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
	                        writer.write("\n");
	                    }
	                }

	                writer.write("\n----------\nResists:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() < 1.0 && entry.getValue() != 0) {
	                        writer.write(entry.getKey().toString());
	                        if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
	                        writer.write("\n");
	                    }
	                }
	            }
	        }

	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	// =====================================================================
	// Offensive
	// =====================================================================
	public static void writeOffensiveTypingsToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Offensive Typings");
	    setupTableColumns(sheet);
	    writeTableHeader(wb, sheet, "Deals 2x to (!! = 4x)", "Deals 1/2x to (!! = 1/4x)", "Deals 0x to");

	    PType[] types = getUsableTypes();
	    Pokemon test = new Pokemon(1, 5, true, false);
	    Pokemon foe = new Pokemon(4, 5, true, false);

	    int rowIndex = 1;
	    for (PType type1 : types) {
	        for (PType type2 : types) {
	            Map<PType, Double> effectiveness = computeOffensiveMap(test, foe, type1, type2, types);

	            String deals2x = joinTypes(effectiveness, v -> v >= 2.0, type1, type2, true);
	            String dealsHalf = joinTypes(effectiveness, v -> v < 1.0 && v != 0.0, type1, type2, true);
	            String deals0x = joinTypes(effectiveness, v -> v == 0.0, type1, type2, false);

	            rowIndex = writeTableRow(wb, sheet, rowIndex, type1, type2, deals2x, dealsHalf, deals0x);
	        }
	    }

	    TypeChart.writeTypeChart(wb, "Type Chart");
	    save(wb, dir, "OffensiveTypings.xlsx");
	}

	private static Map<PType, Double> computeOffensiveMap(Pokemon test, Pokemon foe, PType type1, PType type2, PType[] types) {
	    Map<PType, Double> map = new HashMap<>();
	    for (PType type3 : types) {
	        foe.type1 = type3;
	        foe.type2 = null;
	        double multiplier = 1;
	        if (test.getImmune(foe, type1) && test.getImmune(foe, type2)) {
	            multiplier = 0;
	        } else {
	            ArrayList<PType> resist1 = new ArrayList<>(Arrays.asList(test.getResistances(type1)));
	            ArrayList<PType> resist2 = new ArrayList<>(Arrays.asList(test.getResistances(type2)));
	            if ((resist1.contains(type3) && resist2.contains(type3))
	                    || (test.getImmune(foe, type1) && resist2.contains(type3))
	                    || (resist1.contains(type3) && test.getImmune(foe, type2))) {
	                multiplier /= 2;
	            }

	            ArrayList<PType> weak1 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type1)));
	            ArrayList<PType> weak2 = new ArrayList<>(Arrays.asList(test.getWeaknesses(type2)));
	            if (weak1.contains(type3) && weak2.contains(type3)) {
	                multiplier *= 4;
	            } else if (weak1.contains(type3) || weak2.contains(type3)) {
	                multiplier *= 2;
	            }
	        }
	        map.put(type3, multiplier);
	    }
	    return map;
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeOffensiveTypes); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeOffensiveTypingsToTxt(Path dir) {
	    try {
	        Path outPath = dir.resolve("OffensiveTypings.txt");
	        FileWriter writer = new FileWriter(outPath.toFile());

	        PType[] types = getUsableTypes();

	        Pokemon test = new Pokemon(1, 5, true, false);
	        Pokemon foe = new Pokemon(4, 5, true, false);

	        writer.write("TYPE COMBINATIONS (Offensively)\n");
	        for (PType type1 : types) {
	            for (PType type2 : types) {
	                writer.write("\n===========================\n");
	                String combination = (type1 == type2) ? type1 + " - None" : type1 + " - " + type2;
	                writer.write(combination + "\n===========================\n");

	                Map<PType, Double> typeEffectivenessMap = computeOffensiveMap(test, foe, type1, type2, types);

	                writer.write("Deals 2x to:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() >= 2.0) {
	                        writer.write(entry.getKey().toString());
	                        if (entry.getValue() == 4.0 && type1 != type2) writer.write(" (!!)");
	                        writer.write("\n");
	                    }
	                }

	                writer.write("\n----------\nDeals 1/2x to:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() < 1.0 && entry.getValue() != 0) {
	                        writer.write(entry.getKey().toString());
	                        if (entry.getValue() == 0.25 && type1 != type2) writer.write(" (!!)");
	                        writer.write("\n");
	                    }
	                }

	                writer.write("\n----------\nDeals 0x to:\n\n");
	                for (Map.Entry<PType, Double> entry : typeEffectivenessMap.entrySet()) {
	                    if (entry.getValue() == 0.0) {
	                        writer.write(entry.getKey().toString() + "\n");
	                    }
	                }
	            }
	        }

	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	// =====================================================================
	// Shared helpers
	// =====================================================================
	private static PType[] getUsableTypes() {
	    PType[] types = PType.values();
	    ArrayList<PType> list = new ArrayList<>(Arrays.asList(types));
	    list.remove(PType.UNKNOWN);
	    return list.toArray(new PType[0]);
	}

	private interface Predicate1 {
	    boolean test(double v);
	}

	// Builds the comma-joined cell text for one bucket (immune/weak/resist,
	// or deals-2x/deals-half/deals-0x), appending "(!!)" per-entry the same
	// way the original txt writers flagged quad weaknesses/quarter resists.
	private static String joinTypes(Map<PType, Double> map, Predicate1 include, PType type1, PType type2, boolean flagExtreme) {
	    StringBuilder sb = new StringBuilder();
	    for (Map.Entry<PType, Double> entry : map.entrySet()) {
	        if (!include.test(entry.getValue())) continue;
	        if (sb.length() > 0) sb.append(", ");
	        sb.append(entry.getKey().toString());
	        if (flagExtreme && type1 != type2 && (entry.getValue() == 4.0 || entry.getValue() == 0.25)) {
	            sb.append(" (!!)");
	        }
	    }
	    return sb.length() == 0 ? "\u2014" : sb.toString();
	}

	private static void setupTableColumns(Sheet sheet) {
	    sheet.setColumnWidth(0, 5200);
	    sheet.setColumnWidth(1, 8500);
	    sheet.setColumnWidth(2, 8500);
	    sheet.setColumnWidth(3, 8500);
	    sheet.createFreezePane(0, 1);
	}

	private static void writeTableHeader(Workbook wb, Sheet sheet, String col1, String col2, String col3) {
	    Row header = sheet.createRow(0);
	    header.setHeightInPoints(20);
	    String[] labels = {"Typing", col1, col2, col3};
	    for (int c = 0; c < labels.length; c++) {
	        Cell cell = header.createCell(c);
	        cell.setCellValue(labels[c]);
	        cell.setCellStyle(headerStyle(wb));
	    }
	}

	private static int writeTableRow(Workbook wb, Sheet sheet, int rowIndex, PType type1, PType type2,
	                                  String col1Text, String col2Text, String col3Text) {
	    Row row = sheet.createRow(rowIndex);

	    Cell typingCell = row.createCell(0);
	    String label = (type1 == type2) ? type1.toString().toUpperCase() + " - NONE"
	            : type1.toString().toUpperCase() + " - " + type2.toString().toUpperCase();
	    typingCell.setCellValue(label);
	    typingCell.setCellStyle(typingStyle(wb, type1.getColor()));

	    Cell c1 = row.createCell(1);
	    c1.setCellValue(col1Text);
	    c1.setCellStyle(listStyle(wb, new Color(225, 225, 225)));

	    Cell c2 = row.createCell(2);
	    c2.setCellValue(col2Text);
	    c2.setCellStyle(listStyle(wb, new Color(240, 205, 205)));

	    Cell c3 = row.createCell(3);
	    c3.setCellValue(col3Text);
	    c3.setCellStyle(listStyle(wb, new Color(205, 230, 205)));

	    String longest = col1Text.length() > col2Text.length()
	            ? (col1Text.length() > col3Text.length() ? col1Text : col3Text)
	            : (col2Text.length() > col3Text.length() ? col2Text : col3Text);
	    DocUtils.setWrappedRowHeight(row, longest, LIST_COL_CHARS, 13f);

	    return rowIndex + 1;
	}

	// ----------------------------------------------------------------------
	// Styles
	// ----------------------------------------------------------------------
	private static CellStyle headerStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 11);
	    font.setColor(new XSSFColor(Color.WHITE, null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle typingStyle(Workbook wb, Color bg) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);
	    style.setWrapText(true);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 10);
	    font.setColor(new XSSFColor(textColorFor(bg), null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle listStyle(Workbook wb, Color bg) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.TOP);
	    style.setWrapText(true);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}

	private static Color textColorFor(Color bg) {
	    double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
	    return luminance > 150 ? new Color(20, 20, 20) : Color.WHITE;
	}

	private static void save(Workbook wb, Path dir, String filename) {
	    Path outPath = dir.resolve(filename);
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
}