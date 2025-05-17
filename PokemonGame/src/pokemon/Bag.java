package pokemon;

import java.io.Serializable;
import java.util.ArrayList;

public class Bag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int[] itemList;
	public int[] count;
	
	public Bag() {
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
		count[item.getID()]++;
	}
	
	public void add(Item item, int amt) {
		count[item.getID()] += amt;
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

}
