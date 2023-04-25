package net.advancedplugins.utils;

import net.advancedplugins.utils.nbt.NBTapi;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings({"UnusedReturnValue", "deprecation"})
public class ItemDurability {

    private ItemStack item;
    private int dealtDamage = 0;

    public ItemDurability(ItemStack input) {
        this.item = (input == null) ? new ItemStack(Material.AIR) : input;
    }

    /**
     * @return The modified ItemStack.
     */
    public ItemStack getItemStack() {
        if (item.getAmount() == 0) {
            item.setAmount(1);
        }

        if (item.getType().getMaxDurability() == 0) {
            return item;
        }

        if (isBroken() && !ASManager.isUnbreakable(item)) {
            return new ItemStack(Material.AIR);
        }

        return item;
    }

    public int getUnbreakingLevel() {
        return item.getEnchantmentLevel(Enchantment.DURABILITY);
    }

    /**
     * Damages the item.
     *
     * @param amount Amount to damage item by.
     */
    public ItemDurability damageItem(short amount) {
        try {
            if (!ASManager.isDamageable(item.getType()) || item.getType().name().contains("SKULL"))
                return this;

            if (amount < 0) {
                healItem(amount);
                return this;
            }

            int max = getMaxDurability();
            if (getDurability() + amount > max) {
                setDurability(max);
                return this;
            }

            setDurability(getDurability() + amount);
            dealtDamage += amount;
        } catch (Exception ignored) {
        }
        return this;
    }

    public int getDealtDamage() {
        return dealtDamage;
    }

    /**
     * @return True if the item is broken, false otherwise.
     */
    public boolean isBroken() {
        return getDurability() >= getMaxDurability();
    }

    /**
     * Heals the item.
     *
     * @param amount Amount to heal the item by.
     */
    public ItemDurability healItem(short amount) {
        amount = (short) Math.abs(amount);

        if (!ASManager.isDamageable(item.getType())) return this;

        if (item.getType().name().contains("SKULL"))
            return this;

        if (getDurability() - amount < 0) {
            repairItem();
            return this;
        }

        setDurability(getDurability() - amount);
        return this;
    }

    public ItemDurability handleDurabilityChange(int amount) {
        if (amount < 0) {
            return healItem((short) amount);
        } else {
            return damageItem((short) amount);
        }
    }

    /**
     * @return The maximum durability of the item.
     */
    public int getMaxDurability() {
        return item.getType().getMaxDurability();
    }

    /**
     * @return The current durability of the item.
     */
    public int getDurability() {
        return item.getDurability();
    }

    /**
     * Sets the item's durability.
     *
     * @param amount Durability to set.
     */
    public ItemDurability setDurability(int amount) {
        this.setDurabilityVersionSave(amount);
        return this;
    }

    private int getDurabilityVersionSafe() {
        if (MinecraftVersion.getVersionNumber() >= 1_13_0) {
            final ItemMeta meta = this.item.getItemMeta();
            if (meta instanceof Damageable) {
                final Damageable damageable = (Damageable) meta;
                return damageable.getDamage();
            } else return -1;
        } else return this.item.getDurability();
    }

    private void setDurabilityVersionSave(final int amount) {
        if (MinecraftVersion.getVersionNumber() >= 1_13_0) {
            final ItemMeta meta = this.item.getItemMeta();
            if (meta instanceof Damageable) {
                final Damageable damageable = ((Damageable) meta);
                damageable.setDamage(amount);
                this.item.setItemMeta(meta); // update meta instead of setting the item again in the hotbar.... whos idea was it to replace the item in the hotbar?
            } // todo: May need to add a exception here, lets see
        } else this.item.setDurability((short) amount);
    }

    /**
     * Sets the items durability to its maximum.
     */
    public ItemDurability repairItem() {
        setDurability(0);
        return this;
    }

}
