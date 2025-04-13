package net.advancedplugins.utils.data.cache.iface;

import java.util.function.Consumer;

public interface IAsyncCache<K,V> extends ICache<K,V> {
    void getAsync(K key, Consumer<V> then);
}
