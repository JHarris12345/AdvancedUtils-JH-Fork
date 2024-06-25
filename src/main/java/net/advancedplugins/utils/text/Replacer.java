package net.advancedplugins.utils.text;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Replacer {
    private final Map<String, Object> variables = Maps.newHashMap();
    private final Map<String, Supplier<Object>> retrievableVariables = Maps.newHashMap();
    private OfflinePlayer player;
    private boolean usePlaceholderApi;

    public static String to(String string, UnaryOperator<Replacer> replacer) {
        return replacer.apply(new Replacer()).applyTo(string);
    }

    public static Replacer of(Map<String, String> placeholderValues) {
        Replacer replacer = new Replacer();
        for (Map.Entry<String, String> entry : placeholderValues.entrySet()) {
            replacer.set(entry.getKey(), entry.getValue());
        }
        return replacer;
    }

    public Replacer set(String variable, Object value) {
        if(value instanceof Iterable<?>) {
            value = String.join("<new>", (Iterable<? extends CharSequence>)value);
        }
        if(value instanceof BigDecimal) {
            value = ((BigDecimal) value).stripTrailingZeros().toPlainString();
        }
        this.variables.put("%" + variable + "%", value);
        return this;
    }

    public Replacer set(String variable, Supplier<Object> supplier) {
        this.retrievableVariables.put("%" + variable + "%", supplier);
        return this;
    }

    public Replacer tryAddPapi(OfflinePlayer player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.player = player;
            this.usePlaceholderApi = true;
        }
        return this;
    }

    public HashMap<String, String> getPlaceholders() {
        HashMap<String, String> base = new HashMap<>();
        for (Map.Entry<String, Object> entry : this.variables.entrySet())
            base.put(entry.getKey(), String.valueOf(entry.getValue()));
        for (Map.Entry<String, Supplier<Object>> entry : this.retrievableVariables.entrySet())
            base.put(entry.getKey(), Objects.toString(entry.getValue().get()));
        return base;
    }

    public String applyTo(String string) {
        String result = string;
        for (Map.Entry<String, Object> entry : this.variables.entrySet()) {
            result = result.replace(entry.getKey(), Objects.toString(entry.getValue()));
        }
        for (Map.Entry<String, Supplier<Object>> entry : this.retrievableVariables.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), Objects.toString(entry.getValue().get()));
            }
        }
       if (this.usePlaceholderApi) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(this.player, result);
        }
        return result;
    }
}