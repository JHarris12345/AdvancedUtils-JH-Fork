package net.advancedplugins.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedTime {
    public static final String DEFAULT_FORMAT = "<years:y ><months:mo ><weeks:w ><days:d ><hours:h ><minutes:m ><seconds:s>";

    /**
     * Format time to text using default format
     * @param timeUnit TimeUnit
     * @param duration Time represented by time unit
     * @return Formatted text
     */
    public static String format(TimeUnit timeUnit, long duration) {
        return format(DEFAULT_FORMAT, timeUnit, duration);
    }

    /**
     * Format time to text
     * @param timeFormat Format of time which is made of "blocks" with key and extra text after value.
     *                   Example block: &lt;minutes:min &gt;
     *                   Keys: years,months,weeks,days,hours,minutes,seconds
     * @param timeUnit TimeUnit
     * @param duration Time represented by time unit
     * @return Formatted text
     */
    public static String format(String timeFormat, TimeUnit timeUnit, long duration) {
        long origin = timeUnit.toSeconds(duration);

        Map<String, Long> timeMap = new HashMap<>();
        timeMap.put("years", origin / 31536000);
        timeMap.put("months", origin % 31536000 / 2592000); // Months calculated with 30 days.
        timeMap.put("weeks", origin % 2592000 / 604800);
        timeMap.put("days", origin % 604800 / 86400);
        timeMap.put("hours", origin % 86400 / 3600);
        timeMap.put("minutes", origin % 3600 / 60);
        timeMap.put("seconds", origin % 60);

        String output = timeFormat;
        Map<String,String> replaces = new HashMap<>();
        timeMap.forEach((key,value) -> {
            String regex = "<"+key+":([^>]+)>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(timeFormat);

            while(matcher.find()) {
                String extra = matcher.group();

                String toReplace = "<" + key + ":" + extra + ">";
                String replace = value + extra;

                if(value <= 0) {
                    replaces.put(toReplace,"");
                } else {
                    replaces.put(toReplace,replace);
                }
            }
        });
        for (Map.Entry<String, String> entry : replaces.entrySet()) {
            output = output.replaceAll(entry.getKey(),entry.getValue());
        }

        return output;
    }

    /**
     * Convert text to milliseconds
     * @param time Text which defines time. Example: 10d20h3m40s, 3h10s
     * @return Milliseconds
     */
    public static long textToMillis(String time) {
        String temp = "";

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        for (String s : time.split("")) {
            int num = -1;

            try {
                num = Integer.parseInt(s);
            } catch (Exception e) {}

            if(num >= 0) {
                temp += num;
                continue;
            }

            if(temp.isEmpty()) {
                continue;
            }

            switch (s) {
                case "d":
                    days = Integer.parseInt(temp);
                    break;
                case "h":
                    hours = Integer.parseInt(temp);
                    break;
                case "m":
                    minutes = Integer.parseInt(temp);
                    break;
                case "s":
                    seconds = Integer.parseInt(temp);
                    break;
            }

            temp = "";
        }

        hours += days * 24;
        minutes += hours * 60;
        seconds += minutes * 60;

        return seconds * 1000L;
    }

    public static String oldToNewPattern(String old, List<String> keys) {
        StringBuilder output = new StringBuilder();

        while(old.contains("%d") && !keys.isEmpty()) {
            int start = old.indexOf("%d");
            int extraStart = start + 2;
            int end  = old.indexOf("%d", start);

            String extra = old.substring(extraStart, end);
            output.append("<")
                    .append(keys.get(0))
                    .append(":")
                    .append(extra)
                    .append(">");
            keys.remove(0);
            old = old.replaceFirst("%d"+extra, "");
        }

        return output.toString();
    }
}
