package me.blendy.saveminecraft.gui;

import me.blendy.saveminecraft.SaveMinecraft;
import me.blendy.saveminecraft.packet.PacketListener;
import net.minecraft.server.v1_6_R3.Packet130UpdateSign;
import net.minecraft.server.v1_6_R3.Packet133OpenTileEntity;
import net.minecraft.server.v1_6_R3.Packet53BlockChange;
import net.minecraft.server.v1_6_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

public class SignGUI implements PacketListener, Listener {
    //Class for creating sign GUI's
    private final Object signGUIResponseHandler;
    private final String methodName;
    private static final Set<Player> players = new LinkedHashSet<>();
    private int x, z;

    @Override
    @SuppressWarnings("RedundantArrayCreation")
    public void onReceivePacket(Player p, Object object) {
        if (!(object instanceof Packet130UpdateSign)) return;
        if (!players.contains(p)) return;
        Packet130UpdateSign packet = (Packet130UpdateSign) object;
        if (packet.x != x || packet.z != z) return;
        Class<?> clazz = signGUIResponseHandler.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaredAnnotation(SignGUIResponse.class) != null && method.getName().equals(methodName)) {
                if (method.getParameterCount() == 2 && method.getParameters()[1].getType().toString().equals("class [Ljava.lang.String;") && method.getParameters()[0].getType().equals(Player.class)) {
                    method.setAccessible(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                method.invoke(signGUIResponseHandler, new Object[]{p, packet.lines});
                            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }.runTask(SaveMinecraft.getInstance());

                    break;
                }
            }
        }
        players.remove(p);


    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SignGUIResponse {
    }

    public SignGUI(Object instance, String methodName) {
        signGUIResponseHandler = instance;
        SaveMinecraft.packetInterceptor.registerListener(this);
        this.methodName = methodName;
    }

    @SuppressWarnings("deprecation")
    public void createGUI(Player p, String[] lines) {

        if (players.contains(p)) return;
        players.add(p);

        PlayerConnection playerConnection = ((CraftPlayer) p).getHandle().playerConnection;


        int x = p.getLocation().getBlockX();
        int y = 0;
        int z = p.getLocation().getBlockZ();

        this.x = x; this.z = z;

        int material = p.getLocation().getWorld().getBlockAt(x, y, z).getTypeId();
        int data = p.getLocation().getWorld().getBlockAt(x, y, z).getData();
        Packet53BlockChange packet53 = new Packet53BlockChange();

        packet53.a = x;
        packet53.b = y;
        packet53.c = z;
        packet53.material = 63;
        packet53.data = 0;
        playerConnection.sendPacket(packet53);

        Packet130UpdateSign packet130 = new Packet130UpdateSign();
        packet130.x = x;
        packet130.y = y;
        packet130.z = z;
        packet130.lines = lines;
        playerConnection.sendPacket(packet130);

        Packet133OpenTileEntity packet133 = new Packet133OpenTileEntity();
        packet133.b = x;
        packet133.c = y;
        packet133.d = z;
        playerConnection.sendPacket(packet133);

        packet53 = new Packet53BlockChange();
        packet53.a = x;
        packet53.b = y;
        packet53.c = z;
        packet53.material = material;
        packet53.data = data;
        playerConnection.sendPacket(packet53);

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
    }
}
