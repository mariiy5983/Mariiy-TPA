package com.mariiy.tpa;

import java.util.UUID;

/**
 * Immutable pending teleport request.
 */
public final class TpaRequest {
    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final TpaKind kind;
    private final long expiresAtMs;

    public TpaRequest(UUID id, UUID from, UUID to, TpaKind kind, long expiresAtMs) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.kind = kind;
        this.expiresAtMs = expiresAtMs;
    }

    public UUID id() {
        return id;
    }

    public UUID from() {
        return from;
    }

    public UUID to() {
        return to;
    }

    public TpaKind kind() {
        return kind;
    }

    public long expiresAtMs() {
        return expiresAtMs;
    }

    public boolean isExpired(long nowMs) {
        return nowMs > expiresAtMs;
    }
}
