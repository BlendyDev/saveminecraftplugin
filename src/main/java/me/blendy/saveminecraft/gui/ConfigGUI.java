package me.blendy.saveminecraft.gui;

import me.blendy.saveminecraft.SaveMinecraft;
import me.blendy.saveminecraft.VersionCheckLoop;
import me.blendy.saveminecraft.commands.ChangeOrg;
import me.blendy.saveminecraft.files.ConfigFile;
import me.blendy.saveminecraft.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class ConfigGUI implements Listener, InventoryGUI {
    public Inventory GUI = null;

    public void showGUI(Player p) {
        initializeGUI();
        p.openInventory(GUI);
    }
    @Override
    public void initializeGUI () {
        GUI = Bukkit.createInventory(null, 36, "§lSaveMC configuration");
        GUI.setItem(0, ItemStackUtil.generateItemStack("/changeorg cooldown (in seconds)", 1, Material.BOOK_AND_QUILL, new String[]{ChatColor.AQUA + "" + ChangeOrg.changeOrgCooldown}));
        GUI.setItem(1, ItemStackUtil.generateItemStack("Version checking cooldown (in seconds)", 1, Material.PUMPKIN_PIE, new String[]{ChatColor.AQUA + "" + SaveMinecraft.versionCheckTime}));
        GUI.setItem(2, ItemStackUtil.generateItemStack("&3Plugin administrators", 1, Material.COMMAND));
        GUI.setItem(32, ItemStackUtil.generateItemStack("&cRESET TO DEFAULTS", 1, Material.TNT));
        GUI.setItem(33, ItemStackUtil.generateItemStack("&8Write to config", 1, Material.FEATHER));
        GUI.setItem(34, ItemStackUtil.generateItemStack("&aReload config", 1, Material.PAPER));
        GUI.setItem(35, ItemStackUtil.generateItemStack("&cClose", 1, Material.WOOL, (short) 14));

    }
    @EventHandler
    public void onInventoryClick (InventoryClickEvent e) {
        if (!e.getView().getTopInventory().equals(e.getInventory())) return;
        Inventory inventory = e.getInventory();
        if (!inventory.getTitle().equals("§lSaveMC configuration")) return;
        try {
            handleClick(e.getRawSlot(), (Player) e.getWhoClicked());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        e.setCancelled(true);
    }

    private void handleClick (int index, Player player) throws IOException {
        switch (index) {
            case 0:
                new SignGUI(this, "handleChangeOrgCooldown").createGUI(player, new String[]{"", ">> COOLDOWN <<", "", ""});
                break;
            case 1:
                new SignGUI(this, "handleVersionLoopCooldown").createGUI(player, new String[]{"", ">> COOLDOWN <<", "", ""});
                break;
            case 2:
                new AdminGUI().showGUI(player);
                break;
            case 32:
                ConfigFile.setDefaults();
                ConfigFile.getConfig().save(ConfigFile.getConfigFile());
                SaveMinecraft.getInstance().enableConfigs();
                ChangeOrg.changeOrgLastTimestamp = -1;
                VersionCheckLoop.reRun(SaveMinecraft.getInstance(), SaveMinecraft.versionCheckTime* 20L);
                player.sendMessage(ChatColor.RED + "Config reset to defaults!");
                player.closeInventory();
                break;
            case 33:
                SaveMinecraft.getInstance().disableConfigs();
                player.sendMessage(ChatColor.GREEN + "Changes written to config file!");
                player.closeInventory();
                break;
            case 34:
                SaveMinecraft.getInstance().enableConfigs();
                ChangeOrg.changeOrgLastTimestamp = -1;
                VersionCheckLoop.reRun(SaveMinecraft.getInstance(), SaveMinecraft.versionCheckTime* 20L);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aConfig reloaded!"));
                player.closeInventory();
                break;
            case 35:
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @SignGUI.SignGUIResponse
    public void handleChangeOrgCooldown(Player p, String[] lines) {
        try {
            ChangeOrg.changeOrgCooldown = Integer.parseInt(lines[0]);
            ChangeOrg.changeOrgLastTimestamp = -1;
            p.sendMessage(ChatColor.GREEN + "/changeorg cooldown set!");
        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "That's not a valid number!");
            new SignGUI(this, "handleChangeOrgCooldown").createGUI(p, new String[]{"", ">> COOLDOWN <<", "", ""});
        }
    }

    @SignGUI.SignGUIResponse
    public void handleVersionLoopCooldown(Player p, String[] lines) {

        try {
            int newCooldown = Integer.parseInt(lines[0]);
            SaveMinecraft.versionCheckTime = newCooldown;
            VersionCheckLoop.reRun(SaveMinecraft.plugin, newCooldown*20L);
            p.sendMessage(ChatColor.GREEN + "Version check loop time set!");
        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "That's not a valid number!");
            new SignGUI(this, "handleChangeOrgCooldown").createGUI(p, new String[]{"", ">> COOLDOWN <<", "", ""});
        }
    }
}

