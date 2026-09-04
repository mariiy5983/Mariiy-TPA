package com.mariiy.tpa.neoforge;

import com.mariiy.tpa.TpaI18n;
import com.mariiy.tpa.TpaKeys;
import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaPlatform;
import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class NeoForgeTpaPlatform implements TpaPlatform {

    private final TpaSettings settings;
    private volatile MinecraftServer server;
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
    private boolean tickHooked;

    public NeoForgeTpaPlatform(TpaSettings settings) {
        this.settings = settings;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        ensureTick();
    }

    private void ensureTick() {
        if (tickHooked) {
            return;
        }
        tickHooked = true;
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onTick);
    }

    private void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Iterator<Map.Entry<UUID, Task>> it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Task> e = it.next();
            Task t = e.getValue();
            if (t.left-- <= 0) {
                it.remove();
                try {
                    t.runnable.run();
                } catch (Throwable ex) {
                    MariiyTpaNeoForge.LOGGER.error("TPA task failed", ex);
                }
            }
        }
    }

    private ServerPlayer player(UUID uuid) {
        MinecraftServer s = server;
        return s == null ? null : s.getPlayerList().getPlayer(uuid);
    }

    @Override
    public String getName(UUID uuid) {
        ServerPlayer p = player(uuid);
        return p == null ? null : p.getGameProfile().getName();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return player(uuid) != null;
    }

    @Override
    public boolean hasBypassCooldown(UUID uuid) {
        ServerPlayer p = player(uuid);
        return p != null && p.hasPermissions(2);
    }

    @Override
    public String getLocale(UUID uuid) {
        ServerPlayer p = player(uuid);
        if (p == null) {
            return "en_us";
        }
        try {
            return TpaI18n.normalize(p.clientInformation().language());
        } catch (Throwable ignored) {
        }
        try {
            var m = p.getClass().getMethod("getLanguage");
            Object v = m.invoke(p);
            if (v instanceof String s && !s.isBlank()) {
                return TpaI18n.normalize(s);
            }
        } catch (Throwable ignored) {
        }
        return "en_us";
    }

    @Override
    public void runLater(Runnable task, long delayTicks) {
        scheduleExpire(task, delayTicks);
    }

    @Override
    public Object scheduleExpire(Runnable task, long delayTicks) {
        ensureTick();
        UUID id = UUID.randomUUID();
        tasks.put(id, new Task(Math.max(0, delayTicks), task));
        return id;
    }

    @Override
    public void cancelTask(Object taskHandle) {
        if (taskHandle instanceof UUID id) {
            tasks.remove(id);
        }
    }

    @Override
    public void sendInfo(UUID player, String key, Object... args) {
        send(player, Component.literal(prefixText(player)).withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal(tr(player, key, args)).withStyle(net.minecraft.ChatFormatting.GRAY)));
    }

    @Override
    public void sendError(UUID player, String key, Object... args) {
        send(player, Component.literal(prefixText(player)).withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal(tr(player, key, args)).withStyle(net.minecraft.ChatFormatting.RED)));
    }

    @Override
    public void sendSuccess(UUID player, String key, Object... args) {
        send(player, Component.literal(prefixText(player)).withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal(tr(player, key, args)).withStyle(net.minecraft.ChatFormatting.GREEN)));
    }

    @Override
    public void sendInvite(UUID target, String fromName, TpaKind kind, int timeoutSeconds) {
        ServerPlayer p = player(target);
        if (p == null) {
            return;
        }
        String sep = settings.separator();
        Component line = Component.literal(sep).withStyle(net.minecraft.ChatFormatting.DARK_GRAY);
        String action = tr(target, TpaKeys.action(kind));
        String acceptText = tr(target, TpaKeys.ACCEPT);
        String denyText = tr(target, TpaKeys.DENY);

        MutableComponent accept = Component.literal(acceptText)
                .withStyle(net.minecraft.ChatFormatting.GREEN, net.minecraft.ChatFormatting.BOLD);
        accept.setStyle(accept.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.literal(tr(target, TpaKeys.ACCEPT_HOVER)).withStyle(net.minecraft.ChatFormatting.GRAY))));

        MutableComponent deny = Component.literal(denyText)
                .withStyle(net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD);
        deny.setStyle(deny.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.literal(tr(target, TpaKeys.DENY_HOVER)).withStyle(net.minecraft.ChatFormatting.GRAY))));

        String gap = "     ";
        String pad = TpaService.centerPad(sep, acceptText + gap + denyText);

        p.sendSystemMessage(line);
        p.sendSystemMessage(Component.literal(prefixText(target)).withStyle(net.minecraft.ChatFormatting.AQUA)
                .append(Component.literal(tr(target, TpaKeys.INVITE_BODY, fromName, action, timeoutSeconds))
                        .withStyle(net.minecraft.ChatFormatting.YELLOW)));
        p.sendSystemMessage(Component.empty());
        p.sendSystemMessage(Component.literal(pad).append(accept).append(Component.literal(gap)).append(deny));
        p.sendSystemMessage(line);
    }

    @Override
    public void teleport(UUID from, UUID to, TpaKind kind) {
        ServerPlayer requester = player(from);
        ServerPlayer target = player(to);
        if (requester == null || target == null) {
            return;
        }
        if (kind == TpaKind.TO) {
            Vec3 dest = near(target);
            requester.teleportTo(target.serverLevel(), dest.x, dest.y, dest.z, Set.of(),
                    target.getYRot(), target.getXRot());
            sendSuccess(from, TpaKeys.SUCCESS_TELEPORTED_TO, target.getGameProfile().getName());
            sendSuccess(to, TpaKeys.SUCCESS_ARRIVED_HERE, requester.getGameProfile().getName());
            play(requester, SoundEvents.ENDERMAN_TELEPORT);
        } else {
            Vec3 dest = near(requester);
            target.teleportTo(requester.serverLevel(), dest.x, dest.y, dest.z, Set.of(),
                    requester.getYRot(), requester.getXRot());
            sendSuccess(to, TpaKeys.SUCCESS_TELEPORTED_HERE, requester.getGameProfile().getName());
            sendSuccess(from, TpaKeys.SUCCESS_ARRIVED_TO_YOU, target.getGameProfile().getName());
            play(target, SoundEvents.ENDERMAN_TELEPORT);
        }
    }

    @Override
    public void playRequestSound(UUID target) {
        ServerPlayer p = player(target);
        if (p != null) {
            play(p, SoundEvents.NOTE_BLOCK_PLING.get());
        }
    }

    @Override
    public void playDenySound(UUID requester) {
        ServerPlayer p = player(requester);
        if (p != null) {
            play(p, SoundEvents.VILLAGER_NO);
        }
    }

    private String prefixText(UUID player) {
        String custom = settings.prefix();
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        return tr(player, TpaKeys.PREFIX);
    }

    private void send(UUID uuid, Component text) {
        ServerPlayer p = player(uuid);
        if (p != null) {
            p.sendSystemMessage(text);
        }
    }

    private static Vec3 near(ServerPlayer base) {
        Vec3 look = base.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        if (flat.lengthSqr() > 1.0E-6) {
            flat = flat.normalize().scale(-0.8);
        }
        return base.position().add(flat);
    }

    private static void play(ServerPlayer player, SoundEvent sound) {
        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 0.8f, 1.1f);
    }

    private static final class Task {
        long left;
        final Runnable runnable;

        Task(long left, Runnable runnable) {
            this.left = left;
            this.runnable = runnable;
        }
    }
}
