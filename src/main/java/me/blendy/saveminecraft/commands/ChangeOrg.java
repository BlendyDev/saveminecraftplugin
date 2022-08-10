package me.blendy.saveminecraft.commands;

import me.blendy.saveminecraft.SaveMinecraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.Objects;

public class ChangeOrg implements CommandExecutor {
    public static long changeOrgLastTimestamp = -1;
    public static int changeOrgCooldown = 30;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        long currentTimeMillis = System.currentTimeMillis();
        if (changeOrgLastTimestamp == -1 || currentTimeMillis - changeOrgLastTimestamp > changeOrgCooldown* 1000L) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    String url = "https://www.change.org/p/minecraft-s-1-19-s-new-chat-moderation-is-dangerous-broken-saveminecraft";
                    changeOrgLastTimestamp = currentTimeMillis;
                    try {
                        int signatures = getSignatures(url);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "A total of &l" + signatures + "&r people have signed!\nGo sign at &b&n" + url + "&r!"));

                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIOException"));
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(SaveMinecraft.getInstance());

        } else {
            int cooldownTime = (int) (changeOrgCooldown - Math.floor((currentTimeMillis-changeOrgLastTimestamp)/1000f));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&cThis command is on cooldown! Try again in &b%s&c second%s!", cooldownTime, cooldownTime == 1 ? "" : "s")));
        }
        return true;
    }

    int getSignatures (String url) throws IOException {
        Document doc = Jsoup.connect(url).ignoreContentType(true).parser(Parser.xmlParser()).get();
        String text = Objects.requireNonNull(doc.select("script").first()).text().substring(20);
        JSONObject object = new JSONObject(text);
        return (int) (((JSONObject) ((JSONObject) object.get("petition")).get("signatureCount")).get("total"));
    }
}
