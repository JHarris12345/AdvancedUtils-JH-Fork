package net.advancedplugins.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
@AllArgsConstructor @Getter @Setter
public class Pair<T, V> {
    private T key;
    private V value;

    public static <S, U> Pair<S, U> of(S key, U value) {
        return new Pair<>(key, value);
    }
}
