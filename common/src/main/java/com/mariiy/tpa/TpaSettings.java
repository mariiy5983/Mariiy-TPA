package com.mariiy.tpa;

/**
 * Tunables shared by every platform adapter.
 */
public final class TpaSettings {
    private boolean enabled = true;
    private int timeoutSeconds = 60;
    private int cooldownSeconds = 15;
    private String separator = "-------------------------------------------------";
    /** Empty = use per-player language file prefix. */
    private String prefix = "";

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int timeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = Math.max(5, timeoutSeconds);
    }

    public int cooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(int cooldownSeconds) {
        this.cooldownSeconds = Math.max(0, cooldownSeconds);
    }

    public String separator() {
        return separator;
    }

    public void setSeparator(String separator) {
        if (separator != null && !separator.isBlank()) {
            this.separator = separator;
        }
    }

    public String prefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        }
    }
}
