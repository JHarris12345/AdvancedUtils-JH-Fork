package net.advancedplugins.utils.nbt.backend;

import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.nbt.utils.PackageWrapper;
import org.bukkit.Bukkit;

/**
 * Class used to get commonly used NMS & CraftBukkit classes across any Minecraft version.
 */
public enum ClassWrapper {
    // Blocks
    CRAFT_BlockData("org.bukkit.craftbukkit", "", "block.data.CraftBlockData", MinecraftVersion.MC1_13_R1),
    CRAFT_World("org.bukkit.craftbukkit", "", "CraftWorld", MinecraftVersion.MC1_8_R3),
    // Entities
    CRAFT_Entity("org.bukkit.craftbukkit", "", "entity.CraftEntity", MinecraftVersion.MC1_8_R3),
    CRAFT_Player("org.bukkit.craftbukkit", "", "entity.CraftPlayer", MinecraftVersion.MC1_8_R3),
    // Misc
    CRAFT_MagicNumbers("org.bukkit.craftbukkit", "", "util.CraftMagicNumbers", MinecraftVersion.MC1_8_R3),
    CRAFT_ItemStack("org.bukkit.craftbukkit", "", "inventory.CraftItemStack", MinecraftVersion.MC1_8_R3),
    CRAFT_MetaBook("org.bukkit.craftbukkit", "", "inventory.CraftMetaBook", MinecraftVersion.MC1_8_R3),
    CRAFT_Enchantment("org.bukkit.craftbukkit", "", "enchantments.CraftEnchantment", MinecraftVersion.MC1_8_R3),
    NMS_NBTACCOUNTER(PackageWrapper.NMS, "NBTReadLimiter", MinecraftVersion.MC1_20_R3, null, "net.minecraft.nbt", "net.minecraft.nbt.NbtAccounter"),
    // NBT
    NMS_NBTBase("net.minecraft", "nbt", "NBTBase", MinecraftVersion.MC1_8_R3),
    NMS_NBTTagString("net.minecraft", "nbt", "NBTTagString", MinecraftVersion.MC1_8_R3),
    NMS_NBTTagInt("net.minecraft", "nbt", "NBTTagInt", MinecraftVersion.MC1_8_R3),
    NMS_NBTTagCompound("net.minecraft", "nbt", "NBTTagCompound", MinecraftVersion.MC1_8_R3),
    NMS_NBTTagList("net.minecraft", "nbt", "NBTTagList", MinecraftVersion.MC1_8_R3),
    NMS_NBTCompressedStreamTools("net.minecraft", "nbt", "NBTCompressedStreamTools", MinecraftVersion.MC1_8_R3),
    // Blocks
    NMS_TileEntity("net.minecraft", "world.level.block.entity", "TileEntity", MinecraftVersion.MC1_8_R3),
    NMS_Block("net.minecraft", "world.level.block", "Block", MinecraftVersion.MC1_8_R3),
    NMS_IBlockData("net.minecraft", "world.level.block.state", "IBlockData", MinecraftVersion.MC1_8_R3),
    NMS_BlockPosition("net.minecraft", "core", "BlockPosition", MinecraftVersion.MC1_8_R3),
    NMS_World("net.minecraft", "server.level", "WorldServer", MinecraftVersion.MC1_8_R3),
//    NMS_Material("net.minecraft", "world.level.material", "Material", MinecraftVersion.MC1_8_R3),
    // Items
    NMS_ItemStack("net.minecraft", "world.item", "ItemStack", MinecraftVersion.MC1_8_R3),
    NMS_ItemTool("net.minecraft", "world.item", "ItemTool", MinecraftVersion.MC1_8_R3),
    // Entities
    NMS_Entity("net.minecraft", "world.entity", "Entity", MinecraftVersion.MC1_8_R3),
    NMS_EntityPlayer("net.minecraft", "server.level", "EntityPlayer", MinecraftVersion.MC1_8_R3),
    NMS_EntityTypes("net.minecraft", "world.entity", "EntityTypes", MinecraftVersion.MC1_8_R3),
    NMS_EntityHuman("net.minecraft", "world.entity.player", "EntityHuman", MinecraftVersion.MC1_8_R3),
    NMS_DamageSource("net.minecraft", "world.damagesource", "DamageSource", MinecraftVersion.MC1_8_R3),
    // Misc
    NMS_MojangsonParser("net.minecraft", "nbt", "MojangsonParser", MinecraftVersion.MC1_8_R3),
    NMS_IChatBaseComponent("net.minecraft", "network.chat", "IChatBaseComponent", MinecraftVersion.MC1_8_R3),
    NMS_IChatBaseComponent$ChatSerializer("net.minecraft", "network.chat", "IChatBaseComponent$ChatSerializer", MinecraftVersion.MC1_8_R3),
    NMS_EnumHand("net.minecraft", "world", "EnumHand", MinecraftVersion.MC1_9_R1),
    NMS_Explosion("net.minecraft", "world.level", "Explosion", MinecraftVersion.MC1_8_R3),
    NMS_PathEntity("net.minecraft", "world.level.pathfinder", "PathEntity", MinecraftVersion.MC1_8_R3),
    CRAFT_ITEMSTACK(PackageWrapper.CRAFTBUKKIT, "inventory.CraftItemStack", null, null),
    CRAFT_METAITEM(PackageWrapper.CRAFTBUKKIT, "inventory.CraftMetaItem", null, null),
    CRAFT_ENTITY(PackageWrapper.CRAFTBUKKIT, "entity.CraftEntity", null, null),
    CRAFT_WORLD(PackageWrapper.CRAFTBUKKIT, "CraftWorld", null, null),
    CRAFT_PERSISTENTDATACONTAINER(PackageWrapper.CRAFTBUKKIT, "persistence.CraftPersistentDataContainer",
            MinecraftVersion.MC1_14_R1, null),
    NMS_NBTBASE(PackageWrapper.NMS, "NBTBase", null, null, "net.minecraft.nbt", "net.minecraft.nbt.Tag"),
    NMS_NBTTAGSTRING(PackageWrapper.NMS, "NBTTagString", null, null, "net.minecraft.nbt", "net.minecraft.nbt.StringTag"),
    NMS_NBTTAGINT(PackageWrapper.NMS, "NBTTagInt", null, null, "net.minecraft.nbt", "net.minecraft.nbt.IntTag"),
    NMS_NBTTAGFLOAT(PackageWrapper.NMS, "NBTTagFloat", null, null, "net.minecraft.nbt", "net.minecraft.nbt.FloatTag"),
    NMS_NBTTAGDOUBLE(PackageWrapper.NMS, "NBTTagDouble", null, null, "net.minecraft.nbt", "net.minecraft.nbt.DoubleTag"),
    NMS_NBTTAGLONG(PackageWrapper.NMS, "NBTTagLong", null, null, "net.minecraft.nbt", "net.minecraft.nbt.LongTag"),
    NMS_ITEMSTACK(PackageWrapper.NMS, "ItemStack", null, null, "net.minecraft.world.item", "net.minecraft.world.item.ItemStack"),
    NMS_NBTTAGCOMPOUND(PackageWrapper.NMS, "NBTTagCompound", null, null, "net.minecraft.nbt", "net.minecraft.nbt.CompoundTag"),
    NMS_NBTTAGLIST(PackageWrapper.NMS, "NBTTagList", null, null, "net.minecraft.nbt", "net.minecraft.nbt.ListTag"),
    NMS_NBTCOMPRESSEDSTREAMTOOLS(PackageWrapper.NMS, "NBTCompressedStreamTools", null, null, "net.minecraft.nbt", "net.minecraft.nbt.NbtIo"),
    NMS_MOJANGSONPARSER(PackageWrapper.NMS, "MojangsonParser", null, null, "net.minecraft.nbt", "net.minecraft.nbt.TagParser"),
    NMS_TILEENTITY(PackageWrapper.NMS, "TileEntity", null, null, "net.minecraft.world.level.block.entity", "net.minecraft.world.level.block.entity.BlockEntity"),
    NMS_BLOCKPOSITION(PackageWrapper.NMS, "BlockPosition", MinecraftVersion.MC1_8_R3, null, "net.minecraft.core", "net.minecraft.core.BlockPos"),
    NMS_WORLDSERVER(PackageWrapper.NMS, "WorldServer", null, null, "net.minecraft.server.level", "net.minecraft.server.level.ServerLevel"),
    NMS_MINECRAFTSERVER(PackageWrapper.NMS, "MinecraftServer", null, null, "net.minecraft.server", "net.minecraft.server.MinecraftServer"),
    NMS_WORLD(PackageWrapper.NMS, "World", null, null, "net.minecraft.world.level", "net.minecraft.world.level.Level"),
    NMS_ENTITY(PackageWrapper.NMS, "Entity", null, null, "net.minecraft.world.entity", "net.minecraft.world.entity.Entity"),
    NMS_ENTITY_INSENTIENT(PackageWrapper.NMS, "EntityInsentient", null, null, "net.minecraft.world.entity", "net.minecraft.world.entity.EntityInsentient"),
    NMS_ENTITYTYPES(PackageWrapper.NMS, "EntityTypes", null, null, "net.minecraft.world.entity", "net.minecraft.world.entity.EntityType"),
    NMS_REGISTRYSIMPLE(PackageWrapper.NMS, "RegistrySimple", MinecraftVersion.MC1_11_R1, MinecraftVersion.MC1_12_R1),
    NMS_REGISTRYMATERIALS(PackageWrapper.NMS, "RegistryMaterials", null, null, "net.minecraft.core", "net.minecraft.core.MappedRegistry"),
    NMS_IREGISTRY(PackageWrapper.NMS, "IRegistry", null, null, "net.minecraft.core", "net.minecraft.core.Registry"),
    NMS_MINECRAFTKEY(PackageWrapper.NMS, "MinecraftKey", MinecraftVersion.MC1_8_R3, null, "net.minecraft.resources", "net.minecraft.resources.ResourceKey"),
    NMS_GAMEPROFILESERIALIZER(PackageWrapper.NMS, "GameProfileSerializer", null, null, "net.minecraft.nbt", "net.minecraft.nbt.NbtUtils"),
    NMS_IBLOCKDATA(PackageWrapper.NMS, "IBlockData", MinecraftVersion.MC1_8_R3, null,
            "net.minecraft.world.level.block.state", "net.minecraft.world.level.block.state.BlockState"),
    GAMEPROFILE(PackageWrapper.NONE, "com.mojang.authlib.GameProfile", MinecraftVersion.MC1_8_R3, null),
    ;

