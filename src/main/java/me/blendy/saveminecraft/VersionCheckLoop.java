package me.blendy.saveminecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.Objects;

public class VersionCheckLoop extends BukkitRunnable {
    public static int gitTags = 3;
    @Override
    public void run() {
        try {
            checkForNewVersions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reRun(SaveMinecraft plugin, long period) {
        plugin.versionCheckTask.cancel();
        plugin.versionCheckLoop.cancel();
        plugin.versionCheckLoop = new VersionCheckLoop();
        plugin.versionCheckLoop.runTaskTimer(plugin, 0, period);
    }
    public static void checkForNewVersions() throws IOException {
        Document doc = Jsoup.connect("https://github.com/BlendyDev/saveminecraftplugin").ignoreContentType(true).parser(Parser.xmlParser()).get();
        String text = Objects.requireNonNull(doc.getElementsByClass("ml-3 Link--primary no-underline").first()).text();
        int tags = Integer.parseInt(text.split(" ")[0]);
        if (tags > gitTags) {
            String tag = Objects.requireNonNull(doc.getElementsByClass("Link--primary d-flex no-underline").first()).attributes().get("href");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b[SaveMinecraft] &eA new version of the plugin is available! Download it at &nhttps://github.com" + tag));
        }
    }
}
