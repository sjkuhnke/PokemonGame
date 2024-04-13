package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Bag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Item> bag;
	public int[] count;
	
	public Bag() {
		bag = new ArrayList<>(Arrays.asList(Item.values()));
		count = new int[Item.values().length];
		bag.remove(Item.NULL91);
		bag.remove(Item.NULL92);
		bag.remove(Item.NULL260);
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
	
	public ArrayList<Entry> getItems() {
		ArrayList<Entry> items = new ArrayList<>();
		for (Item i : bag) {
			if (i.getID() >= count.length || count[i.getID()] <= 0) continue;
			items.add(new Entry(i, count[i.getID()]));
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
			return Math.min(99, count);
		}
	}

	public boolean contains(int id) {
		return count[id] > 0;
	}

	public boolean contains(Item item) {
		return count[item.getID()] > 0;
	}

}
