package docs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import pokemon.Pokemon;

public class DocUtils {
	
	private static BufferedImage[] sprites = new BufferedImage[Pokemon.MAX_POKEMON];
	
	public static CellStyle makeStyle(Workbook wb, boolean bold, boolean italic, int fontSize, short color) {
		XSSFFont font = makeFont(wb, bold, italic, fontSize, color);

	    CellStyle style = wb.createCellStyle();
	    style.setFont(font);
	    return style;
	}
	
	public static XSSFFont makeFont(Workbook wb, boolean bold, boolean italic, int fontSize, short color) {
		XSSFFont font = (XSSFFont) wb.createFont();
        font.setFontHeightInPoints((short) fontSize);
	    font.setBold(bold);
	    font.setItalic(italic);
	    font.setColor(color);
	    
	    return font;
	}
	
	public static void insertImage(Sheet sheet, byte[] imageBytes, int col, int row, int colSpan, int rowSpan, double scaleX, double scaleY) {
		Workbook wb = sheet.getWorkbook();
	    int pictureIdx = wb.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
	    CreationHelper helper = wb.getCreationHelper();
	    Drawing<?> drawing = sheet.createDrawingPatriarch();
	    ClientAnchor anchor = helper.createClientAnchor();
	    anchor.setCol1(col);
	    anchor.setRow1(row);
	    anchor.setCol2(col + colSpan);
	    anchor.setRow2(row + rowSpan);
	    
	    Picture pict = drawing.createPicture(anchor, pictureIdx);
	    pict.resize(scaleX, scaleY); // Scales it relative to the anchor box size
	}
	
	public static BufferedImage getCachedSprite(Pokemon p) {
	    if (sprites[p.id - 1] == null) {
	        sprites[p.id - 1] = p.setSprite();
	    }
	    return sprites[p.id - 1];
	}
	
	public static byte[] imageToBytes(BufferedImage image, String formatName) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, formatName, baos); // formatName can be "png", "jpg", etc.
	    baos.flush();
	    byte[] imageBytes = baos.toByteArray();
	    baos.close();
	    return imageBytes;
	}
	
	public static BufferedImage combineIcons(BufferedImage type1, BufferedImage type2) {
	    int width = 96;  // 2 x 24
	    int height = 48;
	    BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = combined.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(type1, 0, 0, null);
	    if (type2 != null) {
	        g.drawImage(type2, 48, 0, null);
	    }
	    g.dispose();
	    return combined;
	}

	// ------------------------------------------------------------------
	// Wrapped-text row sizing
	// ------------------------------------------------------------------

	// Rough estimate of how many lines `text` will wrap to inside a column
	// that's `charsPerLine` characters wide. Good enough for row-height
	// purposes without needing to measure actual font metrics.
	public static int estimateWrappedLineCount(String text, int charsPerLine) {
	    if (text == null || text.isEmpty()) return 1;
	    String[] words = text.split(" ");
	    int lines = 1;
	    int lineLen = 0;
	    for (String w : words) {
	        int wl = w.length();
	        if (lineLen > 0 && lineLen + 1 + wl > charsPerLine) {
	            lines++;
	            lineLen = wl;
	        } else {
	            lineLen += (lineLen > 0 ? 1 : 0) + wl;
	        }
	    }
	    return lines;
	}

	// Grows (never shrinks) a row's height so wrapped `text` fits in a column
	// that's `charsPerLine` characters wide, assuming ~`lineHeightPoints` per line.
	public static void setWrappedRowHeight(Row row, String text, int charsPerLine, float lineHeightPoints) {
	    int lines = estimateWrappedLineCount(text, charsPerLine);
	    float needed = lines * lineHeightPoints + 4f;
	    if (row.getHeightInPoints() < needed) {
	        row.setHeightInPoints(needed);
	    }
	}

	public static CellStyle wrapStyle(Workbook wb, int fontSize, HorizontalAlignment align) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setWrapText(true);
	    style.setAlignment(align);
	    style.setVerticalAlignment(VerticalAlignment.TOP);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) fontSize);
	    style.setFont(font);

	    return style;
	}

	// ------------------------------------------------------------------
	// Shared "location header" banner - the same three-row outline/label
	// pattern TrainerDoc uses (an outline row, a merged colored label row,
	// another outline row), generalized to any column span so ItemsDoc and
	// EncounterDoc's Excel sheets can look consistent with TrainerInfo.xlsx.
	// Returns the next free row index after the banner.
	// ------------------------------------------------------------------
	public static int writeLocationHeader(Sheet sheet, int rowIndex, String location, int lastCol) {
	    Workbook wb = sheet.getWorkbook();

	    XSSFCellStyle headerStyle = (XSSFCellStyle) makeStyle(wb, true, false, 16, org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
	    headerStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    headerStyle.setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    CellStyle outline = wb.createCellStyle();
	    ((XSSFCellStyle) outline).setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    outline.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    Row outlineTop = sheet.createRow(rowIndex++);
	    Row headerRow = sheet.createRow(rowIndex++);
	    Row outlineBottom = sheet.createRow(rowIndex++);

	    for (int c = 0; c <= lastCol; c++) {
	        Cell top = outlineTop.createCell(c);
	        top.setCellStyle(outline);
	        Cell bottom = outlineBottom.createCell(c);
	        bottom.setCellStyle(outline);
	    }
	    sheet.addMergedRegion(new CellRangeAddress(outlineTop.getRowNum(), outlineTop.getRowNum(), 0, lastCol));
	    sheet.addMergedRegion(new CellRangeAddress(outlineBottom.getRowNum(), outlineBottom.getRowNum(), 0, lastCol));

	    Cell headerCell = headerRow.createCell(1);
	    headerCell.setCellValue(location);
	    headerCell.setCellStyle(headerStyle);
	    Cell edge1 = headerRow.createCell(0);
	    edge1.setCellStyle(outline);
	    Cell edge2 = headerRow.createCell(lastCol);
	    edge2.setCellStyle(outline);
	    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 1, lastCol - 1));

	    return rowIndex;
	}
}