package net.advancedplugins.utils.text;

public class BooleanConverter {

    private BooleanConverter() {}

    public static String booleanToYesNo(boolean bool) {
        return bool ? "yes" : "no";
    }
}
