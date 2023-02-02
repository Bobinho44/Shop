package de.elias177.shop.utils;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCreator {
	public static ItemStack createItem(Material mat, int amount, int subid, String displayname, List<String> lore) {
		ItemStack item = new ItemStack(mat, amount, (short) subid);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayname);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static String repalceTokenNames(Material mat) {
		switch (mat) {
		case IRON_NUGGET:
			return "§7Penny Token";
		case IRON_INGOT:
			return "§6Medallion Token";
		case GOLD_NUGGET:
			return "§9Castle Token";
		case GOLD_INGOT:
			return "§5Kingdom Token";
		}
		return null;
	}
}
