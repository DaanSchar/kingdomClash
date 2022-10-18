package org.daan.kingdomclash.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static final String KEY_CATEGORIES_KINGDOM_CLASH = "key.categories.kingdomclash";
    public static KeyMapping exampleKeyMapping;

    public static void init() {
        exampleKeyMapping = new KeyMapping(
                "key.kingdomclash.example_key",
                KeyConflictContext.IN_GAME,
                InputConstants.getKey("key.keyboard.period"),
                KEY_CATEGORIES_KINGDOM_CLASH
        );
        ClientRegistry.registerKeyBinding(exampleKeyMapping);
        LogUtils.getLogger().info("Registered KeyBindings");
    }

}
