package net.advancedplugins.utils.data.cache.iface;

import java.util.Set;
import java.util.function.Consumer;

public interface IAsyncSavable<K,V> extends ISavable<K,V>{
    void loadAsync(K key, Consumer<V> then);
    void loadAsyncAll(Consumer<Set<V>> then);

    void modifyAsync(K key, Consumer<V> action);
    void modifyAsyncMultiple(Set<K> keys, Consumer<V> action);
    void modifyAsyncAll(Consumer<V> action);

    void saveAsync(K key);
    void saveAsyncAll();

    void removeAsync(K key);

    void existsAsync(K key, Consumer<Boolean> then);
}
