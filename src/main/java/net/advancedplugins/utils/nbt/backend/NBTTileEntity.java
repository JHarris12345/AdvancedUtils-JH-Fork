package net.advancedplugins.utils.nbt.backend;

import org.bukkit.block.BlockState;

public class NBTTileEntity extends net.advancedplugins.utils.nbt.backend.NBTCompound {

    private final BlockState tile;

    public NBTTileEntity(BlockState tile) {
        super(null, null);
        this.tile = tile;
    }

    public Object getCompound() {
        return NBTReflectionUtil.getTileEntityNBTTagCompound(tile);
    }

    protected void setCompound(Object compound) {
        NBTReflectionUtil.setTileEntityNBTTagCompound(tile, compound);
    }

}
