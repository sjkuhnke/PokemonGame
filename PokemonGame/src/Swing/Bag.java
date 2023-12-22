package Swing;

import java.io.Serializable;
import java.util.ArrayList;

public class Bag implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Item[] bag;
	public int[] count;
	
	public Bag() {
		bag = new Item[Item.values().length];
		count = new int[Item.values().length];
	}
	
	public void add(Item item) {
		int index = item.getID();
		if (bag[index] == null) {
			bag[index] = item;
			count[index] = 1;
		} else {
			count[index]++;
		}
	}
	
	public void add(Item item, int amt) {
		int index = item.getID();
		if (bag[index] == null) {
			bag[index] = item;
			count[index] = amt;
		} else {
			count[index] += amt;
		}
	}
	
	public void remove(Item item) {
		int index = item.getID();
		if (--count[index] == 0) bag[index] = null;
	}
	
	public ArrayList<Entry> getItems() {
		ArrayList<Entry> items = new ArrayList<>();
		for (int i = 0; i < bag.length; i++) {
			if (bag[i] == null) continue;
			if (i == 18) {
				items.add(0, new Entry(bag[i], count[i]));
			} else {
				items.add(new Entry(bag[i], count[i]));
			}
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
	}

	public boolean contains(int id) {
		return bag[id] != null;
	}

}
