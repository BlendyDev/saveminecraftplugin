package me.blendy.saveminecraft.gui;

import me.blendy.saveminecraft.SaveMinecraft;
import me.blendy.saveminecraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AdminGUI implements Listener, InventoryGUI {
    public Inventory GUI = null;

    @Override
    public void showGUI(Player p) {
        if (SaveMinecraft.pluginAdministrators.size() <= 27) {
            initializeGUI();
            p.openInventory(GUI);
        } else {
            p.sendMessage(ChatColor.RED + "This GUI can't handle over 27 plugin administrators!");
        }
    }

    @EventHandler
    public void onInventoryClick (InventoryClickEvent e) {
        if (!e.getView().getTopInventory().equals(e.getInventory())) return;
        Inventory inventory = e.getInventory();
        if (!inventory.getTitle().equals("§lPlugin administrators")) return;
        handleClick(e.getRawSlot(), (Player) e.getWhoClicked(), e.getInventory(), e.getClick());
        e.setCancelled(true);
    }

    private void handleClick (int slot, Player player, Inventory inventory, ClickType click) {
        switch (slot) {
            case 27:
                new ConfigGUI().showGUI(player);
                break;
            case 34:
                new SignGUI(this, "handleNewAdministrator").createGUI(player, new String[] {"", "", ">> ADD ADMIN <<", ""});
                break;
            case 35:
                player.closeInventory();
                break;
            default:
                ItemStack item = inventory.getItem(slot);
                if (item == null) return;
                if (!item.getType().equals(Material.SKULL)) break;
                if (!click.isRightClick()) break;
                String name = item.getItemMeta().getDisplayName().substring(4);
                if (name.equals(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You can't remove yourself as a plugin administrator!");
                    break;
                }
                SaveMinecraft.pluginAdministrators.remove(name);

                player.sendMessage(String.format("%s%s %swas removed as a plugin administrator!", ChatColor.AQUA, name, ChatColor.RED));
                player.closeInventory();
                break;
        }
    }

    @Override
    public void initializeGUI() {
        GUI = Bukkit.createInventory(null, 36, "§lPlugin administrators");
        for (int i = 0; i < SaveMinecraft.pluginAdministrators.size(); i++) {
            GUI.setItem(i, ItemStackUtil.generateItemStack(ChatColor.DARK_AQUA + SaveMinecraft.pluginAdministrators.get(i), 1, Material.SKULL, new String[]{ChatColor.GRAY + "Right click to remove!"}));
        }
        GUI.setItem(27, ItemStackUtil.generateItemStack("&aGo back", 1, Material.ARROW));
        GUI.setItem(34, ItemStackUtil.generateItemStack("&3Add administrator (by IGN)", 2, Material.DIAMOND));
        GUI.setItem(35, ItemStackUtil.generateItemStack("&cClose", 1, Material.WOOL, (short) 14));
    }

    @SignGUI.SignGUIResponse
    public void handleNewAdministrator(Player p, String[] lines) {
        String name = lines[0] + lines[1];
        if (name.isEmpty()) return;
        if (!name.matches("^[a-zA-Z0-9_]{3,16}$")) {
            p.sendMessage(ChatColor.RED + "That isn't a valid Minecraft username!");
            return;
        }
        for (String admin : SaveMinecraft.pluginAdministrators) {
            if (name.equalsIgnoreCase(admin)) {
                p.sendMessage(ChatColor.RED + "That player is already a plugin administrator");
                return;
            }
        }
        SaveMinecraft.pluginAdministrators.add(name);
        p.sendMessage(String.format("%s%s %swas added as a plugin administrator!", ChatColor.AQUA, name, ChatColor.GREEN));
    }
}
