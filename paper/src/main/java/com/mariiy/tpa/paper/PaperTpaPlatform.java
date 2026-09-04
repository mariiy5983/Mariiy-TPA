package com.mariiy.tpa.paper;

import com.mariiy.tpa.TpaI18n;
import com.mariiy.tpa.TpaKeys;
import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaPlatform;
import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Locale;
import java.util.UUID;

public final class PaperTpaPlatform implements TpaPlatform {

    private final MariiyTpaPlugin plugin;
    private final TpaSettings settings;

    public PaperTpaPlatform(MariiyTpaPlugin plugin, TpaSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    @Override
    public String getName(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return p == null ? null : p.getName();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return p != null && p.isOnline();
    }

    @Override
    public boolean hasBypassCooldown(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return p != null && p.hasPermission("mariiytpa.bypass");
    }

    @Override
    public String getLocale(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return "en_us";
        }
        try {
            Locale loc = p.locale();
            if (loc != null) {
                String language = loc.getLanguage();
                String country = loc.getCountry();
                if (language != null && !language.isBlank()) {
                    if (country != null && !country.isBlank()) {
                        return TpaI18n.normalize(language + "_" + country);
                    }
                    return TpaI18n.normalize(language);
                }
            }
        } catch (NoSuchMethodError ignored) {
        }
        try {
            String legacy = p.getLocale();
            if (legacy != null && !legacy.isBlank()) {
                return TpaI18n.normalize(legacy);
            }
        } catch (Throwable ignored) {
        }
        return "en_us";
    }

    @Override
    public void runLater(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @Override
    public Object scheduleExpire(Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @Override
    public void cancelTask(Object taskHandle) {
        if (taskHandle instanceof BukkitTask t) {
            t.cancel();
        }
    }

    @Override
    public void sendInfo(UUID player, String key, Object... args) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.sendMessage(prefix(player).append(Component.text(tr(player, key, args), NamedTextColor.GRAY)));
        }
    }

    @Override
    public void sendError(UUID player, String key, Object... args) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.sendMessage(prefix(player).append(Component.text(tr(player, key, args), NamedTextColor.RED)));
        }
    }

    @Override
    public void sendSuccess(UUID player, String key, Object... args) {
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.sendMessage(prefix(player).append(Component.text(tr(player, key, args), NamedTextColor.GREEN)));
        }
    }

    @Override
    public void sendInvite(UUID target, String fromName, TpaKind kind, int timeoutSeconds) {
        Player p = Bukkit.getPlayer(target);
        if (p == null) {
            return;
        }
        String sep = settings.separator();
        Component line = Component.text(sep, NamedTextColor.DARK_GRAY);
        String action = tr(target, TpaKeys.action(kind));
        String acceptText = tr(target, TpaKeys.ACCEPT);
        String denyText = tr(target, TpaKeys.DENY);

        Component accept = Component.text(acceptText, NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpaccept"))
                .hoverEvent(HoverEvent.showText(Component.text(tr(target, TpaKeys.ACCEPT_HOVER), NamedTextColor.GRAY)));
        Component deny = Component.text(denyText, NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpdeny"))
                .hoverEvent(HoverEvent.showText(Component.text(tr(target, TpaKeys.DENY_HOVER), NamedTextColor.GRAY)));

        String gap = "     ";
        String pad = TpaService.centerPad(sep, acceptText + gap + denyText);

        p.sendMessage(line);
        p.sendMessage(prefix(target).append(Component.text(
                tr(target, TpaKeys.INVITE_BODY, fromName, action, timeoutSeconds),
                NamedTextColor.YELLOW)));
        p.sendMessage(Component.empty());
        p.sendMessage(Component.text(pad).append(accept).append(Component.text(gap)).append(deny));
        p.sendMessage(line);
    }

    @Override
    public void teleport(UUID from, UUID to, TpaKind kind) {
        Player requester = Bukkit.getPlayer(from);
        Player target = Bukkit.getPlayer(to);
        if (requester == null || target == null) {
            return;
        }
        if (kind == TpaKind.TO) {
            requester.teleport(safeNear(target.getLocation()));
            sendSuccess(from, TpaKeys.SUCCESS_TELEPORTED_TO, target.getName());
            sendSuccess(to, TpaKeys.SUCCESS_ARRIVED_HERE, requester.getName());
            play(requester, Sound.ENTITY_ENDERMAN_TELEPORT);
        } else {
            target.teleport(safeNear(requester.getLocation()));
            sendSuccess(to, TpaKeys.SUCCESS_TELEPORTED_HERE, requester.getName());
            sendSuccess(from, TpaKeys.SUCCESS_ARRIVED_TO_YOU, target.getName());
            play(target, Sound.ENTITY_ENDERMAN_TELEPORT);
        }
    }

    @Override
    public void playRequestSound(UUID target) {
        Player p = Bukkit.getPlayer(target);
        if (p != null) {
            play(p, Sound.BLOCK_NOTE_BLOCK_PLING);
        }
    }

    @Override
    public void playDenySound(UUID requester) {
        Player p = Bukkit.getPlayer(requester);
        if (p != null) {
            play(p, Sound.ENTITY_VILLAGER_NO);
        }
    }

    private Component prefix(UUID player) {
        String custom = settings.prefix();
        String text = (custom != null && !custom.isBlank())
                ? custom
                : tr(player, TpaKeys.PREFIX);
        return Component.text(text, TextColor.color(0x55FFFF));
    }

    private static Location safeNear(Location base) {
        Location loc = base.clone();
        var dir = base.getDirection().setY(0);
        if (dir.lengthSquared() > 1.0E-6) {
            loc.add(dir.normalize().multiply(-0.8));
        }
        loc.setY(base.getY());
        return loc;
    }

    private static void play(Player player, Sound sound) {
        try {
            player.playSound(player.getLocation(), sound, 0.8f, 1.1f);
        } catch (Exception ignored) {
        }
    }
}
