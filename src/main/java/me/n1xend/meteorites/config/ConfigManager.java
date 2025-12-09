package me.n1xend.meteorites.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reload() {
        loadConfig();
    }

    public void saveConfig() {
        if (config == null || configFile == null) return;
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить config.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getRawConfig() {
        return config;
    }

    // --- Рандомні метеорити ---
    public boolean isRandomMeteoritesEnabled() {
        return config.getBoolean("enable-random-meteorites", true);
    }

    public int getInterval() {
        return config.getInt("random-meteorite-interval", 7200);
    }

    public String getTargetWorldName() {
        return config.getString("random-meteorite-world", "world");
    }

    public int getMaxSpawnX() { return config.getInt("random-meteorite-max-spawn-x-coord", 2500); }
    public int getMaxSpawnZ() { return config.getInt("random-meteorite-max-spawn-z-coord", 2500); }
    public int getMinSpawnX() { return config.getInt("random-meteorite-min-spawn-x-coord", -2500); }
    public int getMinSpawnZ() { return config.getInt("random-meteorite-min-spawn-z-coord", -2500); }
    public int getSpawnHeight() { return config.getInt("random-meteorite-spawn-height", 150); }

    // --- Метеорити ---
    public ConfigurationSection getMeteoritesConfig() {
        return config.getConfigurationSection("meteorites");
    }

    // --- Вибухи/шари ---
    public ConfigurationSection getCoreSettings() {
        return config.getConfigurationSection("core-settings");
    }

    public ConfigurationSection getInnerLayerSettings() {
        return config.getConfigurationSection("inner-layer-settings");
    }

    public ConfigurationSection getOuterLayerSettings() {
        return config.getConfigurationSection("outer-layer-settings");
    }

    // --- Частинки ---
    public boolean areParticlesEnabled() {
        return config.getBoolean("enable-meteorite-particles", true);
    }

    public int getParticleInterval() {
        return config.getInt("meteorite-particle-interval", 5);
    }

    public ConfigurationSection getParticleEffects() {
        return config.getConfigurationSection("possible-meteorite-particle-effects");
    }

    // --- Скарби ---
    public boolean isTreasureEnabled() {
        return config.getBoolean("enable-meteorite-treasure", true);
    }

    public String getTreasureType() {
        return config.getString("treasure-barrel-or-chest", "CHEST");
    }

    public ConfigurationSection getTreasureContent() {
        return config.getConfigurationSection("treasure-content");
    }

    // --- Охоронці ---
    public boolean isGuardianEnabled() {
        return config.getBoolean("enable-treasure-guardian", true);
    }

    public ConfigurationSection getPossibleGuardians() {
        return config.getConfigurationSection("possible-guardians");
    }

    // --- Атмосфера ---
    public ConfigurationSection getAtmosphereSettings() {
        return config.getConfigurationSection("atmosphere-effect");
    }

    // --- Ударна хвиля ---
    public ConfigurationSection getShockwaveSettings() {
        return config.getConfigurationSection("impact-shockwave");
    }

    // --- Радар ---
    public ConfigurationSection getRadarSettings() {
        return config.getConfigurationSection("meteorite-radar");
    }
}
