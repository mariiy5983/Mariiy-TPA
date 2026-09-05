package com.mariiy.tpa.paper;

import com.mariiy.tpa.BackService;
import com.mariiy.tpa.TpaKind;
import com.mariiy.tpa.TpaKeys;
import com.mariiy.tpa.TpaService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class TpaCommand implements CommandExecutor, TabCompleter {

    private final TpaService service;
    private final BackService backService;

    public TpaCommand(TpaService service, BackService backService) {
        this.service = service;
        this.backService = backService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(com.mariiy.tpa.TpaI18n.translate("en_us", TpaKeys.CMD_PLAYERS_ONLY));
            return true;
        }
        String name = command.getName().toLowerCase(Locale.ROOT);
        if (name.equals("back")) {
            if (!player.hasPermission("mariiytpa.back") && !player.hasPermission("mariiytpa.use")) {
                service.platform().sendError(player.getUniqueId(), TpaKeys.CMD_NO_PERMISSION);
                return true;
            }
            backService.back(player.getUniqueId());
            return true;
        }
        if (!player.hasPermission("mariiytpa.use")) {
            service.platform().sendError(player.getUniqueId(), TpaKeys.CMD_NO_PERMISSION);
            return true;
        }
        return switch (name) {
            case "tpa" -> request(player, args, TpaKind.TO);
            case "tpahere" -> request(player, args, TpaKind.HERE);
            case "tpacancel" -> {
                service.cancel(player.getUniqueId());
                yield true;
            }
            case "tpaccept", "tpyes" -> {
                service.accept(player.getUniqueId());
                yield true;
            }
            case "tpdeny", "tpno" -> {
                service.deny(player.getUniqueId());
                yield true;
            }
            default -> true;
        };
    }

    private boolean request(Player player, String[] args, TpaKind kind) {
        if (args.length < 1) {
            service.platform().sendError(player.getUniqueId(),
                    kind == TpaKind.TO ? TpaKeys.CMD_USAGE_TPA : TpaKeys.CMD_USAGE_TPAHERE);
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            service.platform().sendError(player.getUniqueId(), TpaKeys.CMD_NOT_ONLINE, args[0]);
            return true;
        }
        service.request(player.getUniqueId(), target.getUniqueId(), kind);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player self) || args.length != 1) {
            return Collections.emptyList();
        }
        String cmd = command.getName().toLowerCase(Locale.ROOT);
        if (!cmd.equals("tpa") && !cmd.equals("tpahere")) {
            return Collections.emptyList();
        }
        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(self.getUniqueId())
                    && p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                out.add(p.getName());
            }
        }
        return out.stream().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
    }
}
