package net.advancedplugins.utils.trycatch;

public interface ITryCatch<T> {
    T run() throws Exception;
}
