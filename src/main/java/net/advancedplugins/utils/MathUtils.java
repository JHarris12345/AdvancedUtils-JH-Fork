package net.advancedplugins.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Insprill
 */
public class MathUtils {

    /**
     * @return an int between the 2 values provided.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * @return a long between the 2 values provided.
     */
    public static long clamp(long value, long min, long max) {
        if (min > max)
            return max;
        return Math.max(min, Math.min(max, value));
    }

    /**
     * @return a double between the 2 values provided.
     */
    public static double clamp(double value, double min, double max) {
        if (min > max)
            return max;
        return Math.max(min, Math.min(max, value));
    }

    /**
     * @return a random number between two numbers.
     */
    public static int randomBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }

    /**
     * Gets the closest number in a list to the number you provided.
     *
     * @param num     Number to get closest of.
     * @param numList List of Integers.
     * @return The Integer in numList closest to num.
     */
    public static int getClosestInt(int num, List<Integer> numList) {
        int min = Integer.MAX_VALUE;
        int closest = num;
        for (int i : numList) {
            int diff = Math.abs(i - num);
            if (diff < min) {
                min = diff;
                closest = i;
            }
        }
        return closest;
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Byte, false otherwise.
     */
    public static boolean isByte(String number) {
        try {
            Byte.parseByte(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Short, false otherwise.
     */
    public static boolean isShort(String number) {
        try {
            Short.parseShort(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Integer, false otherwise.
     */
    public static boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Long, false otherwise.
     */
    public static boolean isLong(String number) {
        try {
            Long.parseLong(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Float, false otherwise.
     */
    public static boolean isFloat(String number) {
        try {
            Float.parseFloat(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param number String you want to test.
     * @return True if the string is a valid Double, false otherwise.
     */
    public static boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
