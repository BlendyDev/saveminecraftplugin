package me.blendy.saveminecraft.qol;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatColorFormatting implements Listener {
    @EventHandler
    public void onChat (AsyncPlayerChatEvent e) {
        e.setMessage("§r" + e.getMessage().replaceAll("&k", "§k") + "§r");
    }
}
