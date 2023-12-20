package com.shatam.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiValueMap<K,V> implements MultiMap<K, V> {

	private Map<K, List<V>> map = new LinkedHashMap<K, List<V>>();
	
	@Override
	public void clear() {
		map.clear();		
	}

	@Override
	public boolean containsKey(K key) {		
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(V value) {
		for(K key: map.keySet()){
			List<V> values = map.get(key);
			if(values.contains(value)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<V> get(K key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		if(size() == 0)
			return true;
		return false;
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> put(K key, V value) {
		List<V> l = map.get(key);
		if(l == null){
			l = new ArrayList<V>();
		}
		l.add(value);
		return map.put(key, l);
	}

	@Override
	public Collection<V> remove(K key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<V>();
		for(K key: map.keySet()){
			List<V> v = map.get(key);
			values.addAll(v);
		}
		return values;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MultiValueMap)) {
			return false;
		}
		final MultiValueMap<K, V> other = (MultiValueMap<K, V>) obj;
	
		if (this.keySet() != other.keySet() && (this.keySet() == null || !this.keySet().equals(other.keySet()))) {
			return false;
		}
		final Collection thisValues = this.values();
		final Collection otherValues = other.values();
		if (thisValues != otherValues && (thisValues == null || !thisValues.equals(otherValues))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + (this.map != null ? this.map.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
