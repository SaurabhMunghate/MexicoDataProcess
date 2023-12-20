package com.shatam.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface MultiMap<K,V> extends Serializable {
	
	void clear();
	
	boolean containsKey(K key);
	
	boolean containsValue(V value);
	
	Collection<V> get(K key);
	
	boolean isEmpty();
	
	Set<K> keySet();
	
	Collection<V> put(K key, V value);
	
	Collection<V> remove(K key);
	
	int size();
	
	Collection<V> values();
}
