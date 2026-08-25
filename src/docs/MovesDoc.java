package docs;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pokemon.Move;
import pokemon.PType;
import pokemon.Pokemon;

public class MovesDoc {

	// name | category | bp | acc | pp | description
	private static final int COL_NAME = 0, COL_CAT = 1, COL_BP = 2, COL_ACC = 3, COL_PP = 4, COL_DESC = 5;
	private static final int LAST_COL = COL_DESC;
	private static final int DESC_COL_CHARS = 85; // tuned for the description column width below - moves like Stealth Rock run long

	public static void writeMovesToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Moves");

	    sheet.setColumnWidth(COL_NAME, 5200);
	    sheet.setColumnWidth(COL_CAT, 2800);
	    sheet.setColumnWidth(COL_BP, 1800);
	    sheet.setColumnWidth(COL_ACC, 1800);
	    sheet.setColumnWidth(COL_PP, 1800);
	    sheet.setColumnWidth(COL_DESC, 20000);
	    sheet.createFreezePane(0, 1);

	    Map<PType, List<Move>> movesByType = groupMovesByType();
	    List<PType> sortedTypes = new ArrayList<>(movesByType.keySet());
	    sortedTypes.sort(Comparator.comparing(PType::toString));

	    int rowIndex = 0;
	    for (PType type : sortedTypes) {
	        rowIndex = writeTypeHeader(sheet, type, rowIndex);
	        rowIndex = writeColumnLabels(wb, sheet, rowIndex);

	        List<Move> typeMoves = movesByType.get(type);
	        for (Move m : typeMoves) {
	            rowIndex = writeMoveRow(wb, sheet, m, rowIndex);
	        }
	        rowIndex++; // spacer row between type blocks
	    }

	    writeHiddenPowerSheet(wb);

	    Path outPath = dir.resolve("MovesInfo.xlsx");
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}

	private static Map<PType, List<Move>> groupMovesByType() {
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

	private static int writeTypeHeader(Sheet sheet, PType type, int rowIndex) {
	    Workbook wb = sheet.getWorkbook();
	    Row row = sheet.createRow(rowIndex);
	    row.setHeightInPoints(26);

	    Cell nameCell = row.createCell(COL_NAME + 1); // leave col 0 free for the icon
	    nameCell.setCellValue(type.toString().toUpperCase());
	    nameCell.setCellStyle(typeHeaderStyle(wb, type.getColor()));
	    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), COL_NAME + 1, LAST_COL));

	    Cell iconCell = row.createCell(COL_NAME);
	    iconCell.setCellStyle(typeHeaderStyle(wb, type.getColor()));
	    try {
	        byte[] iconBytes = DocUtils.imageToBytes(type.getImage2(), "png");
	        if (iconBytes != null) {
	            DocUtils.insertImage(sheet, iconBytes, COL_NAME, row.getRowNum(), 1, 1, 0.6, 0.6);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return rowIndex + 1;
	}

	private static int writeColumnLabels(Workbook wb, Sheet sheet, int rowIndex) {
	    Row row = sheet.createRow(rowIndex);
	    String[] labels = new String[6];
	    labels[COL_NAME] = "Move";
	    labels[COL_CAT] = "Category";
	    labels[COL_BP] = "BP";
	    labels[COL_ACC] = "Acc";
	    labels[COL_PP] = "PP";
	    labels[COL_DESC] = "Description";
	    for (int c = 0; c <= LAST_COL; c++) {
	        Cell cell = row.createCell(c);
	        cell.setCellValue(labels[c]);
	        cell.setCellStyle(columnLabelStyle(wb));
	    }
	    return rowIndex + 1;
	}

	private static int writeMoveRow(Workbook wb, Sheet sheet, Move m, int rowIndex) {
	    Row row = sheet.createRow(rowIndex);

	    Cell nameCell = row.createCell(COL_NAME);
	    nameCell.setCellValue(m.toString());
	    nameCell.setCellStyle(plainStyle(wb, true, HorizontalAlignment.LEFT));

	    Cell catCell = row.createCell(COL_CAT);
	    catCell.setCellValue(m.getCategory().toString());
	    catCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	    Cell bpCell = row.createCell(COL_BP);
	    bpCell.setCellValue(m.formatbp(null, null, Pokemon.field));
	    bpCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	    Cell accCell = row.createCell(COL_ACC);
	    accCell.setCellValue(m.getAccuracy(null, null, Pokemon.field));
	    accCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	    Cell ppCell = row.createCell(COL_PP);
	    ppCell.setCellValue(m.pp);
	    ppCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	    String desc = m.getDescription();
	    Cell descCell = row.createCell(COL_DESC);
	    descCell.setCellValue(desc);
	    descCell.setCellStyle(descStyle(wb));

	    DocUtils.setWrappedRowHeight(row, desc, DESC_COL_CHARS, 13f);

	    return rowIndex + 1;
	}

	// ----------------------------------------------------------------------
	// Second tab: IVs -> Hidden Power type
	// ----------------------------------------------------------------------
	private static void writeHiddenPowerSheet(Workbook wb) {
	    Sheet sheet = wb.createSheet("Hidden Power");
	    sheet.setColumnWidth(0, 7000);
	    sheet.setColumnWidth(1, 4000);
	    sheet.createFreezePane(0, 1);

	    Row header = sheet.createRow(0);
	    header.setHeightInPoints(18);
	    Cell ivHeader = header.createCell(0);
	    ivHeader.setCellValue("IVs (HP/Atk/Def/SpA/SpD/Spe)");
	    ivHeader.setCellStyle(columnLabelStyle(wb));
	    Cell typeHeader = header.createCell(1);
	    typeHeader.setCellValue("Hidden Power Type");
	    typeHeader.setCellStyle(columnLabelStyle(wb));

	    Pokemon test = new Pokemon(1, 5, true, false);
	    for (int i = 0; i < 64; i++) {
	        int[] ivs = new int[6];
	        for (int j = 0; j < 6; j++) {
	            ivs[j] = (i & (1 << j)) != 0 ? 31 : 30;
	        }
	        test.ivs = ivs;
	        PType hpType = test.determineHPType();

	        Row row = sheet.createRow(i + 1);
	        Cell ivCell = row.createCell(0);
	        ivCell.setCellValue(Arrays.toString(ivs));
	        ivCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.LEFT));

	        Cell typeCell = row.createCell(1);
	        typeCell.setCellValue(hpType.toString().toUpperCase());
	        typeCell.setCellStyle(typeHeaderStyle(wb, hpType.getColor()));
	    }
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
	            writer.write("                                                  " + type.toString() + "\n");
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
	                writer.write(String.format("%s|%s|%s|%s|%s: %s\n", move, cat, bp, acc, pp, m.getDescription()));
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

	// ----------------------------------------------------------------------
	// Styles
	// ----------------------------------------------------------------------
	private static CellStyle typeHeaderStyle(Workbook wb, Color bg) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 13);
	    font.setColor(new XSSFColor(textColorFor(bg), null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle columnLabelStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(230, 230, 230), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle plainStyle(Workbook wb, boolean bold, HorizontalAlignment align) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(align);
	    style.setVerticalAlignment(VerticalAlignment.TOP);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(bold);
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle descStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
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
}