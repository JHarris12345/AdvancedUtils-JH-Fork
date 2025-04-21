package net.advancedplugins.utils.data.cache.iface;

import java.util.concurrent.CompletableFuture;

public interface IAsyncCache<K,V> extends ICache<K,V> {
    CompletableFuture<V> getAsync(K key);
}
