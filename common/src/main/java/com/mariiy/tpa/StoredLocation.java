package com.mariiy.tpa;

/**
 * Cross-platform saved position for {@code /back}.
 */
public final class StoredLocation {
    private final String worldKey;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public StoredLocation(String worldKey, double x, double y, double z, float yaw, float pitch) {
        this.worldKey = worldKey;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String worldKey() {
        return worldKey;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }
}
