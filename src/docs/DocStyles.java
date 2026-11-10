package docs;

import java.awt.Color;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

final class DocStyles {
	static final Color HEADER_BG = new Color(80, 80, 80);
	static final Color COLUMN_LABEL_BG = new Color(230, 230, 230);

	private DocStyles() {}

	/** White bold text on the dark gray table-header fill. */
	static StyleSpec header(int size, HorizontalAlignment align) {
		return StyleSpec.plain().bold().size(size).textColor(Color.WHITE).fill(HEADER_BG)
				.align(align).valign(VerticalAlignment.CENTER);
	}

	/** Light gray bold column-label row (Moves, Hidden Power). */
	static StyleSpec columnLabel(int size) {
		return StyleSpec.plain().bold().size(size).fill(COLUMN_LABEL_BG)
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER);
	}

	static StyleSpec text(boolean bold, int size, HorizontalAlignment align, VerticalAlignment valign) {
		return StyleSpec.plain().bold(bold).size(size).align(align).valign(valign);
	}

	/** Left-aligned, top-aligned wrapped text. */
	static StyleSpec wrapped(int size) {
		return StyleSpec.plain().size(size).wrap()
				.align(HorizontalAlignment.LEFT).valign(VerticalAlignment.TOP);
	}

	/** Bold text on a colored badge (types, move chips) with auto black/white text. */
	static StyleSpec badge(Color bg, int size, HorizontalAlignment align) {
		return StyleSpec.plain().bold().size(size).fillWithContrastText(bg)
				.align(align).valign(VerticalAlignment.CENTER);
	}
}
