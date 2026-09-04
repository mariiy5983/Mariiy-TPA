package com.mariiy.tpa.paper;

import com.mariiy.tpa.TpaService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TpaListener implements Listener {

    private final TpaService service;

    public TpaListener(TpaService service) {
        this.service = service;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.clearPlayer(event.getPlayer().getUniqueId());
    }
}
