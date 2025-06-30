package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int[] itemList;
	public int[] count;
	public Player p;
	
	public Bag(Player p) {
		this.p = p;
		Item[] items = Item.values();
		itemList = setupItemList(items);
		count = new int[items.length];
	}
	
	private int[] setupItemList(Item[] items) {
		int[] result = new int[items.length];
		int index = 0;
		for (Item i : items) {
			result[index] = i.getID();
			index++;
		}
		return result;
	}

	public void add(Item item) {
		add(item, 1);
	}
	
	public void add(Item item, int amt) {
		count[item.getID()] += amt;
		if (item.isBall()) {
			if (p.nuzlocke) p.nuzlockeStarted = true;
		}
	}
	
	public void remove(Item item) {
		count[item.getID()]--;
	}
	
	public void remove(Item item, int amt) {
		if (count[item.getID()] < amt) throw new IllegalArgumentException("You have less " + item.toString() + " then you're trying to remove (" + amt + ")");
		count[item.getID()] -= amt;
	}
	
	public void removeAll(Item item) {
		count[item.getID()] = 0;
	}
	
	public ArrayList<Entry> getItems() {
		ArrayList<Entry> items = new ArrayList<>();
		for (int i : itemList) {
			if (i >= count.length || count[i] <= 0) continue;
			items.add(new Entry(Item.getItem(i), count[i]));
		}
		return items;
	}
	
	public class Entry {
		Item item;
		int count;
		
		public Entry(Item item, int count) {
			this.item = item;
			this.count = count;
		}

		public Item getItem() {
			return item;
		}
		
		public int getCount() {
			return count;
		}
		
		public String toString() {
			return item.toString() + " x " + count;
		}

		public int getMaxSell() {
			return Math.min(999, count);
		}
	}

	public boolean contains(int id) {
		return count[id] > 0;
	}

	public boolean contains(Item item) {
		return count[item.getID()] > 0;
	}

	public int getCount(Item item) {
		return count[item.getID()];
	}
	
	public enum SortType {
		ALPHABETICAL("Sort (A-Z)", SortType.ALL),
		REVERSE_ALPHABETICAL("Sort (Z-A)", SortType.ALL),
		COUNT("Sort (Count)", Item.MEDICINE, Item.OTHER, Item.BALLS, Item.HELD_ITEM, Item.BERRY),
		SELL("Sort (Sell Price)", Item.MEDICINE, Item.OTHER, Item.BALLS, Item.HELD_ITEM, Item.BERRY),
		DEFAULT("Sort (Default)", SortType.ALL),
		TYPE("Sort (Type)", Item.TMS),
		POWER("Sort (Base Power)", Item.TMS),
		CATEGORY("Sort (Category)", Item.TMS),
		ACCURACY("Sort (Accuracy)", Item.TMS),
		PRIORITY("Sort (Priority)", Item.TMS),
		PP("Sort (PP)", Item.TMS),
		BACK("Back", SortType.ALL),
		;
		
		public static final int ALL = -1;
		
		private String label;
		private ArrayList<Integer> pocket;
		
		private SortType(String label, int... pockets) {
			this.label = label;
			this.pocket = IntStream.of(pockets).boxed().collect(Collectors.toCollection(ArrayList::new));
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public static SortType[] getSortOptions(int pocket) {
			List<SortType> options = new ArrayList<>();
			for (SortType sort : values()) {
				if (sort.pocket.contains(SortType.ALL) || sort.pocket.contains(pocket)) {
					options.add(sort);
				}
			}
			return options.toArray(new SortType[0]);
		}
		
		public static int getMaxSortOptions(int pocket) {
			return getSortOptions(pocket).length;
		}
	}
	
	public void sortPocket(int pocket, SortType sortType) {
		List<Integer> pocketIDs = new ArrayList<>();
		for (int id : itemList) {
			Item item = Item.getItem(id);
			if (item.getPocket() == pocket && count[id] > 0) {
				pocketIDs.add(id);
			}
		}
		
		Comparator<Integer> comparator = null;
		
		switch (sortType) {
		case DEFAULT:
			comparator = Comparator.comparingInt(id -> Item.getItem(id).ordinal());
			break;
		case ALPHABETICAL:
			if (pocket == Item.TMS) {
				comparator = Comparator.comparing(id -> {
					Move move = Item.getItem(id).getMove();
					return move != null ? move.toString() : "";
				}, String.CASE_INSENSITIVE_ORDER);
			} else {
				comparator = Comparator.comparing(id -> Item.getItem(id).toString(), String.CASE_INSENSITIVE_ORDER);
			}
			break;
		case REVERSE_ALPHABETICAL:
			if (pocket == Item.TMS) {
				comparator = Comparator.comparing(
					(Function<Integer, String>) id -> {
						Move move = Item.getItem(id).getMove();
						return move != null ? move.toString() : "";
				}, String.CASE_INSENSITIVE_ORDER).reversed();
			} else {
				comparator = Comparator.comparing(
					(Function<Integer, String>) id -> Item.getItem(id).toString(),
					String.CASE_INSENSITIVE_ORDER).reversed();
			}
			break;
		case COUNT:
			comparator = Comparator.<Integer>comparingInt(id -> count[id]).reversed();
			break;
		case SELL:
			comparator = Comparator.<Integer>comparingInt(id -> Item.getItem(id).isSellable(1) ? Item.getItem(id).getSell() : -1).reversed();
			break;
		case TYPE:
			comparator = Comparator.comparing(id -> {
				Move move = Item.getItem(id).getMove();
				return move != null ? move.mtype.toString() : "";
			}, String.CASE_INSENSITIVE_ORDER);
			break;
		case POWER:
			comparator = Comparator.<Integer>comparingInt(id -> {
				Move move = Item.getItem(id).getMove();
				return (int) (move != null ? move.getbp(null, null, null) : -1);
			}).reversed();
			break;
		case CATEGORY:
			comparator = Comparator.comparingInt(id -> {
				Move move = Item.getItem(id).getMove();
				return move != null ? move.cat : Integer.MAX_VALUE;
			});
			break;
		case ACCURACY:
			comparator = Comparator.<Integer>comparingInt(id -> {
				Move move = Item.getItem(id).getMove();
				return move != null ? move.accuracy : 0;
			}).reversed();
			break;
		case PRIORITY:
			comparator = Comparator.<Integer>comparingInt(id -> {
				Move move = Item.getItem(id).getMove();
				return move != null ? move.priority : Integer.MIN_VALUE;
			}).reversed();
			break;
		case PP:
			comparator = Comparator.<Integer>comparingInt(id -> {
				Move move = Item.getItem(id).getMove();
				return move != null ? move.pp : 0;
			}).reversed();
			break;
		case BACK:
			break;
		}
		
		pocketIDs.sort(comparator);
		
		int index = 0;
		for (int i = 0; i < itemList.length; i++) {
			Item item = Item.getItem(itemList[i]);
			if (item.getPocket() == pocket && count[itemList[i]] > 0 && index < pocketIDs.size()) {
				itemList[i] = pocketIDs.get(index++);
			}
		}
	}
	
	@Override
	public Bag clone() {
		Bag copy = new Bag(this.p);
		copy.itemList = this.itemList.clone();
		copy.count = this.count.clone();
		return copy;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		Bag other = (Bag) obj;
		return Arrays.equals(this.itemList, other.itemList)
				&& Arrays.equals(this.count, other.count);
	}
	
	@Override
	public int hashCode() {
	    int result = Arrays.hashCode(itemList);
	    result = 31 * result + Arrays.hashCode(count);
	    return result;
	}

}
