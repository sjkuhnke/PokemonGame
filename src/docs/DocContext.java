package docs;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Everything a section needs to write into the shared master workbook.
 * One instance is created per generation and passed to every section.
 */
public class DocContext {
	public final XSSFWorkbook wb;
	public final StyleCache styles;
	public final ImageCache images;
	public final LinkRegistry links;
	public final Progress progress;

	public DocContext(ProgressListener listener, int startPercent, int endPercent, int totalWeight) {
		this.wb = new XSSFWorkbook();
		this.styles = new StyleCache(wb);
		this.images = new ImageCache(wb);
		this.links = new LinkRegistry(styles);
		this.progress = new Progress(listener, startPercent, endPercent, totalWeight);
	}

	/** Creates a sheet, sanitizing the name for Excel (31 chars, no []:*?/\). */
	public Sheet newSheet(String name) {
		return wb.createSheet(WorkbookUtil.createSafeSheetName(name));
	}

	// ----------------------------------------------------------------------
	// Convenience wrappers so section code stays short
	// ----------------------------------------------------------------------

	/** Creates the cell, sets its text, and applies a cached style. */
	public Cell text(Row row, int col, String value, StyleSpec style) {
		Cell cell = row.createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(styles.get(style));
		return cell;
	}

	public Cell number(Row row, int col, double value, StyleSpec style) {
		Cell cell = row.createCell(col);
		cell.setCellValue(value);
		cell.setCellStyle(styles.get(style));
		return cell;
	}

	public CellStyle style(StyleSpec spec) {
		return styles.get(spec);
	}

	/**
	 * Inserts an image using the shared cache: the picture is embedded once
	 * per key no matter how many times it's placed. No-op if the image is null.
	 */
	public void image(Sheet sheet, String key, Supplier<BufferedImage> source,
			int col, int row, int colSpan, int rowSpan, double scaleX, double scaleY) {
		int idx = images.getOrAdd(key, source);
		if (idx >= 0) {
			DocUtils.insertPicture(sheet, idx, col, row, colSpan, rowSpan, scaleX, scaleY);
		}
	}
}