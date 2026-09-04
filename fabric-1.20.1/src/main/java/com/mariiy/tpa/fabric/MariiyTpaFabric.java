package com.mariiy.tpa.fabric;

import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MariiyTpaFabric implements ModInitializer {

    public static final String MOD_ID = "mariiy-tpa";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static TpaService service;
    private static FabricTpaPlatform platform;

    @Override
    public void onInitialize() {
        TpaSettings settings = new TpaSettings();
        platform = new FabricTpaPlatform(settings);
        service = new TpaService(platform, settings);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> platform.setServer(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> platform.setServer(null));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                service.clearPlayer(handler.getPlayer().getUuid()));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("tpa")
                    .then(CommandManager.argument("player", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestPlayers(ctx.getSource(), builder))
                            .executes(ctx -> {
                                ServerPlayerEntity from = ctx.getSource().getPlayer();
                                if (from == null) return 0;
                                ServerPlayerEntity to = find(ctx.getSource().getServer(),
                                        StringArgumentType.getString(ctx, "player"));
                                if (to == null) {
                                    platform.sendError(from.getUuid(), com.mariiy.tpa.TpaKeys.ERROR_OFFLINE);
                                    return 0;
                                }
                                service.request(from.getUuid(), to.getUuid(), TpaKind.TO);
                                return 1;
                            })));

            dispatcher.register(CommandManager.literal("tpahere")
                    .then(CommandManager.argument("player", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestPlayers(ctx.getSource(), builder))
                            .executes(ctx -> {
                                ServerPlayerEntity from = ctx.getSource().getPlayer();
                                if (from == null) return 0;
                                ServerPlayerEntity to = find(ctx.getSource().getServer(),
                                        StringArgumentType.getString(ctx, "player"));
                                if (to == null) {
                                    platform.sendError(from.getUuid(), com.mariiy.tpa.TpaKeys.ERROR_OFFLINE);
                                    return 0;
                                }
                                service.request(from.getUuid(), to.getUuid(), TpaKind.HERE);
                                return 1;
                            })));

            dispatcher.register(CommandManager.literal("tpacancel")
                    .executes(ctx -> {
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        if (p == null) return 0;
                        service.cancel(p.getUuid());
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("tpaccept")
                    .executes(ctx -> {
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        if (p == null) return 0;
                        service.accept(p.getUuid());
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("tpyes")
                    .executes(ctx -> {
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        if (p == null) return 0;
                        service.accept(p.getUuid());
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("tpdeny")
                    .executes(ctx -> {
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        if (p == null) return 0;
                        service.deny(p.getUuid());
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("tpno")
                    .executes(ctx -> {
                        ServerPlayerEntity p = ctx.getSource().getPlayer();
                        if (p == null) return 0;
                        service.deny(p.getUuid());
                        return 1;
                    }));
        });

        LOGGER.info("Mariiy-TPA (Fabric) loaded.");
    }

    private static ServerPlayerEntity find(MinecraftServer server, String name) {
        if (server == null) {
            return null;
        }
        return server.getPlayerManager().getPlayer(name);
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestPlayers(
            ServerCommandSource source,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        MinecraftServer server = source.getServer();
        String remaining = builder.getRemainingLowerCase();
        ServerPlayerEntity self = source.getPlayer();
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (self != null && p.getUuid().equals(self.getUuid())) continue;
            if (p.getGameProfile().getName().toLowerCase().startsWith(remaining)) {
                builder.suggest(p.getGameProfile().getName());
            }
        }
        return builder.buildFuture();
    }
}
