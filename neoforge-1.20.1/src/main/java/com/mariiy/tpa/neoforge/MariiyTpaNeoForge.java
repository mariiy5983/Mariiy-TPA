package com.mariiy.tpa.neoforge;

import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MariiyTpaNeoForge.MOD_ID)
public final class MariiyTpaNeoForge {

    public static final String MOD_ID = "mariiy_tpa";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static TpaService service;
    private static NeoForgeTpaPlatform platform;

    public MariiyTpaNeoForge(IEventBus modBus) {
        TpaSettings settings = new TpaSettings();
        platform = new NeoForgeTpaPlatform(settings);
        service = new TpaService(platform, settings);

        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::onCommands);
        NeoForge.EVENT_BUS.addListener(this::onLogout);
        LOGGER.info("Mariiy-TPA (NeoForge) loaded.");
    }

    private void onServerStarted(ServerStartedEvent event) {
        platform.setServer(event.getServer());
    }

    private void onServerStopped(ServerStoppedEvent event) {
        platform.setServer(null);
    }

    private void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            service.clearPlayer(sp.getUUID());
        }
    }

    private void onCommands(RegisterCommandsEvent event) {
        var d = event.getDispatcher();
        d.register(Commands.literal("tpa")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayer();
                            if (from == null) return 0;
                            ServerPlayer to = ctx.getSource().getServer()
                                    .getPlayerList().getPlayerByName(StringArgumentType.getString(ctx, "player"));
                            if (to == null) {
                                platform.sendError(from.getUUID(), com.mariiy.tpa.TpaKeys.ERROR_OFFLINE);
                                return 0;
                            }
                            service.request(from.getUUID(), to.getUUID(), TpaKind.TO);
                            return 1;
                        })));
        d.register(Commands.literal("tpahere")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer from = ctx.getSource().getPlayer();
                            if (from == null) return 0;
                            ServerPlayer to = ctx.getSource().getServer()
                                    .getPlayerList().getPlayerByName(StringArgumentType.getString(ctx, "player"));
                            if (to == null) {
                                platform.sendError(from.getUUID(), com.mariiy.tpa.TpaKeys.ERROR_OFFLINE);
                                return 0;
                            }
                            service.request(from.getUUID(), to.getUUID(), TpaKind.HERE);
                            return 1;
                        })));
        d.register(Commands.literal("tpacancel").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return 0;
            service.cancel(p.getUUID());
            return 1;
        }));
        d.register(Commands.literal("tpaccept").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return 0;
            service.accept(p.getUUID());
            return 1;
        }));
        d.register(Commands.literal("tpyes").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return 0;
            service.accept(p.getUUID());
            return 1;
        }));
        d.register(Commands.literal("tpdeny").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return 0;
            service.deny(p.getUUID());
            return 1;
        }));
        d.register(Commands.literal("tpno").executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayer();
            if (p == null) return 0;
            service.deny(p.getUUID());
            return 1;
        }));
    }
}
