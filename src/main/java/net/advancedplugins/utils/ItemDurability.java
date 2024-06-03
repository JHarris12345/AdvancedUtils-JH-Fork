package net.advancedplugins.utils;

import lombok.Getter;
import net.advancedplugins.utils.hooks.HookPlugin;
import net.advancedplugins.utils.hooks.HooksHandler;
import net.advancedplugins.utils.hooks.plugins.ItemsAdderHook;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"UnusedReturnValue", "deprecation"})
public class ItemDurability {
    private final @Nullable LivingEntity itemHolder;
    private ItemStack item;
    @Getter
    private int dealtDamage = 0;

    private final boolean itemsAdder;

    public ItemDurability(ItemStack input) {
        this(null, input);
    }

    public ItemDurability(@Nullable LivingEntity itemHolder, ItemStack input) {
        this.itemHolder = itemHolder;
        this.item = (input == null) ? new ItemStack(Material.AIR) : input;
        itemsAdder = HooksHandler.getHook(HookPlugin.ITEMSADDER) != null &&
                ((ItemsAdderHook) HooksHandler.getHook(HookPlugin.ITEMSADDER)).isCustomItem(item);
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
        return item.getEnchantmentLevel(VanillaEnchants.displayNameToEnchant("unbreaking"));
    }

    /**
     * Damages the item.
     *
     * @param amount Amount to damage item by.
     */
    public ItemDurability damageItem(int amount) {
        try {
            if (!ASManager.isDamageable(item.getType()) || item.getType().name().contains("SKULL") || ASManager.isUnbreakable(item))
                return this;

            if (amount < 0) {
                healItem(amount);
                return this;
            }

            if (this.itemHolder instanceof Player) {
                PlayerItemDamageEvent event = new PlayerItemDamageEvent((Player) this.itemHolder, this.item, amount);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) return this;
                amount = event.getDamage();
            }

            int max = getMaxDurability();
//            Bukkit.broadcastMessage("§aDo damage: " + amount + ", Max durability: " + max);
            if (getDurability() + amount > max) {
                setDurability(max);
                return this;
            }

//            Bukkit.broadcastMessage("§cDo damage: " + amount + ", new dur: " + (getDurability() + amount));
            setDurability(getDurability() + amount);
            dealtDamage += amount;
        } catch (Exception ignored) {
        }
        return this;
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
    public ItemDurability healItem(int amount) {
        amount = (short) Math.abs(amount);

        if (!ASManager.isDamageable(item.getType())) return this;

        if (item.getType().name().contains("SKULL"))
            return this;

        if (this.itemHolder instanceof Player) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent((Player) this.itemHolder, this.item, amount);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return this;
            amount = event.getDamage();
        }

        if (getDurability() - amount < 0) {
            repairItem();
            return this;
        }

        setDurability(getDurability() - amount);
        return this;
    }

    public ItemDurability handleDurabilityChange(int amount) {
        if (amount < 0) {
            return damageItem((short) -amount);
        } else {
            return healItem((short) (amount));
        }
    }

    /**
     * @return The maximum durability of the item.
     */
    public int getMaxDurability() {
        if (itemsAdder) {
            return ((ItemsAdderHook) HooksHandler.getHook(HookPlugin.ITEMSADDER)).getCustomItemMaxDurability(item);
        }
        return item.getType().getMaxDurability();
    }

    /**
     * @return The current durability of the item.
     */
    public int getDurability() {
        if (itemsAdder) {
            return getMaxDurability() - ((ItemsAdderHook) HooksHandler.getHook(HookPlugin.ITEMSADDER)).getCustomItemDurability(item);
        }
        return item.getDurability();
    }

    /**
     * Sets the item's durability.
     *
     * @param amount Durability to set.
     */
    public ItemDurability setDurability(int amount) {
        if (itemsAdder) {
            this.item = ((ItemsAdderHook) HooksHandler.getHook(HookPlugin.ITEMSADDER)).setCustomItemDurability(item,
                    amount < getMaxDurability() ? getMaxDurability() - amount : -1);
            return this;
        }

        if (amount >= this.getMaxDurability() && this.itemHolder != null && this.itemHolder instanceof Player && item.getItemMeta() instanceof Damageable) {
            // is not cancellable
            Bukkit.getPluginManager().callEvent(new PlayerItemBreakEvent((Player) this.itemHolder, this.item));
        }

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