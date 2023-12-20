package com.shatam.stringSimilarity;

import java.util.HashMap;
import java.util.Map;

public class MemHashMap<K, V> implements IMap<K, V> {

	private MemHashMap() {
	}

	private Map<K, V> map = new HashMap<K, V>();

	public static <K, V> MemHashMap<K, V> getInstance() {
		return new MemHashMap<K, V>();
	}

	@Override
	public void put(K t, V v) {
		map.put(t, v);
	}

	@Override
	public V get(K t) {
		return map.get(t);
	}
	
	@Override
	public int size() {
		return map.size();
	}

}
