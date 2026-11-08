package com.vladosiwe.openace;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

/**
 * Openace — the awakening scene on entering a server. A standalone mod: it does not depend on Solo Status or any
 * other mod; GeckoLib is optional (with it the scene model wears the player's skin, without it the vanilla model
 * plays a simplified scene).
 */
@Mod(OpenAce.MODID)
public final class OpenAce {
    public static final String MODID = "openace";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    /** The player has seen the awakening at least once (for everyJoin = false). Kept on death. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SEEN = ATTACHMENTS.register("seen",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());

    public OpenAce(IEventBus modBus, ModContainer container) {
        ATTACHMENTS.register(modBus);
        container.registerConfig(ModConfig.Type.SERVER, OpenaceConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
