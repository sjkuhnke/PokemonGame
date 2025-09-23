package util;

import java.util.ArrayList;

public interface DeepClonable<T> {
	T deepClone();
	
	public static <T> ArrayList<T> deepCloneList(ArrayList<? extends DeepClonable<T>> list){
		ArrayList<T> result = new ArrayList<>(list.size());
		for (DeepClonable<T> item : list) {
			result.add(item.deepClone());
		}
		return result;
	}
}
