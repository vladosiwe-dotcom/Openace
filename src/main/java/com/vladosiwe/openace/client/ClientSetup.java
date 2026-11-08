package com.vladosiwe.openace.client;

import com.vladosiwe.openace.OpenAce;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = OpenAce.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    /** Below everything: the launcher's rift light (if any) fades out above the closed eyes. */
    @SubscribeEvent
    public static void layers(RegisterGuiLayersEvent event) {
        event.registerBelowAll(AwakeningClient.LAYER, AwakeningClient::renderLayer);
    }
}
