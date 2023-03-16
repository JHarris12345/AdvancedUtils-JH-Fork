package net.advancedplugins.utils.nbt.backend;

import org.bukkit.entity.Entity;

public class NBTEntity extends NBTCompound {

    private final Entity ent;

    /**
     * @param entity Any valid Bukkit Entity
     */
    public NBTEntity(Entity entity) {
        super(null, null);
        if (entity == null) {
            throw new NullPointerException("Entity can't be null!");
        }
        ent = entity;
    }

    @Override
    public Object getCompound() {
        return NBTReflectionUtil.getEntityNBTTagCompound(NBTReflectionUtil.getNMSEntity(ent));
    }

    @Override
    protected void setCompound(Object compound) {
        NBTReflectionUtil.setEntityNBTTag(compound, NBTReflectionUtil.getNMSEntity(ent));
    }

}