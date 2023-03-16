package net.advancedplugins.utils;

import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.Optional;

public class LocalLocation extends Location {

    private String locationName = null;

    public LocalLocation(World w, double x, double y, double z) {
        super(w, x, y, z);
    }

    public LocalLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public LocalLocation(Location loc) {
        super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static String locToEncode(Location location) {
        return new LocalLocation(location).getEncode();
    }

    public String getEncode() {
        if (getWorld() == null)
            return null;

        return getWorld().getName() + ";" + getX() + ";" + getY() + ";" + getZ();
    }

    public static LocalLocation getFromEncode(String encode) {
        String[] data = encode.split(";");

        World world = Bukkit.getWorld(data[0]);
        double x = Double.parseDouble(data[1]);
        double y = Double.parseDouble(data[2]);
        double z = Double.parseDouble(data[3]);

        return new LocalLocation(world, x, y, z);
    }

    public boolean isInDistance(Location loc, int distance) {
        World w = loc.getWorld();
        if (!w.equals(getWorld()))
            return false;
        distance = distance ^ 2;
        if (distanceSquared(loc) > distance)
            return false;
        return true;
    }

    public void setName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void playParticles(Effect effect, int amount, float spread) {
        //getWorld().spigot().playEffect(this, effect, 0, 0, spread, spread, spread, 0, amount, 32);
    }

    public LocalLocation clone() {
        return new LocalLocation(super.clone());
    }

    public Optional<Block> getOptionalBlock() {
        return Optional.of(getBlock());
    }

    public String getChunkEncode() {
        return getChunkEncode(getChunk());
    }

    public static String getChunkEncode(Chunk c) {
        return c.getWorld().getName() + ";" + c.getX() + ";" + c.getZ();
    }
}
