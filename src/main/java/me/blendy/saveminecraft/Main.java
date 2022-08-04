package me.blendy.saveminecraft;

import me.blendy.saveminecraft.files.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;


public class Main extends JavaPlugin {
    private long changeOrgLastTimestamp = -1;
    public static int changeOrgCooldown = 30;
    public static int versionCheckTime = 7200;
    public static Main plugin;
    public static Main getInstance() {
        return plugin;
    }
    VersionCheckLoop versionCheckLoop;
    BukkitTask versionCheckTask;

    @Override
    public void onEnable() {

        plugin = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    enableConfigs();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                versionCheckLoop = new VersionCheckLoop();
                versionCheckTask = versionCheckLoop.runTaskTimerAsynchronously(plugin, 0, versionCheckTime*20);
            }
        }.runTaskLater(this, 1);
    }
    public void onDisable() {
        try {
            disableConfigs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enableConfigs() throws IOException {
        ConfigFile.loadConfig();
    }
    public void disableConfigs() throws IOException {
        ConfigFile.setConfig(changeOrgCooldown, versionCheckTime);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "changeorg" -> {
                long currentTimeMillis = System.currentTimeMillis();
                if (changeOrgLastTimestamp == -1 || currentTimeMillis - changeOrgLastTimestamp > changeOrgCooldown*1000) {
                    String url = "https://www.change.org/p/minecraft-s-1-19-s-new-chat-moderation-is-dangerous-broken-saveminecraft";
                    changeOrgLastTimestamp = currentTimeMillis;
                    try {
                        int signatures = getSignatures(url);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "A total of &l" + signatures + "&r people have signed!\nGo sign at &b&n" + url + "&r!"));

                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIOException"));
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command is on cooldown! Try again in &b" + (int) (changeOrgCooldown - Math.floor((currentTimeMillis-changeOrgLastTimestamp)/1000)) + "&c seconds!"));
                }

            }
            case "savemc" -> {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    try {
                        enableConfigs();
                        changeOrgLastTimestamp = -1;
                        VersionCheckLoop.reRun(plugin, versionCheckTime* 20L);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aConfig reloaded!"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            default -> {
            }
        }
        return true;
    }



    int getSignatures (String url) throws IOException {
        Document doc = Jsoup.connect(url).ignoreContentType(true).parser(Parser.xmlParser()).get();
        String text = doc.select("script").first().text().substring(20);
        JSONObject object = new JSONObject(text);
        return (int) (((JSONObject) ((JSONObject) object.get("petition")).get("signatureCount")).get("total"));
    }
}
