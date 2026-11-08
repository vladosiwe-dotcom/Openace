package com.vladosiwe.openace;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** /openace awaken &lt;players&gt; — play the scene again (permission level 2). */
@EventBusSubscriber(modid = OpenAce.MODID)
public final class OpenaceCommands {
    private OpenaceCommands() {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(OpenAce.MODID).requires(s -> s.hasPermission(2))
                .then(Commands.literal("awaken").then(Commands.argument("targets", EntityArgument.players()).executes(c -> {
                    var players = EntityArgument.getPlayers(c, "targets");
                    for (ServerPlayer p : players) AwakeningScene.start(p);
                    c.getSource().sendSuccess(() -> Component.translatable("commands.openace.awaken", players.size()), true);
                    return players.size();
                }))));
    }
}
