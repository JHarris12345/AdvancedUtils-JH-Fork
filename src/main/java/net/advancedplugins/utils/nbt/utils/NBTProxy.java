package net.advancedplugins.utils.nbt.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface NBTProxy {

    final Map<Class<?>, NBTHandler<Object>> handlers = new HashMap<>();

    public default void init() {

    }

    public default Casing getCasing() {
        return Casing.PascalCase;
    }

    @SuppressWarnings("unchecked")
    public default <T> NBTHandler<T> getHandler(Class<T> clazz) {
        return (NBTHandler<T>) handlers.get(clazz);
    }

    public default Collection<NBTHandler<Object>> getHandlers() {
        return handlers.values();
    }

    @SuppressWarnings("unchecked")
    public default <T> void registerHandler(Class<T> clazz, NBTHandler<T> handler) {
        handlers.put(clazz, (NBTHandler<Object>) handler);
    }

}