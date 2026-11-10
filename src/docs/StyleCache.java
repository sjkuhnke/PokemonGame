package docs;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * One CellStyle (and one Font) per distinct StyleSpec, per workbook.
 *
 * Replaces the pattern of calling wb.createCellStyle()/createFont() inside
 * per-cell helpers (PokemonDoc.fillStyle, TrainerDoc's move cells, ...),
 * which in a single master workbook would blow past the ~64k style limit.
 *
 * Not thread-safe; a workbook is written by one thread.
 */
public class StyleCache {
	private final XSSFWorkbook wb;
	private final Map<StyleSpec, XSSFCellStyle> styles = new HashMap<>();
	private final Map<String, XSSFFont> fonts = new HashMap<>();
	// style index -> the spec it was built from, so a cell's existing style
	// can be turned into its "link" variant without losing fill/alignment
	private final Map<Integer, StyleSpec> specByIndex = new HashMap<>();

	private static final Color LINK_BLUE = new Color(5, 99, 193);

	public StyleCache(XSSFWorkbook wb) {
		this.wb = wb;
	}

	public CellStyle get(StyleSpec spec) {
		XSSFCellStyle style = styles.get(spec);
		if (style != null) return style;

		style = wb.createCellStyle();
		style.setFont(fontFor(spec));
		style.setAlignment(spec.h);
		style.setVerticalAlignment(spec.v);
		if (spec.wrap) style.setWrapText(true);
		if (spec.rotation != 0) style.setRotation(spec.rotation);
		if (spec.format != null) {
			style.setDataFormat(wb.createDataFormat().getFormat(spec.format));
		}
		if (spec.hasFill()) {
			style.setFillForegroundColor(new XSSFColor(new Color(spec.fillRgb), null));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}

		styles.put(spec, style);
		specByIndex.put((int) style.getIndex(), spec);
		return style;
	}

	/**
	 * The hyperlink look for a given existing style: same spec, underlined,
	 * and blue text when the cell has no fill and no explicit text color
	 * (cells with a colored background, like move/type badges, keep their
	 * colors and just gain an underline so they stay readable).
	 *
	 * If `base` didn't come from this cache (e.g. someone called
	 * wb.createCellStyle() directly) it is returned unchanged.
	 */
	public CellStyle linkVariant(CellStyle base) {
		if (base == null) return get(StyleSpec.plain().underline(true).textColor(LINK_BLUE));
		StyleSpec spec = specByIndex.get((int) base.getIndex());
		if (spec == null) return base;
		StyleSpec link = spec.underline(true);
		if (!spec.hasFill() && !spec.hasTextColor()) link = link.textColor(LINK_BLUE);
		return get(link);
	}

	private XSSFFont fontFor(StyleSpec spec) {
		String key = spec.fontKey();
		XSSFFont font = fonts.get(key);
		if (font != null) return font;

		font = wb.createFont();
		font.setBold(spec.bold);
		font.setItalic(spec.italic);
		if (spec.underline) font.setUnderline(XSSFFont.U_SINGLE);
		font.setFontHeightInPoints((short) spec.size);
		if (spec.hasTextColor()) {
			font.setColor(new XSSFColor(new Color(spec.textRgb), null));
		}
		fonts.put(key, font);
		return font;
	}

	/** Diagnostics: how many distinct styles/fonts this workbook is using. */
	public String stats() {
		return styles.size() + " styles, " + fonts.size() + " fonts";
	}
}