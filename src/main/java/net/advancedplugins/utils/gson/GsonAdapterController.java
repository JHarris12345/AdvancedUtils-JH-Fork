/*
 *    Copyright 2023 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.advancedplugins.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import net.advancedplugins.utils.gson.adapter.ItemStackAdapter;
import net.advancedplugins.utils.gson.adapter.LocationAdapter;
import net.advancedplugins.utils.gson.adapter.OfflinePlayerAdapter;
import net.advancedplugins.utils.gson.adapter.WorldAdapter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class GsonAdapterController {
    private static final GsonAdapterController INSTANCE = new GsonAdapterController();
    private final GsonBuilder gsonBuilder;

    private GsonAdapterController() {
        gsonBuilder = new GsonBuilder();
        registerDefaultAdapters();
    }

    private void registerDefaultAdapters() {
        registerAdapter(ItemStack.class, new ItemStackAdapter());
        registerAdapter(Location.class, new LocationAdapter());
        registerAdapter(OfflinePlayer.class, new OfflinePlayerAdapter());
        registerAdapter(World.class, new WorldAdapter());
    }

    public GsonAdapterController registerAdapter(Type clazz, TypeAdapter<?> adapter) {
        gsonBuilder.registerTypeAdapter(clazz, adapter);
        return INSTANCE;
    }

    public Gson getGson() {
        return gsonBuilder.create();
    }

    public static GsonAdapterController getInstance() {
        return INSTANCE;
    }
}
