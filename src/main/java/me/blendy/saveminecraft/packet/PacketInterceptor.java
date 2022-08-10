package me.blendy.saveminecraft.packet;

import com.google.common.collect.ForwardingQueue;
import net.minecraft.server.v1_6_R3.INetworkManager;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

public class PacketInterceptor implements Listener {
    public Map<Player, ProxyQueue> proxyMap = new WeakHashMap<>();
    public List<PacketListener> listeners = new ArrayList<>();

    private class ProxyQueue extends ForwardingQueue<Object> {

        private final Player player;
        private final Queue<Object> original;


        public ProxyQueue(Player player, Queue<Object> original) {
            this.player = player;
            this.original = original;
        }
        @Override
        protected Queue<Object> delegate() {
            return original;
        }

        @Override
        public boolean add(@Nonnull Object a) {
            return super.add(interceptPacket(player, a));
        }

    }

    private final Plugin plugin;
    public void clear() {
        proxyMap.clear();
    }

    public PacketInterceptor(Plugin plugin) {
        // Register this as a listener
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void registerListener(PacketListener listener) {
        listeners.add(listener);
    }

    public void unregisterListeners() {
        listeners.clear();
    }

    private Object interceptPacket(Player player, Object packet) {
        for (PacketListener listener : listeners) {
            listener.onReceivePacket(player, packet);
        }
        return packet;
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void onPlayerLogin(PlayerLoginEvent e) {
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                INetworkManager networkManager = ((CraftPlayer) player).getHandle().playerConnection.networkManager;
                try {
                    ProxyQueue proxyQueue = new ProxyQueue(player, (Queue<Object>) getField(networkManager, "inboundQueue").get(networkManager));
                    proxyMap.put(player, proxyQueue);
                    getField(networkManager, "inboundQueue").set(networkManager, proxyQueue);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }.runTaskLaterAsynchronously(plugin, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        proxyMap.remove(e.getPlayer());
    }

    @SuppressWarnings("SameParameterValue")
    private static Field getField(Object instance, String fieldName) {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalStateException("Unable to find field " + fieldName + "!");
    }

}