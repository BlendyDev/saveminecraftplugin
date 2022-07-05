package me.blendy.saveminecraft.files;

import me.blendy.saveminecraft.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {
    static File configFile;
    static FileConfiguration config;
    public static void createConfig () throws IOException {
        Main plugin = Main.getInstance();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdir();
            configFile.createNewFile();
            config = new YamlConfiguration();
            setDefaults();
            config.save(configFile);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }
    public static void setDefaults() {
        config.set("cooldown", 30);
        config.set("versionCheckTime", 7200);
    }
    public static FileConfiguration getConfig() {
        return config;
    }

    public static void setConfig (int cooldown, int versionCheckTime) throws IOException {
        createConfig();
        config.set("cooldown", cooldown);
        config.set("versionCheckTime", versionCheckTime);
        config.save(configFile);
    }

    public static void loadConfig() throws IOException {
        createConfig();
        try {
            Main.changeOrgCooldown = (int) config.get("cooldown");
            Main.versionCheckTime = (int) config.get("versionCheckTime");
        } catch (NullPointerException ex) {}
    }

}
