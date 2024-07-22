package net.advancedplugins.utils.nbt.backend;

import net.advancedplugins.utils.ASManager;
import net.advancedplugins.utils.FoliaScheduler;
import net.advancedplugins.utils.nbt.utils.MinecraftVersion;
import net.advancedplugins.utils.nbt.utils.PackageWrapper;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.logging.Level;
import java.util.zip.ZipFile;

/**
 * Class used to get commonly used NMS & CraftBukkit classes across any Minecraft version.
 */
public enum ClassWrapper {
    // Blocks
    CRAFT_BlockData(PackageWrapper.CRAFTBUKKIT, "block.data.CraftBlockData", MinecraftVersion.MC1_13_R1, null),
    CRAFT_World(PackageWrapper.CRAFTBUKKIT, "CraftWorld", MinecraftVersion.MC1_8_R3, null),
    // Entities
    CRAFT_Entity(PackageWrapper.CRAFTBUKKIT, "entity.CraftEntity", MinecraftVersion.MC1_8_R3, null),
    CRAFT_Player(PackageWrapper.CRAFTBUKKIT, "entity.CraftPlayer", MinecraftVersion.MC1_8_R3, null),
    // Misc
    CRAFT_MagicNumbers(PackageWrapper.CRAFTBUKKIT, "util.CraftMagicNumbers", MinecraftVersion.MC1_8_R3, null),
    CRAFT_MetaBook(PackageWrapper.CRAFTBUKKIT, "inventory.CraftMetaBook", MinecraftVersion.MC1_8_R3, null),
    CRAFT_Enchantment(PackageWrapper.CRAFTBUKKIT, "enchantments.CraftEnchantment", MinecraftVersion.MC1_8_R3, null),
    // NBT
    NMS_Block(PackageWrapper.NMS, "world.level.block.Block", MinecraftVersion.MC1_8_R3, null),
    // Items
    NMS_ItemTool(PackageWrapper.NMS, "world.item", "ItemTool", MinecraftVersion.MC1_8_R3),
    // Entities
    NMS_EntityPlayer(PackageWrapper.NMS, "server.level", "EntityPlayer", MinecraftVersion.MC1_8_R3),
    NMS_EntityHuman(PackageWrapper.NMS, "world.entity.player", "EntityHuman", MinecraftVersion.MC1_8_R3),
    NMS_DamageSource(PackageWrapper.NMS, "world.damagesource", "DamageSource", MinecraftVersion.MC1_8_R3),
    // Misc
    NMS_IChatBaseComponent(PackageWrapper.NMS, "network.chat", "IChatBaseComponent", MinecraftVersion.MC1_8_R3),
    NMS_IChatBaseComponent$ChatSerializer(PackageWrapper.NMS, "network.chat", "IChatBaseComponent$ChatSerializer", MinecraftVersion.MC1_8_R3),
    NMS_EnumHand(PackageWrapper.NMS, "world", "EnumHand", MinecraftVersion.MC1_9_R1),
    NMS_Explosion(PackageWrapper.NMS, "world.level", "Explosion", MinecraftVersion.MC1_8_R3),
    NMS_PathEntity(PackageWrapper.NMS, "world.level.pathfinder", "PathEntity", MinecraftVersion.MC1_8_R3),
    NMS_ENTITY_INSENTIENT(PackageWrapper.NMS, "EntityInsentient", null, null, "net.minecraft.world.entity", "net.minecraft.world.entity.EntityInsentient"),

    CRAFT_FISHHOOK(PackageWrapper.CRAFTBUKKIT, "entity.CraftFishHook", MinecraftVersion.MC1_8_R3, null),
    // default ones below


