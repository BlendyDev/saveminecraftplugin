package me.blendy.saveminecraft;

import me.blendy.saveminecraft.commands.ChangeOrg;
import me.blendy.saveminecraft.files.ConfigFile;
import me.blendy.saveminecraft.gui.AdminGUI;
import me.blendy.saveminecraft.gui.ConfigGUI;
import me.blendy.saveminecraft.packet.PacketInterceptor;
import me.blendy.saveminecraft.qol.ChatColorFormatting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SaveMinecraft extends JavaPlugin {

    public static int versionCheckTime = 1800;
    public static SaveMinecraft plugin;
    public static SaveMinecraft getInstance() {
        return plugin;
    }
    public static List<String> pluginAdministrators = new ArrayList<>();
    public static PacketInterceptor packetInterceptor;
    VersionCheckLoop versionCheckLoop;
    BukkitTask versionCheckTask;

    @Override
    public void onEnable() {
        plugin = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            //Force players to get injected
            player.kickPlayer("Plugin has been reloaded without a restart. Rejoin!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    enableConfigs();
                    init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                versionCheckLoop = new VersionCheckLoop();
                versionCheckTask = versionCheckLoop.runTaskTimerAsynchronously(plugin, 0, versionCheckTime* 20L);
            }
        }.runTaskLater(this, 1);
    }
    public void onDisable() {
        try {
            disableConfigs();
            packetInterceptor.unregisterListeners();
            packetInterceptor.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enableConfigs() throws IOException {
        ConfigFile.loadConfig();
    }
    public void disableConfigs() throws IOException {
        ConfigFile.setConfig(ChangeOrg.changeOrgCooldown, versionCheckTime, pluginAdministrators);
    }
    public void init() {
        packetInterceptor = new PacketInterceptor(this);
        getServer().getPluginCommand("changeorg").setExecutor(new ChangeOrg());
        getServer().getPluginManager().registerEvents(new AdminGUI(), plugin);
        getServer().getPluginManager().registerEvents(new ChatColorFormatting(), plugin);
        getServer().getPluginManager().registerEvents(new ConfigGUI(), plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("savemc")) return true;
        if (!(sender.hasPermission("savemc.*") || pluginAdministrators.contains(sender.getName()))) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Available subcommands: " + ChatColor.GOLD + "reload | config");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                try {
                    enableConfigs();
                    ChangeOrg.changeOrgLastTimestamp = -1;
                    VersionCheckLoop.reRun(plugin, versionCheckTime* 20L);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aConfig reloaded!"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "config":
                if (!(sender instanceof Player))  {
                    sender.sendMessage(ChatColor.RED + "This command must not be used by the console");
                    return true;
                }
                ConfigGUI configGUI = new ConfigGUI();
                configGUI.showGUI((Player) sender);
                break;
        }
        return true;
    }




}