    private Class<?> clazz;
    private String mojangName = "";

    ClassWrapper(PackageWrapper packageId, String clazzName, MinecraftVersion from, MinecraftVersion to) {
        this(packageId, clazzName, from, to, null, null);
    }

    ClassWrapper(PackageWrapper packageId, String clazzName, MinecraftVersion from, MinecraftVersion to,
                 String mojangMap, String mojangName) {
        this.mojangName = mojangName;
        if (from != null && MinecraftVersion.getVersion().getVersionId() < from.getVersionId()) {
            return;
        }
        if (to != null && MinecraftVersion.getVersion().getVersionId() > to.getVersionId()) {
            return;
        }
        try {
            if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1) && mojangMap != null) {
                clazz = Class.forName(mojangMap + "." + clazzName);
            } else if (packageId == PackageWrapper.NONE) {
                clazz = Class.forName(clazzName);
            } else {
                String version = MinecraftVersion.getVersion().getPackageName();
                clazz = Class.forName(packageId.getUri() + "." + version + "." + clazzName);
            }
        } catch (Throwable ex) {
        }
    }

    /**
     * @param pre         First part of the package.
     * @param mid The rest of the package path after "net.minecraft" for 1.17+.
     * @param suffix      Rest of the package + class name.
     * @param subClass    Whether the class requested is a sub-class or not.
     */
    ClassWrapper(String pre, String mid, String suffix, MinecraftVersion since, boolean subClass) {
        if (MinecraftVersion.getCurrentVersion().getVersionId() < since.getVersionId())
            return;
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            if (MinecraftVersion.getVersionNumber() < 1_17_0) {
                clazz = Class.forName(pre + (pre.equals("net.minecraft") ? ".server." : ".") + version + ((subClass) ? "$" : ".") + suffix);
            } else {
                String middle = (pre.equals("org.bukkit.craftbukkit")) ? version : mid;
                clazz = Class.forName(pre + "." + middle + ((subClass) ? "$" : ".") + suffix);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param pre         First part of the package.
     * @param mid The rest of the package path after "net.minecraft" for 1.17+.
     * @param suffix      Rest of the package + class name.
     */
    ClassWrapper(String pre, String mid, String suffix, MinecraftVersion since) {
        this(pre, mid, suffix, since, false);
    }

    public Class<?> getClazz() {
        return clazz;
    }


    public String getMojangName() {
        return mojangName;
    }
}
