package docs;

import java.awt.Color;
import java.util.Objects;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * Immutable description of a cell style. Two equal specs always map to the
 * same CellStyle (and Font) in StyleCache, which keeps the workbook under
 * Excel's style limit no matter how many cells are written.
 *
 * Build with the fluent "with" methods:
 *   StyleSpec.plain().bold().size(14).fill(new Color(80,80,80)).textColor(Color.WHITE)
 *            .align(HorizontalAlignment.CENTER)
 *
 * Every method returns a new spec, so shared constants are safe.
 */
public final class StyleSpec {
	public static final int NONE = -1;

	final boolean bold, italic, underline, wrap;
	final int size;
	final int textRgb;      // NONE = default font color
	final int fillRgb;      // NONE = no fill
	final HorizontalAlignment h;
	final VerticalAlignment v;
	final short rotation;
	final String format;    // data format string, e.g. "0%" or "$#,##0"; null = General

	private StyleSpec(boolean bold, boolean italic, boolean underline, boolean wrap, int size, int textRgb,
			int fillRgb, HorizontalAlignment h, VerticalAlignment v, short rotation, String format) {
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.wrap = wrap;
		this.size = size;
		this.textRgb = textRgb;
		this.fillRgb = fillRgb;
		this.h = h;
		this.v = v;
		this.rotation = rotation;
		this.format = format;
	}

	/** Default: 11pt, general alignment (matches POI's default cell look). */
	public static StyleSpec plain() {
		return new StyleSpec(false, false, false, false, 11, NONE, NONE,
				HorizontalAlignment.GENERAL, VerticalAlignment.BOTTOM, (short) 0, null);
	}

	public StyleSpec bold() { return bold(true); }
	public StyleSpec bold(boolean b) {
		return new StyleSpec(b, italic, underline, wrap, size, textRgb, fillRgb, h, v, rotation, format);
	}
	public StyleSpec italic() { return italic(true); }
	public StyleSpec italic(boolean i) {
		return new StyleSpec(bold, i, underline, wrap, size, textRgb, fillRgb, h, v, rotation, format);
	}
	public StyleSpec underline(boolean u) {
		return new StyleSpec(bold, italic, u, wrap, size, textRgb, fillRgb, h, v, rotation, format);
	}
	public StyleSpec wrap() {
		return new StyleSpec(bold, italic, underline, true, size, textRgb, fillRgb, h, v, rotation, format);
	}
	public StyleSpec size(int s) {
		return new StyleSpec(bold, italic, underline, wrap, s, textRgb, fillRgb, h, v, rotation, format);
	}
	public StyleSpec textColor(Color c) {
		return new StyleSpec(bold, italic, underline, wrap, size, c.getRGB() & 0xFFFFFF, fillRgb, h, v, rotation, format);
	}
	public StyleSpec fill(Color c) {
		return new StyleSpec(bold, italic, underline, wrap, size, textRgb, c.getRGB() & 0xFFFFFF, h, v, rotation, format);
	}
	public StyleSpec align(HorizontalAlignment a) {
		return new StyleSpec(bold, italic, underline, wrap, size, textRgb, fillRgb, a, v, rotation, format);
	}
	public StyleSpec valign(VerticalAlignment a) {
		return new StyleSpec(bold, italic, underline, wrap, size, textRgb, fillRgb, h, a, rotation, format);
	}
	public StyleSpec rotate(int degrees) {
		return new StyleSpec(bold, italic, underline, wrap, size, textRgb, fillRgb, h, v, (short) degrees, format);
	}
	public StyleSpec format(String f) {
		return new StyleSpec(bold, italic, underline, wrap, size, textRgb, fillRgb, h, v, rotation, f);
	}

	/**
	 * Sets the fill AND picks black/white text for readability (same luminance
	 * rule every Doc class currently duplicates as textColorFor).
	 */
	public StyleSpec fillWithContrastText(Color bg) {
		return fill(bg).textColor(DocUtils.textColorFor(bg));
	}

	boolean hasFill() { return fillRgb != NONE; }
	boolean hasTextColor() { return textRgb != NONE; }

	/** Font-only key, so styles that differ only in fill/alignment share one Font. */
	String fontKey() {
		return bold + "|" + italic + "|" + underline + "|" + size + "|" + textRgb;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StyleSpec)) return false;
		StyleSpec s = (StyleSpec) o;
		return bold == s.bold && italic == s.italic && underline == s.underline && wrap == s.wrap
				&& size == s.size && textRgb == s.textRgb && fillRgb == s.fillRgb && h == s.h && v == s.v
				&& rotation == s.rotation && Objects.equals(format, s.format);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bold, italic, underline, wrap, size, textRgb, fillRgb, h, v, rotation, format);
	}
}