package com.mariiy.tpa;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Platform-agnostic TPA logic. Instant accept → immediate teleport (no warmup / move-cancel).
 * All player-facing strings go through {@link TpaKeys} / {@link TpaI18n} per player locale.
 */
public final class TpaService {

    private final TpaPlatform platform;
    private final TpaSettings settings;

    private final Map<UUID, TpaRequest> byTarget = new ConcurrentHashMap<>();
    private final Map<UUID, TpaRequest> byFrom = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cooldownUntil = new ConcurrentHashMap<>();
    private final Map<UUID, Object> expireTasks = new ConcurrentHashMap<>();

    public TpaService(TpaPlatform platform, TpaSettings settings) {
        this.platform = platform;
        this.settings = settings;
    }

    public TpaSettings settings() {
        return settings;
    }

    public TpaPlatform platform() {
        return platform;
    }

    public void clearPlayer(UUID uuid) {
        TpaRequest asFrom = byFrom.remove(uuid);
        if (asFrom != null) {
            byTarget.remove(asFrom.to(), asFrom);
            cancelExpire(asFrom.id());
            if (platform.isOnline(asFrom.to())) {
                platform.sendInfo(asFrom.to(), TpaKeys.INFO_PEER_OFFLINE);
            }
        }
        TpaRequest asTo = byTarget.remove(uuid);
        if (asTo != null) {
            byFrom.remove(asTo.from(), asTo);
            cancelExpire(asTo.id());
            if (platform.isOnline(asTo.from())) {
                platform.sendInfo(asTo.from(), TpaKeys.INFO_PEER_OFFLINE);
            }
        }
        cooldownUntil.remove(uuid);
    }

    public boolean request(UUID from, UUID to, TpaKind kind) {
        if (!settings.enabled()) {
            platform.sendError(from, TpaKeys.ERROR_DISABLED);
            return false;
        }
        if (from.equals(to)) {
            platform.sendError(from, TpaKeys.ERROR_SELF);
            return false;
        }
        if (!platform.isOnline(to)) {
            platform.sendError(from, TpaKeys.ERROR_OFFLINE);
            return false;
        }

        long now = System.currentTimeMillis();
        Long until = cooldownUntil.get(from);
        if (until != null && until > now && !platform.hasBypassCooldown(from)) {
            long sec = (until - now + 999) / 1000;
            platform.sendError(from, TpaKeys.ERROR_COOLDOWN, sec);
            return false;
        }
        if (byFrom.containsKey(from)) {
            platform.sendError(from, TpaKeys.ERROR_HAS_OUTGOING);
            return false;
        }
        if (byTarget.containsKey(to)) {
            platform.sendError(from, TpaKeys.ERROR_TARGET_BUSY);
            return false;
        }

        int timeout = settings.timeoutSeconds();
        UUID id = UUID.randomUUID();
        TpaRequest req = new TpaRequest(id, from, to, kind, now + timeout * 1000L);
        byFrom.put(from, req);
        byTarget.put(to, req);

        Object handle = platform.scheduleExpire(() -> expire(id), 20L * timeout);
        expireTasks.put(id, handle);

        String toName = platform.getName(to);
        String fromName = platform.getName(from);
        platform.sendSuccess(from, TpaKeys.SUCCESS_SENT, toName == null ? "?" : toName, timeout);
        platform.playRequestSound(to);
        platform.sendInvite(to, fromName == null ? "?" : fromName, kind, timeout);
        return true;
    }

    public boolean cancel(UUID from) {
        TpaRequest req = byFrom.get(from);
        if (req == null) {
            platform.sendInfo(from, TpaKeys.INFO_NO_REQUEST);
            return false;
        }
        remove(req);
        platform.sendInfo(from, TpaKeys.INFO_CANCELLED);
        if (platform.isOnline(req.to())) {
            String name = platform.getName(from);
            platform.sendInfo(req.to(), TpaKeys.INFO_CANCELLED_BY,
                    name == null ? platform.tr(req.to(), TpaKeys.OTHER) : name);
        }
        return true;
    }

    public void accept(UUID target) {
        TpaRequest req = byTarget.get(target);
        if (req == null) {
            platform.sendInfo(target, TpaKeys.INFO_NO_REQUEST);
            return;
        }
        if (req.isExpired(System.currentTimeMillis())) {
            remove(req);
            platform.sendError(target, TpaKeys.ERROR_EXPIRED);
            return;
        }
        if (!platform.isOnline(req.from())) {
            remove(req);
            platform.sendError(target, TpaKeys.ERROR_REQUESTER_OFFLINE);
            return;
        }
        remove(req);
        applyCooldown(req.from());
        String fromName = platform.getName(req.from());
        platform.sendSuccess(target, TpaKeys.SUCCESS_ACCEPTED,
                fromName == null ? platform.tr(target, TpaKeys.OTHER) : fromName);
        platform.teleport(req.from(), req.to(), req.kind());
    }

    public void deny(UUID target) {
        TpaRequest req = byTarget.get(target);
        if (req == null) {
            platform.sendInfo(target, TpaKeys.INFO_NO_REQUEST);
            return;
        }
        remove(req);
        platform.sendInfo(target, TpaKeys.INFO_DENIED);
        if (platform.isOnline(req.from())) {
            String name = platform.getName(target);
            platform.sendError(req.from(), TpaKeys.ERROR_DENIED_BY,
                    name == null ? platform.tr(req.from(), TpaKeys.OTHER) : name);
            platform.playDenySound(req.from());
        }
    }

    private void expire(UUID requestId) {
        TpaRequest req = findById(requestId);
        if (req == null) {
            return;
        }
        remove(req);
        if (platform.isOnline(req.from())) {
            platform.sendInfo(req.from(), TpaKeys.INFO_EXPIRED);
        }
        if (platform.isOnline(req.to())) {
            platform.sendInfo(req.to(), TpaKeys.INFO_EXPIRED);
        }
    }

    private void applyCooldown(UUID from) {
        int cd = settings.cooldownSeconds();
        if (cd > 0 && !platform.hasBypassCooldown(from)) {
            cooldownUntil.put(from, System.currentTimeMillis() + cd * 1000L);
        }
    }

    private TpaRequest findById(UUID id) {
        for (TpaRequest r : byFrom.values()) {
            if (r.id().equals(id)) {
                return r;
            }
        }
        return null;
    }

    private void remove(TpaRequest req) {
        byFrom.remove(req.from(), req);
        byTarget.remove(req.to(), req);
        cancelExpire(req.id());
    }

    private void cancelExpire(UUID id) {
        Object handle = expireTasks.remove(id);
        if (handle != null) {
            platform.cancelTask(handle);
        }
    }

    /** Approximate centered padding under the separator. */
    public static String centerPad(String separator, String content) {
        int contentWidth = displayWidth(content);
        int sepWidth = separator.length();
        int left = Math.max(0, (sepWidth - contentWidth) / 2);
        return " ".repeat(left);
    }

    public static int displayWidth(String s) {
        int w = 0;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            w += (cp > 0x7F) ? 2 : 1;
        }
        return w;
    }
}
