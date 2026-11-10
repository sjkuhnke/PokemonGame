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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import pokemon.Pokemon;

public class DocUtils {
	
	private static BufferedImage[] sprites = new BufferedImage[Pokemon.MAX_POKEMON + 1]; // 0 is egg
	private static BufferedImage[] shinySprites = new BufferedImage[Pokemon.MAX_POKEMON + 1];

	// Places an already-embedded picture (index from wb.addPicture or ImageCache).
	// Use this with ImageCache so each distinct image is stored in the file once.
	public static void insertPicture(Sheet sheet, int pictureIdx, int col, int row, int colSpan, int rowSpan, double scaleX, double scaleY) {
		CreationHelper helper = sheet.getWorkbook().getCreationHelper();
		Drawing<?> drawing = sheet.createDrawingPatriarch();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(col);
		anchor.setRow1(row);
		anchor.setCol2(col + colSpan);
		anchor.setRow2(row + rowSpan);
		
		Picture pict = drawing.createPicture(anchor, pictureIdx);
		pict.resize(scaleX, scaleY); // Scales it relative to the anchor box size
	}
	
	// Picks black or white text based on background luminance so labels stay
	// readable. Shared replacement for the copies in PokemonDoc, MovesDoc,
	// TypeChart, and TypingsDoc.
	public static Color textColorFor(Color bg) {
		double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
		return luminance > 150 ? new Color(20, 20, 20) : Color.WHITE;
	}
	
	public static BufferedImage getCachedSprite(int id, boolean shiny) {
		BufferedImage[] sprite = shiny ? shinySprites : sprites;
		String folder = shiny ? "/shiny/" : "/sprites/";
		if (sprite[id] == null) {
			sprite[id] = Pokemon.getSprite(folder, id);
		}
		return sprite[id];
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

	// Three-row location banner (outline row, merged colored label row, outline
	// row) spanning columns 0..lastCol, used by the Trainer/Encounter/Items sheets.
	// Styles come from ctx.styles so repeated banners don't create new CellStyles.
	// Returns the next free row index after the banner.
	public static int writeLocationHeader(DocContext ctx, Sheet sheet, int rowIndex, String location, int lastCol) {
		Color banner = new Color(80, 80, 80);
		CellStyle headerStyle = ctx.style(StyleSpec.plain().bold().size(16).textColor(Color.WHITE).fill(banner)
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER));
		CellStyle outline = ctx.style(StyleSpec.plain().fill(banner));

		Row outlineTop = sheet.createRow(rowIndex++);
		Row headerRow = sheet.createRow(rowIndex++);
		Row outlineBottom = sheet.createRow(rowIndex++);

		for (int c = 0; c <= lastCol; c++) {
			outlineTop.createCell(c).setCellStyle(outline);
			outlineBottom.createCell(c).setCellStyle(outline);
		}
		sheet.addMergedRegion(new CellRangeAddress(outlineTop.getRowNum(), outlineTop.getRowNum(), 0, lastCol));
		sheet.addMergedRegion(new CellRangeAddress(outlineBottom.getRowNum(), outlineBottom.getRowNum(), 0, lastCol));

		Cell headerCell = headerRow.createCell(1);
		headerCell.setCellValue(location);
		headerCell.setCellStyle(headerStyle);
		headerRow.createCell(0).setCellStyle(outline);
		headerRow.createCell(lastCol).setCellStyle(outline);
		sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 1, lastCol - 1));

		return rowIndex;
	}
}