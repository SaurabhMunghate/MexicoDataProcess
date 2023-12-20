package com.shatam.stringSimilarity;

public interface IMap<K, V> {

	public void put(K t, V v);

	public V get(K t);

	public int size();
}
