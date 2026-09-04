package com.mariiy.tpa;

/**
 * Platform bridge used by {@link TpaService}.
 * Implementations live in paper / fabric / forge / neoforge modules.
 */
public interface TpaPlatform {

    /** Player display name, or null if offline. */
    String getName(java.util.UUID uuid);

    boolean isOnline(java.util.UUID uuid);

    boolean hasBypassCooldown(java.util.UUID uuid);

    /**
     * Minecraft client language for this player, e.g. {@code zh_tw}, {@code en_us}.
     * Used so TPA messages follow the player's in-game language setting.
     */
    String getLocale(java.util.UUID uuid);

    /** Schedule a task on the main game thread after {@code delayTicks}. */
    void runLater(Runnable task, long delayTicks);

    void cancelTask(Object taskHandle);

    /** Returns an opaque handle for {@link #cancelTask}. */
    Object scheduleExpire(Runnable task, long delayTicks);

    void sendInfo(java.util.UUID player, String key, Object... args);

    void sendError(java.util.UUID player, String key, Object... args);

    void sendSuccess(java.util.UUID player, String key, Object... args);

    /**
     * Send the clickable invite block to {@code target}.
     * Layout should match:
     * <pre>
     * -------------------------------------------------
     * [TPA] name ...
     *
     *               [Accept]     [Deny]
     * -------------------------------------------------
     * </pre>
     */
    void sendInvite(java.util.UUID target, String fromName, TpaKind kind, int timeoutSeconds);

    /** Teleport immediately: TO = from→to, HERE = to→from. */
    void teleport(java.util.UUID from, java.util.UUID to, TpaKind kind);

    void playRequestSound(java.util.UUID target);

    void playDenySound(java.util.UUID requester);

    /** Resolve a message for this player's locale (optional override prefix handled by impl). */
    default String tr(java.util.UUID player, String key, Object... args) {
        return TpaI18n.translate(getLocale(player), key, args);
    }
}
