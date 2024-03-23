package net.advancedplugins.utils.items;
import net.advancedplugins.utils.ASManager;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Glow extends Enchantment {

    public static Glow ench;

    public static void register() {
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JavaPlugin plugin = ASManager.getInstance();
                NamespacedKey key = new NamespacedKey(plugin, "ae_glow");

                Glow glow = new Glow(key);
                Enchantment.registerEnchantment(glow);
                ench = glow;
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
            }
    }

    private Glow(NamespacedKey i) {
        super(i);
    }

    @NotNull
    @Override
    public String getName() {
        return "ae_glow";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return true;
    }
}