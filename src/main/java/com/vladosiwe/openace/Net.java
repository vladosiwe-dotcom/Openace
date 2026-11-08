package com.vladosiwe.openace;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Three small packets. Client handlers are resolved only when called, so a dedicated server never loads client
 * classes.
 */
@EventBusSubscriber(modid = OpenAce.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Net {
    private Net() {}

    /** A player's scene: who, and how many ticks of it have already passed (for players who see it late). */
    public record Awakening(int entityId, int elapsed) implements CustomPacketPayload {
        public static final Type<Awakening> TYPE = new Type<>(OpenAce.id("awakening"));
        public static final StreamCodec<ByteBuf, Awakening> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Awakening::entityId, ByteBufCodecs.VAR_INT, Awakening::elapsed, Awakening::new);
        @Override public Type<Awakening> type() { return TYPE; }
    }

    /** Sent at login when a scene will play: the client keeps the eyes closed from the moment the world shows. */
    public record Expect() implements CustomPacketPayload {
        public static final Type<Expect> TYPE = new Type<>(OpenAce.id("expect"));
        public static final StreamCodec<ByteBuf, Expect> CODEC = StreamCodec.unit(new Expect());
        @Override public Type<Expect> type() { return TYPE; }
    }

    /** The client has loaded the world (no loading screen): the server may start the scene. */
    public record ClientReady() implements CustomPacketPayload {
        public static final Type<ClientReady> TYPE = new Type<>(OpenAce.id("client_ready"));
        public static final StreamCodec<ByteBuf, ClientReady> CODEC = StreamCodec.unit(new ClientReady());
        @Override public Type<ClientReady> type() { return TYPE; }
    }

    /** Skips fake players and connections without this mod (vanilla clients), which cannot take the packet. */
    public static void toPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (player instanceof FakePlayer || player.connection == null || !player.connection.hasChannel(payload)) return;
        PacketDistributor.sendToPlayer(player, payload);
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1").optional();
        r.playToClient(Awakening.TYPE, Awakening.CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> com.vladosiwe.openace.client.AwakeningClient.onPacket(p)));
        r.playToClient(Expect.TYPE, Expect.CODEC,
                (p, ctx) -> ctx.enqueueWork(com.vladosiwe.openace.client.AwakeningClient::onExpect));
        r.playToServer(ClientReady.TYPE, ClientReady.CODEC,
                (p, ctx) -> ctx.enqueueWork(() -> { if (ctx.player() instanceof ServerPlayer sp) AwakeningScene.onClientReady(sp); }));
    }
}
