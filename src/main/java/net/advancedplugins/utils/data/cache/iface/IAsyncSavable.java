package net.advancedplugins.utils.data.cache.iface;

import java.util.Set;
import java.util.function.Consumer;

public interface IAsyncSavable<K,V> extends ISavable<K,V>{
    void loadAsync(K key, Consumer<V> then);
    void loadAsyncAll(Consumer<Set<V>> then);

    void modifyAsync(K key, Consumer<V> action,Consumer<V> then);
    void modifyAsyncMultiple(Set<K> keys, Consumer<V> action,Runnable then);
    void modifyAsyncAll(Consumer<V> action,Runnable then);

    void saveAsync(K key,Runnable then);
    void saveAsyncAll(Runnable then);

    void createAsync(K key, V value, Runnable then);

    void removeAsync(K key,Runnable then);
    void removeAsyncAll(Runnable then);

    void existsAsync(K key, Consumer<Boolean> then);
}
