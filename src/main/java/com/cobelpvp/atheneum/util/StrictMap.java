package com.cobelpvp.atheneum.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * Strict map that only allows to remove elements that are contained within, or add elements that are not.
 * <p>
 * Failing to do so results in an error, with optional error message.
 */
public final class StrictMap<E, T> extends StrictCollection {

    private final Map<E, T> map = new LinkedHashMap<>();

    public StrictMap() {
        this("Cannot remove '%s' as it is not in the map!", "Key '%s' is already in the map --> '%s'");
    }

    public StrictMap(String removeMessage, String addMessage) {
        super(removeMessage, addMessage);
    }

    public StrictMap(Map<E, T> copyOf) {
        this();

        setAll(copyOf);
    }

    public void setAll(Map<E, T> copyOf) {
        map.clear();

        for (final Entry<E, T> e : copyOf.entrySet())
            put(e.getKey(), e.getValue());
    }

    public T remove(E key) {
        final T removed = removeWeak(key);
        //Verify.verifyNotNull(removed, String.format(getCannotRemoveMessage(), key));
        return removed;
    }

    public void removeByValue(T value) {
        for (final Entry<E, T> e : map.entrySet())
            if (e.getValue().equals(value)) {
                map.remove(e.getKey());
                return;
            }

        throw new NullPointerException(String.format(getCannotRemoveMessage(), value));
    }

    public E getKeyFromValue(T value) {
        for (final Entry<E, T> e : map.entrySet())
            if (e.getValue().equals(value))
                return e.getKey();

        return null;
    }

    public T removeWeak(E value) {
        return map.remove(value);
    }

    public Object[] removeAll(Collection<E> keys) {
        final List<T> removedKeys = new ArrayList<>();

        for (final E key : keys)
            removedKeys.add(remove(key));

        return removedKeys.toArray();
    }

    public void put(E key, T value) {
        //Verify.verify(!map.containsKey(key), String.format(getCannotAddMessage(), key, map.get(key)));

        override(key, value);
    }

    public void putAll(Map<? extends E, ? extends T> m) {
        for (final Entry<? extends E, ? extends T> e : m.entrySet())
            //Verify.verify(!map.containsKey(e.getKey()), String.format(getCannotAddMessage(), e.getKey(), map.get(e.getKey())));

            override(m);
    }

    public void override(E key, T value) {
        map.put(key, value);
    }

    public void override(Map<? extends E, ? extends T> m) {
        map.putAll(m);
    }

    /**
     * Will return the key as normal or put it there and return it.
     */
    public T getOrPut(E key, T defaultToPut) {
        if (contains(key))
            return get(key);

        put(key, defaultToPut);
        return defaultToPut;
    }

    /**
     * CAN BE NULL, NO EXCEPTION THROWING
     */
    public T get(E key) {
        return map.get(key);
    }

    public T getOrDefault(E key, T def) {
        return map.getOrDefault(key, def);
    }

    public boolean contains(E key) {
        return key != null && map.containsKey(key);
    }

    public boolean containsValue(T value) {
        return value != null && map.containsValue(value);
    }

    public Set<Entry<E, T>> entrySet() {
        return map.entrySet();
    }

    public Set<E> keySet() {
        return map.keySet();
    }

    public Collection<T> values() {
        return map.values();
    }

    public void clear() {
        map.clear();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Map<E, T> getSource() {
        return map;
    }

    public int size() {
        return map.size();
    }

    public E firstKey() {
        return map.isEmpty() ? null : map.keySet().iterator().next();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}