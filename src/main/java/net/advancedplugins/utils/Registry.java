package net.advancedplugins.utils;

public class Registry {

    public static String get() {
        if(!"%%__USER__%%".equals("%%__USER__%%"))
            return null;

        return "9454";
//        return "%%__USER__%%";
    }
}
