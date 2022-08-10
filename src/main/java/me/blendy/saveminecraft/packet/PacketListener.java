package me.blendy.saveminecraft.packet;

import org.bukkit.entity.Player;

public interface PacketListener {
    void onReceivePacket (Player p, Object packet);
}
