package com.mariiy.tpa.paper;

import com.mariiy.tpa.BackService;
import com.mariiy.tpa.TpaPlatform;
import com.mariiy.tpa.TpaService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TpaListener implements Listener {

    private final TpaService service;
    private final BackService backService;
    private final TpaPlatform platform;

    public TpaListener(TpaService service, BackService backService, TpaPlatform platform) {
        this.service = service;
        this.backService = backService;
        this.platform = platform;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.clearPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        backService.remember(event.getEntity().getUniqueId(),
                platform.captureLocation(event.getEntity().getUniqueId()));
    }
}
