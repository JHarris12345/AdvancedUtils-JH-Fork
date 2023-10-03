package net.advancedplugins.utils;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TotemUndying {

    public boolean playEffect(@NotNull LivingEntity target, @NotNull EquipmentSlot equipmentSlot, int regenTicks, int fireResTicks, int absorTicks, boolean callEvent) {
        if (callEvent) {
            EntityResurrectEvent event = new EntityResurrectEvent(target, equipmentSlot);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
        }

        // https://minecraft.fandom.com/wiki/Totem_of_Undying#Effect
        List<PotionEffect> activeEffects = new ArrayList<>(target.getActivePotionEffects());
        activeEffects.forEach(potionEffect -> target.removePotionEffect(potionEffect.getType()));
        target.playEffect(EntityEffect.TOTEM_RESURRECT);
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenTicks, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, fireResTicks, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, absorTicks, 2));
        return true;
    }
}
