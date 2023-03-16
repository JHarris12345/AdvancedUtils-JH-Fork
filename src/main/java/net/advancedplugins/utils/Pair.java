package net.advancedplugins.utils;

public class Pair<T, V> {

    private T key;
    private V value;

    public T getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Pair(T key, V value) {
        this.key = key;
        this.value = value;
    }
}
