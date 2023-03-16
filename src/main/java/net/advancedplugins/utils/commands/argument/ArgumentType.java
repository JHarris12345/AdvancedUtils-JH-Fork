package net.advancedplugins.utils.commands.argument;

public interface ArgumentType<T> {

    T parse(String arg);
}