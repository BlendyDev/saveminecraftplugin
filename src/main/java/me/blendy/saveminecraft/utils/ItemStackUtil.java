package me.blendy.saveminecraft.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {
    //public static ItemStack MENU_GLASS = generateItemStack("&r", 1, Material.THIN_GLASS); # RIP menu glass :(
    public static ItemStack generateItemStack(String displayName, int count, Material type) {
        ItemStack item = new ItemStack(type, count);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null && !displayName.equals("")) meta.setDisplayName("§r" + ChatColor.translateAlternateColorCodes('&', displayName));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack generateItemStack(String displayName, int count, Material type, short durability) {
        ItemStack item = generateItemStack(displayName, count, type);
        item.setDurability(durability);
        return item;
    }
    public static ItemStack generateItemStack(String displayName, int count, Material type, String[] lore) {
        ItemStack item = generateItemStack(displayName, count, type);
        ItemMeta itemMeta = item.getItemMeta();
        List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(loreList);
        item.setItemMeta(itemMeta);
        return item;
    }
}
