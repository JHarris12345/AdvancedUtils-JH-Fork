package net.advancedplugins.utils.data.cache.iface;

import java.util.Set;
import java.util.function.Consumer;

public interface ISavable<K, V> {
	V load(K key);

	Set<V> loadAll();

	Set<V> loadAll(boolean ignoreCached);

	void modify(K key, Consumer<V> action);

	void modifyMultiple(Set<K> keys, Consumer<V> action);

	void modifyAll(Consumer<V> action);

	void loopAll(Consumer<V> action);

	Set<V> loopAll();

	void save(K key);

	void saveValue(V value);

	void saveAll();

	void create(K key, V value);

	void create(V value);

	void remove(K key);

	void removeAll();

	boolean exists(K key);
}
