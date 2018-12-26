package indexer;

import java.util.HashMap;
import java.util.Iterator;


public class Bag implements Iterable<String>{
	
	private HashMap<String, Integer> bag = new HashMap<>();
	private int size;
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean contains(Object key) {
		return bag.containsKey(key);
	}

	public Iterator<String> iterator() {
		return bag.keySet().iterator();
	}

	public Object[] toArray() {
		return bag.keySet().toArray();
	}

	public boolean add(String key) {
		++size;
		bag.put(key, !bag.containsKey(key) ? 1 : bag.get(key) + 1);
		return true;
	}

	public boolean remove(String key) {
		if(!bag.containsKey(key))
			return false;
		int oldCounter = bag.get(key);
		if(oldCounter == 1)
			bag.remove(key);
		else
			bag.put(key, oldCounter - 1);
		return true;
		
	}
	
	public Integer getCount(String key) {
		return !bag.containsKey(key) ? 0 : bag.get(key);
	}


}
