package net.advancedplugins.utils.data.cache;

import com.j256.ormlite.dao.Dao;
import net.advancedplugins.utils.data.DatabaseController;
import net.advancedplugins.utils.data.cache.iface.ICache;
import net.advancedplugins.utils.data.cache.iface.ISavable;
import net.advancedplugins.utils.trycatch.TryCatchUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class DataCache<K,V> implements ICache<K,V>, ISavable<K,V> {

    private final Map<K,V> cache;
    private final Dao<V,K> dao;

    @SuppressWarnings("unchecked")
    public DataCache(DatabaseController controller) {
        this.cache = new HashMap<>();

        ParameterizedType type = (ParameterizedType) getClass().getGenericInterfaces()[0];
        Type[] typeArgs = type.getActualTypeArguments();
        Class<K> keyClass = (Class<K>) typeArgs[0];
        Class<V> valueClass = (Class<V>) typeArgs[1];

        this.dao = controller.getDao(valueClass,keyClass);
    }

    @Override
    public V get(K key) {
        return this.contains(key) ?
                this.cache.get(key) : this.load(key);
    }

    @Override
    public void set(K key, V value) {
        this.cache.put(key,value);
    }

    @Override
    public void invalidate(K key) {
        this.cache.remove(key);
    }

    @Override
    public void invalidateAll() {
        this.cache.clear();
    }

    @Override
    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }

    @Override
    public void remove(K key) {
        TryCatchUtil.tryRun(() -> this.dao.deleteById(key));
    }

    @Override
    public V load(K key) {
        V value = TryCatchUtil.tryAndReturn(() -> this.dao.queryForId(key));
        if(value == null) return null;
        this.cache.put(key,value);
        return value;
    }

    @Override
    public Set<V> loadAll() {
        TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList<V>())
                .forEach(value -> this.cache.put(
                        TryCatchUtil.tryAndReturn(() -> this.dao.extractId(value)),
                        value
                ));
        return new HashSet<>(this.cache.values());
    }

    @Override
    public void modify(K key, Consumer<V> action) {
        boolean contains = this.contains(key);
        if(!exists(key) && !contains) return;

        action.accept(this.get(key));
        if(!contains) {
            this.save(key);
            this.invalidate(key);
        }
    }

    @Override
    public void modifyMultiple(Set<K> keys, Consumer<V> action) {
        this.cache.entrySet()
                .stream()
                .filter(entry -> keys.contains(entry.getKey()))
                .forEach(entry -> {
                    action.accept(entry.getValue());
                });

        TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList<V>())
                .stream()
                .map(value -> new AbstractMap.SimpleEntry<K,V>(
                        TryCatchUtil.tryAndReturn(() -> this.dao.extractId(value)),
                        value
                ))
                .filter(entry -> !this.contains(entry.getKey()))
                .filter(entry -> keys.contains(entry.getKey()))
                .forEach(entry -> {
                    action.accept(entry.getValue());
                    this.save(entry.getKey());
                    this.invalidate(entry.getKey());
                });
    }

    @Override
    public void modifyAll(Consumer<V> action) {
        this.cache.values().forEach(action);

        TryCatchUtil.tryOrDefault(this.dao::queryForAll, new ArrayList<V>())
                .stream()
                .map(value -> new AbstractMap.SimpleEntry<K,V>(
                        TryCatchUtil.tryAndReturn(() -> this.dao.extractId(value)),
                        value
                ))
                .filter(entry -> !this.contains(entry.getKey()))
                .forEach(entry -> {
                    action.accept(entry.getValue());
                    this.save(entry.getKey());
                    this.invalidate(entry.getKey());
                });
    }

    @Override
    public void save(K key) {
        if(!contains(key)) return;
        TryCatchUtil.tryRun(() -> this.dao.createOrUpdate(this.cache.get(key)));
    }

    @Override
    public void saveAll() {
        this.cache.keySet().forEach(this::save);
    }

    @Override
    public boolean exists(K key) {
        if(this.contains(key)) return true;
        return TryCatchUtil.tryOrDefault(() -> this.dao.idExists(key),false);
    }
}
