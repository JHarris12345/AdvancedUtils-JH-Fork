package net.advancedplugins.utils;

import java.util.TreeMap;

public class DecimalToRoman {

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public static String toRoman(int number) {
        if(net.advancedplugins.ae.Values.m_useNumbers)
            return number+"";

        return getString(number);
    }

    public static String parseInteger(int number) {
        return getString(number);
    }

    private static String getString(int number) {
        Integer l = map.floorKey(number);
        if(number == 0)
            return "0";

        if(l == null) {
            ASManager.getInstance().getLogger().warning("Invalid number for roman numerals: " + number);
            Thread.dumpStack();
            return "?";
        }

        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + parseInteger(number - l);
    }
}