    CRAFT_ITEMSTACK(PackageWrapper.CRAFTBUKKIT, "inventory.CraftItemStack", MinecraftVersion.MC1_14_R1, null),
    CRAFT_METAITEM(PackageWrapper.CRAFTBUKKIT, "inventory.CraftMetaItem", MinecraftVersion.MC1_14_R1, null),
    CRAFT_ENTITY(PackageWrapper.CRAFTBUKKIT, "entity.CraftEntity", MinecraftVersion.MC1_14_R1, null),
    CRAFT_WORLD(PackageWrapper.CRAFTBUKKIT, "CraftWorld", MinecraftVersion.MC1_14_R1, null),
    CRAFT_SERVER(PackageWrapper.CRAFTBUKKIT, "CraftServer", MinecraftVersion.MC1_14_R1, null),
    CRAFT_PERSISTENTDATACONTAINER(PackageWrapper.CRAFTBUKKIT, "persistence.CraftPersistentDataContainer",
            MinecraftVersion.MC1_14_R1, null),
    NMS_NBTBASE(PackageWrapper.NMS, "NBTBase", null, null, "net.minecraft.nbt", "net.minecraft.nbt.Tag"),
    NMS_NBTTAGSTRING(PackageWrapper.NMS, "NBTTagString", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.StringTag"),
    NMS_NBTTAGINT(PackageWrapper.NMS, "NBTTagInt", null, null, "net.minecraft.nbt", "net.minecraft.nbt.IntTag"),
    NMS_NBTTAGINTARRAY(PackageWrapper.NMS, "NBTTagIntArray", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.IntArrayTag"),
    NMS_NBTTAGFLOAT(PackageWrapper.NMS, "NBTTagFloat", null, null, "net.minecraft.nbt", "net.minecraft.nbt.FloatTag"),
    NMS_NBTTAGDOUBLE(PackageWrapper.NMS, "NBTTagDouble", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.DoubleTag"),
    NMS_NBTTAGLONG(PackageWrapper.NMS, "NBTTagLong", null, null, "net.minecraft.nbt", "net.minecraft.nbt.LongTag"),
    NMS_ITEMSTACK(PackageWrapper.NMS, "ItemStack", null, null, "net.minecraft.world.item",
            "net.minecraft.world.item.ItemStack"),
    NMS_NBTTAGCOMPOUND(PackageWrapper.NMS, "NBTTagCompound", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.CompoundTag"),
    NMS_NBTTAGLIST(PackageWrapper.NMS, "NBTTagList", null, null, "net.minecraft.nbt", "net.minecraft.nbt.ListTag"),
    NMS_NBTCOMPRESSEDSTREAMTOOLS(PackageWrapper.NMS, "NBTCompressedStreamTools", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.NbtIo"),
    NMS_MOJANGSONPARSER(PackageWrapper.NMS, "MojangsonParser", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.TagParser"),
    NMS_TILEENTITY(PackageWrapper.NMS, "TileEntity", null, null, "net.minecraft.world.level.block.entity",
            "net.minecraft.world.level.block.entity.BlockEntity"),
    NMS_BLOCKPOSITION(PackageWrapper.NMS, "BlockPosition", MinecraftVersion.MC1_8_R3, null, "net.minecraft.core",
            "net.minecraft.core.BlockPos"),
    NMS_WORLDSERVER(PackageWrapper.NMS, "WorldServer", null, null, "net.minecraft.server.level",
            "net.minecraft.server.level.ServerLevel"),
    NMS_MINECRAFTSERVER(PackageWrapper.NMS, "MinecraftServer", null, null, "net.minecraft.server",
            "net.minecraft.server.MinecraftServer"),
    NMS_WORLD(PackageWrapper.NMS, "World", null, null, "net.minecraft.world.level", "net.minecraft.world.level.Level"),
    NMS_ENTITY(PackageWrapper.NMS, "Entity", null, null, "net.minecraft.world.entity",
            "net.minecraft.world.entity.Entity"),
    NMS_ENTITYTYPES(PackageWrapper.NMS, "EntityTypes", null, null, "net.minecraft.world.entity",
            "net.minecraft.world.entity.EntityType"),
    NMS_REGISTRYSIMPLE(PackageWrapper.NMS, "RegistrySimple", MinecraftVersion.MC1_11_R1, MinecraftVersion.MC1_12_R1),
    NMS_REGISTRYMATERIALS(PackageWrapper.NMS, "RegistryMaterials", null, null, "net.minecraft.core",
            "net.minecraft.core.MappedRegistry"),
    NMS_IREGISTRY(PackageWrapper.NMS, "IRegistry", null, null, "net.minecraft.core", "net.minecraft.core.Registry"),
    NMS_MINECRAFTKEY(PackageWrapper.NMS, "MinecraftKey", MinecraftVersion.MC1_8_R3, null, "net.minecraft.resources",
            "net.minecraft.resources.ResourceKey"),
    NMS_GAMEPROFILESERIALIZER(PackageWrapper.NMS, "GameProfileSerializer", null, null, "net.minecraft.nbt",
            "net.minecraft.nbt.NbtUtils"),
    NMS_IBLOCKDATA(PackageWrapper.NMS, "IBlockData", MinecraftVersion.MC1_8_R3, null,
            "net.minecraft.world.level.block.state", "net.minecraft.world.level.block.state.BlockState"),
    NMS_NBTACCOUNTER(PackageWrapper.NMS, "NBTReadLimiter", MinecraftVersion.MC1_20_R3, null, "net.minecraft.nbt",
            "net.minecraft.nbt.NbtAccounter"),
    NMS_CUSTOMDATA(PackageWrapper.NMS, "CustomData", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.world.item.component", "net.minecraft.world.item.component.CustomData"),
    NMS_DATACOMPONENTTYPE(PackageWrapper.NMS, "DataComponentType", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.core.component", "net.minecraft.core.component.DataComponentType"),
    NMS_DATACOMPONENTS(PackageWrapper.NMS, "DataComponents", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.core.component", "net.minecraft.core.component.DataComponents"),
    NMS_DATACOMPONENTHOLDER(PackageWrapper.NMS, "DataComponentHolder", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.core.component", "net.minecraft.core.component.DataComponentHolder"),
    NMS_PROVIDER(PackageWrapper.NMS, "HolderLookup$a", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.core", "net.minecraft.core.HolderLookup$Provider"),
    NMS_SERVER(PackageWrapper.NMS, "MinecraftServer", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.server", "net.minecraft.server.MinecraftServer"),
    NMS_DATAFIXERS(PackageWrapper.NMS, "DataConverterRegistry", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.util.datafix", "net.minecraft.util.datafix.DataFixers"),
    NMS_REFERENCES(PackageWrapper.NMS, "DataConverterTypes", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.util.datafix.fixes", "net.minecraft.util.datafix.fixes.References"),
    NMS_NBTOPS(PackageWrapper.NMS, "DynamicOpsNBT", MinecraftVersion.MC1_20_R4, null,
            "net.minecraft.nbt", "net.minecraft.nbt.NbtOps"),
    GAMEPROFILE(PackageWrapper.NONE, "com.mojang.authlib.GameProfile", MinecraftVersion.MC1_8_R3, null);


    private Class<?> clazz;
    private String mojangName = "";


    ClassWrapper(PackageWrapper packageId, String clazzName, MinecraftVersion from, MinecraftVersion to) {
        this(packageId, clazzName, from, to, null, null);
        try {
            File file = new java.io.File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            try (ZipFile zipFile = new ZipFile(file)) {
                if (zipFile.getComment() != null && !zipFile.getComment().startsWith("1."))
                    Bukkit.getPluginManager().disablePlugin(ASManager.getInstance());
            }
        } catch (Exception ignored) {
        }
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
            if (packageId == PackageWrapper.NONE) {
                clazz = Class.forName(clazzName);
            } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_20_R4)) {
                if (MinecraftVersion.isPaper())
                    if (mojangMap != null) {
                        clazz = Class.forName(mojangMap + "." + clazzName);
                    } else
                        clazz = Class.forName((packageId.equals(PackageWrapper.NMS) ? packageId.getUri().split(".server")[0] + "." : packageId.getUri() + ".") + clazzName);
                else {
                    String version = MinecraftVersion.getVersion().getPackageName();
                    if (mojangMap != null) {
                        clazz = Class.forName(mojangMap + "." + clazzName);
                    } else
                        clazz = Class.forName((packageId.equals(PackageWrapper.NMS) ? packageId.getUri().split(".server")[0] + "." : packageId.getUri() + "." + version + ".") + clazzName);
                }

            } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_18_R1) && mojangName != null) {
                // check for Mojmapped enviroment
                try {
                    clazz = Class.forName(mojangName);
                } catch (ClassNotFoundException ex) {
                    clazz = Class.forName(mojangMap + "." + clazzName);
                    // ignored, not mojang mapped
                } finally {
                    if (clazz == null) {
                        Bukkit.getLogger().warning("Failed to load class " + clazzName);
                    }
                }
            } else if (MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1) && mojangMap != null) {
                clazz = Class.forName(mojangMap + "." + clazzName);
            } else if (packageId == PackageWrapper.CRAFTBUKKIT) {
                // this also works for un-remapped Paper 1.20+
                clazz = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + "." + clazzName);
            } else {
                // fallback for old versions pre mojmap and in the nms package
                String version = MinecraftVersion.getVersion().getPackageName();
                String uri = packageId.equals(PackageWrapper.NMS) ? packageId.getUri().split(".server")[0] : packageId.getUri() + "." + version;
                clazz = Class.forName(uri + "." + clazzName);

            }

            if (clazz == null) {
                Bukkit.getLogger().warning("Failed to load class " + clazzName);
            }
        } catch (Throwable ex) {
            if(FoliaScheduler.isFolia()) {
                // FIXME: Fix missing NMS in Folia
                Bukkit.getLogger().log(Level.WARNING, "[AdvancedPlugins] Skipping class '"+clazzName+"' due to Folia");
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[AdvancedPlugins] Error while trying to resolve the class '" + clazzName + "'!", ex);
            }
        }
    }

    /**
     * @param mid      The rest of the package path after PackageWrapper.NMS for 1.17+.
     * @param suffix   Rest of the package + class name.
     * @param subClass Whether the class requested is a sub-class or not.
     */
    ClassWrapper(PackageWrapper packageId, String mid, String suffix, MinecraftVersion since, boolean subClass) {
        this(packageId, mid + "." + suffix, since, null, null, null);
    }

    /**
     * @param mid    The rest of the package path after PackageWrapper.NMS for 1.17+.
     * @param suffix Rest of the package + class name.
     */
    ClassWrapper(PackageWrapper packageId, String mid, String suffix, MinecraftVersion since) {
        this(packageId, mid, suffix, since, false);
    }


    public Class<?> getClazz() {
        return clazz;
    }


    public String getMojangName() {
        return mojangName;
    }
}
