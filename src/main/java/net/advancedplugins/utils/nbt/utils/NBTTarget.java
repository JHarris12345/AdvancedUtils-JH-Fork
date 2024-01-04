package net.advancedplugins.utils.nbt.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public @interface NBTTarget {
    public String value();

    public Type type() default Type.AUTOMATIC;

    public enum Type {
        AUTOMATIC, GET, SET, HAS
    }
}