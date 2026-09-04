package com.mariiy.tpa.paper;

import com.mariiy.tpa.TpaService;
import com.mariiy.tpa.TpaSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class MariiyTpaPlugin extends JavaPlugin {

    private TpaService service;
    private PaperTpaPlatform platform;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        TpaSettings settings = loadSettings();
        platform = new PaperTpaPlatform(this, settings);
        service = new TpaService(platform, settings);

        TpaCommand cmd = new TpaCommand(service);
        for (String name : new String[]{"tpa", "tpahere", "tpacancel", "tpaccept", "tpdeny"}) {
            Objects.requireNonNull(getCommand(name)).setExecutor(cmd);
            Objects.requireNonNull(getCommand(name)).setTabCompleter(cmd);
        }
        getServer().getPluginManager().registerEvents(new TpaListener(service), this);
        getLogger().info("Mariiy-TPA enabled (Paper).");
    }

    @Override
    public void onDisable() {
        getLogger().info("Mariiy-TPA disabled.");
    }

    public void reloadTpaConfig() {
        reloadConfig();
        TpaSettings s = service.settings();
        s.setEnabled(getConfig().getBoolean("enabled", true));
        s.setTimeoutSeconds(getConfig().getInt("timeout-seconds", 60));
        s.setCooldownSeconds(getConfig().getInt("cooldown-seconds", 15));
        s.setSeparator(getConfig().getString("separator", "-------------------------------------------------"));
        s.setPrefix(getConfig().getString("prefix", ""));
    }

    private TpaSettings loadSettings() {
        TpaSettings s = new TpaSettings();
        s.setEnabled(getConfig().getBoolean("enabled", true));
        s.setTimeoutSeconds(getConfig().getInt("timeout-seconds", 60));
        s.setCooldownSeconds(getConfig().getInt("cooldown-seconds", 15));
        s.setSeparator(getConfig().getString("separator", "-------------------------------------------------"));
        s.setPrefix(getConfig().getString("prefix", ""));
        return s;
    }

    public TpaService service() {
        return service;
    }
}
