package me.blendy.saveminecraft.files;

import me.blendy.saveminecraft.SaveMinecraft;
import me.blendy.saveminecraft.commands.ChangeOrg;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConfigFile {
    static File configFile;
    static FileConfiguration config;
    public static void createConfig () throws IOException {
        SaveMinecraft plugin = SaveMinecraft.getInstance();
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
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
        config.set("changeOrgCooldown", 30);
        config.set("versionCheckTime", 1800);
        List<String> administrators = Arrays.asList("BlendyGamer", "deathlife23", "mayuna_");
        config.set("administrators", administrators);
    }
    public static FileConfiguration getConfig() {
        return config;
    }
    public static File getConfigFile() {return configFile;}

    public static void setConfig (int cooldown, int versionCheckTime, List<String> administrators) throws IOException {
        createConfig();
        config.set("changeOrgCooldown", cooldown);
        config.set("versionCheckTime", versionCheckTime);
        config.set("administrators", administrators);
        config.save(configFile);
    }

    @SuppressWarnings("unchecked")
    public static void loadConfig() throws IOException {
        createConfig();
        try {
            ChangeOrg.changeOrgCooldown = (int) config.get("changeOrgCooldown");
            SaveMinecraft.versionCheckTime = (int) config.get("versionCheckTime");
            SaveMinecraft.pluginAdministrators = (List<String>) config.get("administrators");
        } catch (NullPointerException ex) {
            config = new YamlConfiguration();
            setDefaults();
            config.save(configFile);
            ChangeOrg.changeOrgCooldown = (int) config.get("changeOrgCooldown");
            SaveMinecraft.versionCheckTime = (int) config.get("versionCheckTime");
            SaveMinecraft.pluginAdministrators = (List<String>) config.get("administrators");
        }
    }

}
