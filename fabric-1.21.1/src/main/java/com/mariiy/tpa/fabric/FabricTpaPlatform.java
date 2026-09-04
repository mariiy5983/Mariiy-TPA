package com.mariiy.tpa.fabric;

import com.mariiy.tpa.TpaI18n;
import com.mariiy.tpa.TpaKeys;
import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaPlatform;
import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.Set;
import java.util.UUID;

public final class FabricTpaPlatform implements TpaPlatform {

    private final TpaSettings settings;
    private volatile MinecraftServer server;

    public FabricTpaPlatform(TpaSettings settings) {
        this.settings = settings;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    private ServerPlayerEntity player(UUID uuid) {
        MinecraftServer s = server;
        return s == null ? null : s.getPlayerManager().getPlayer(uuid);
    }

    @Override
    public String getName(UUID uuid) {
        ServerPlayerEntity p = player(uuid);
        return p == null ? null : p.getGameProfile().getName();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return player(uuid) != null;
    }

    @Override
    public boolean hasBypassCooldown(UUID uuid) {
        ServerPlayerEntity p = player(uuid);
        return p != null && p.hasPermissionLevel(2);
    }

    @Override
    public String getLocale(UUID uuid) {
        ServerPlayerEntity p = player(uuid);
        if (p == null) {
            return "en_us";
        }
        try {
            return TpaI18n.normalize(p.getClientOptions().language());
        } catch (Throwable t) {
            return "en_us";
        }
    }

    @Override
    public void runLater(Runnable task, long delayTicks) {
        final boolean[] cancelled = {false};
        FabricScheduler.schedule(task, delayTicks, () -> cancelled[0]);
    }

    @Override
    public Object scheduleExpire(Runnable task, long delayTicks) {
        return FabricScheduler.schedule(task, delayTicks, () -> false);
    }

    @Override
    public void cancelTask(Object taskHandle) {
        FabricScheduler.cancel(taskHandle);
    }

    @Override
    public void sendInfo(UUID player, String key, Object... args) {
        send(player, Text.literal(prefixText(player)).formatted(Formatting.AQUA)
                .append(Text.literal(tr(player, key, args)).formatted(Formatting.GRAY)));
    }

    @Override
    public void sendError(UUID player, String key, Object... args) {
        send(player, Text.literal(prefixText(player)).formatted(Formatting.AQUA)
                .append(Text.literal(tr(player, key, args)).formatted(Formatting.RED)));
    }

    @Override
    public void sendSuccess(UUID player, String key, Object... args) {
        send(player, Text.literal(prefixText(player)).formatted(Formatting.AQUA)
                .append(Text.literal(tr(player, key, args)).formatted(Formatting.GREEN)));
    }

    @Override
    public void sendInvite(UUID target, String fromName, TpaKind kind, int timeoutSeconds) {
        ServerPlayerEntity p = player(target);
        if (p == null) {
            return;
        }
        String sep = settings.separator();
        Text line = Text.literal(sep).formatted(Formatting.DARK_GRAY);
        String action = tr(target, TpaKeys.action(kind));
        String acceptText = tr(target, TpaKeys.ACCEPT);
        String denyText = tr(target, TpaKeys.DENY);

        MutableText accept = Text.literal(acceptText).formatted(Formatting.GREEN, Formatting.BOLD);
        accept.setStyle(accept.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.literal(tr(target, TpaKeys.ACCEPT_HOVER)).formatted(Formatting.GRAY))));

        MutableText deny = Text.literal(denyText).formatted(Formatting.RED, Formatting.BOLD);
        deny.setStyle(deny.getStyle()
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Text.literal(tr(target, TpaKeys.DENY_HOVER)).formatted(Formatting.GRAY))));

        String gap = "     ";
        String pad = TpaService.centerPad(sep, acceptText + gap + denyText);

        p.sendMessage(line);
        p.sendMessage(Text.literal(prefixText(target)).formatted(Formatting.AQUA)
                .append(Text.literal(tr(target, TpaKeys.INVITE_BODY, fromName, action, timeoutSeconds))
                        .formatted(Formatting.YELLOW)));
        p.sendMessage(Text.empty());
        p.sendMessage(Text.literal(pad).append(accept).append(Text.literal(gap)).append(deny));
        p.sendMessage(line);
    }

    @Override
    public void teleport(UUID from, UUID to, TpaKind kind) {
        ServerPlayerEntity requester = player(from);
        ServerPlayerEntity target = player(to);
        if (requester == null || target == null) {
            return;
        }
        if (kind == TpaKind.TO) {
            Vec3d dest = near(target);
            requester.teleport(target.getServerWorld(), dest.x, dest.y, dest.z,
                    Set.of(), target.getYaw(), target.getPitch());
            sendSuccess(from, TpaKeys.SUCCESS_TELEPORTED_TO, target.getGameProfile().getName());
            sendSuccess(to, TpaKeys.SUCCESS_ARRIVED_HERE, requester.getGameProfile().getName());
            play(requester, SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        } else {
            Vec3d dest = near(requester);
            target.teleport(requester.getServerWorld(), dest.x, dest.y, dest.z,
                    Set.of(), requester.getYaw(), requester.getPitch());
            sendSuccess(to, TpaKeys.SUCCESS_TELEPORTED_HERE, requester.getGameProfile().getName());
            sendSuccess(from, TpaKeys.SUCCESS_ARRIVED_TO_YOU, target.getGameProfile().getName());
            play(target, SoundEvents.ENTITY_ENDERMAN_TELEPORT);
        }
    }

    @Override
    public void playRequestSound(UUID target) {
        ServerPlayerEntity p = player(target);
        if (p != null) {
            play(p, SoundEvents.BLOCK_NOTE_BLOCK_PLING.value());
        }
    }

    @Override
    public void playDenySound(UUID requester) {
        ServerPlayerEntity p = player(requester);
        if (p != null) {
            play(p, SoundEvents.ENTITY_VILLAGER_NO);
        }
    }

    private String prefixText(UUID player) {
        String custom = settings.prefix();
        if (custom != null && !custom.isBlank()) {
            return custom;
        }
        return tr(player, TpaKeys.PREFIX);
    }

    private void send(UUID uuid, Text text) {
        ServerPlayerEntity p = player(uuid);
        if (p != null) {
            p.sendMessage(text);
        }
    }

    private static Vec3d near(ServerPlayerEntity base) {
        Vec3d look = base.getRotationVector();
        Vec3d flat = new Vec3d(look.x, 0, look.z);
        if (flat.lengthSquared() > 1.0E-6) {
            flat = flat.normalize().multiply(-0.8);
        }
        return base.getPos().add(flat);
    }

    private static void play(ServerPlayerEntity player, SoundEvent sound) {
        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundCategory.PLAYERS, 0.8f, 1.1f);
    }
}
