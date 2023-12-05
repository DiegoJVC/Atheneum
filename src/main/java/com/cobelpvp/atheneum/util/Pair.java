package com.cobelpvp.atheneum.util;

import java.beans.ConstructorProperties;

public class Pair<K, V> {
    private K key;

    private V value;

    @ConstructorProperties({"key", "value"})
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}
