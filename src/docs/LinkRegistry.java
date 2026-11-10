package docs;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Deferred cross-sheet hyperlinks.
 *
 * While writing a sheet you don't need to know where the target lives yet:
 *   - the sheet that OWNS an entity calls   register(Kind.MOVE, move, sheet, row, col)
 *   - any sheet that MENTIONS it calls      link(cell, Kind.MOVE, move)
 * After every section is written, resolveAll() turns each queued link into a
 * real internal hyperlink (and restyles the cell as a link). Write order
 * between sections therefore never matters.
 *
 * Key conventions (keys are compared with equals/hashCode):
 *   POKEMON -> Integer id (the "original" id, so alt forms 237/291 get their own target)
 *   MOVE    -> pokemon.Move enum constant
 *   ABILITY -> pokemon.Ability enum constant
 *   ITEM    -> pokemon.Item enum constant
 *   TYPE    -> pokemon.PType enum constant
 *
 * A cell holds only one hyperlink; if link() is called twice for the same
 * cell the last one resolved wins.
 *
 * Rows/cols are 0-based, matching POI.
 */
public class LinkRegistry {
	public enum Kind { POKEMON, MOVE, ABILITY, ITEM, TYPE }

	private static final class Target {
		final String sheet;
		final int row, col;
		Target(String sheet, int row, int col) {
			this.sheet = sheet;
			this.row = row;
			this.col = col;
		}
		String address() {
			return "'" + sheet.replace("'", "''") + "'!" + new CellReference(row, col).formatAsString();
		}
	}

	private static final class Pending {
		final Cell cell;
		final Kind kind;
		final Object key;
		Pending(Cell cell, Kind kind, Object key) {
			this.cell = cell;
			this.kind = kind;
			this.key = key;
		}
	}

	private final StyleCache styles;
	private final Map<Kind, Map<Object, Target>> targets = new EnumMap<>(Kind.class);
	private final List<Pending> pending = new ArrayList<>();

	public LinkRegistry(StyleCache styles) {
		this.styles = styles;
		for (Kind k : Kind.values()) targets.put(k, new HashMap<>());
	}

	/** Declares where an entity's "home" entry lives. First registration wins. */
	public void register(Kind kind, Object key, Sheet sheet, int row, int col) {
		targets.get(kind).putIfAbsent(key, new Target(sheet.getSheetName(), row, col));
	}

	public boolean isRegistered(Kind kind, Object key) {
		return targets.get(kind).containsKey(key);
	}

	/** Queues a link from `cell` to the entity's home entry. Resolved later. */
	public void link(Cell cell, Kind kind, Object key) {
		if (cell == null || key == null) return;
		pending.add(new Pending(cell, kind, key));
	}

	/** Immediate link to a known sheet (e.g. Index sheet -> each tab). */
	public void linkToSheet(Cell cell, String sheetName, int row, int col) {
		applyLink(cell, new Target(sheetName, row, col));
	}

	public int pendingCount() {
		return pending.size();
	}

	/**
	 * Resolves every queued link. Call once, after all sections are written.
	 * Unresolvable links are left as plain text (no link styling).
	 *
	 * @return one line per unresolved link ("MOVE:Foo from 'Trainer Teams'!B12"),
	 *         so MasterDoc can log entities that are mentioned but never defined.
	 */
	public List<String> resolveAll(XSSFWorkbook wb) {
		List<String> unresolved = new ArrayList<>();
		for (Pending p : pending) {
			Target t = targets.get(p.kind).get(p.key);
			if (t == null) {
				unresolved.add(p.kind + ":" + p.key + " from '" + p.cell.getSheet().getSheetName() + "'!"
						+ new CellReference(p.cell.getRowIndex(), p.cell.getColumnIndex()).formatAsString());
				continue;
			}
			applyLink(p.cell, t);
		}
		pending.clear();
		return unresolved;
	}

	private void applyLink(Cell cell, Target t) {
		CreationHelper helper = cell.getSheet().getWorkbook().getCreationHelper();
		Hyperlink hl = helper.createHyperlink(HyperlinkType.DOCUMENT);
		hl.setAddress(t.address());
		cell.setHyperlink(hl);
		cell.setCellStyle(styles.linkVariant(cell.getCellStyle()));
	}
}