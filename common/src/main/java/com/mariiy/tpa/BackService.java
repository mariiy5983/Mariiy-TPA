package com.mariiy.tpa;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers the last death / pre-teleport location for {@code /back}.
 * Using {@code /back} swaps with the current position so you can return again.
 */
public final class BackService {

    private final TpaPlatform platform;
    private final Map<UUID, StoredLocation> last = new ConcurrentHashMap<>();

    public BackService(TpaPlatform platform) {
        this.platform = platform;
    }

    public void remember(UUID player, StoredLocation location) {
        if (player != null && location != null && location.worldKey() != null) {
            last.put(player, location);
        }
    }

    /** Capture and store the player's current position. */
    public void rememberCurrent(UUID player) {
        remember(player, platform.captureLocation(player));
    }

    public void clear(UUID player) {
        last.remove(player);
    }

    public void back(UUID player) {
        if (!platform.isOnline(player)) {
            return;
        }
        StoredLocation dest = last.get(player);
        if (dest == null) {
            platform.sendError(player, TpaKeys.BACK_NONE);
            return;
        }
        StoredLocation current = platform.captureLocation(player);
        if (!platform.teleportTo(player, dest)) {
            platform.sendError(player, TpaKeys.BACK_FAILED);
            return;
        }
        if (current != null) {
            last.put(player, current);
        }
        platform.sendSuccess(player, TpaKeys.BACK_SUCCESS);
        platform.playBackSound(player);
    }
}
