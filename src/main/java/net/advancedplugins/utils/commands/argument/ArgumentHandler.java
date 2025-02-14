package net.advancedplugins.utils.commands.argument;

import com.google.common.collect.Maps;

import java.util.Map;

public class ArgumentHandler {
    private static Map<Class<?>, ArgumentType<?>> argumentTypes = Maps.newHashMap();

    public static void register(Class<?> clazz, ArgumentType<?> argumentType) {
        argumentTypes.put(clazz, argumentType);
    }

    @SuppressWarnings("unchecked")
    public static <T> ArgumentType<T> getArgumentType(Class<?> clazz) {
        return (ArgumentType<T>) argumentTypes.get(clazz);
    }
}