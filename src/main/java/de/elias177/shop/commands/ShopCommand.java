package de.elias177.shop.commands;

import de.elias177.shop.inventorys.ShopEditInventory;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.manager.ShopManager;
import de.elias177.shop.utils.ItemCreator;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cYou are not a player!");
			return false;
		}

		Player p = (Player) sender;

		if (args.length != 2) {
			p.sendMessage("§cUseshop <create, delete> <Name>");
			return false;
		}

		if (args[0].equalsIgnoreCase("create")) {

			String name = args[1];

			NPCShop shop = new NPCShop(name);

			if (shop.exists()) {
				p.sendMessage("§cThis shop already exists!");
				return false;
			}

			shop.create();
			ShopManager.loadShops();
			p.sendMessage("§aSuccessfully created §6Shop " + name + "§a!");
		} else if (args[0].equalsIgnoreCase("setnpc")) {

			String name = args[1];

			NPCShop shop = new NPCShop(name);

			if (!shop.exists()) {
				p.sendMessage("§cThis shop does not exists!");
				return false;
			}

			List<String> lore = new ArrayList<>();
			lore.add(name);
			p.getInventory().setItemInMainHand(ItemCreator.createItem(Material.STICK, 1, 0, "§cSET NPC", lore));
			p.sendMessage("§7Right click to the NPC to add the GUI!");
		} else if (args[0].equalsIgnoreCase("delete")) {

			String name = args[1];

			NPCShop shop = new NPCShop(name);

			if (!shop.exists()) {
				p.sendMessage("§cThis shop does not exists!");
				return false;
			}

			if (shop.delete()) {
				ShopManager.loadShops();
				p.sendMessage("§aSuccessfully deleted §6Shop " + name + "§a!");
			}

		} else if (args[0].equalsIgnoreCase("edit")) {

			String name = args[1];

			if (!ShopManager.shopsName.containsKey(name)) {
				p.sendMessage("§cThis shop does not exist");
				return false;
			}
			new ShopEditInventory(p, (NPCShop) ShopManager.shopsName.get(name));
			p.sendMessage("Editing " + ((NPCShop) ShopManager.shopsName.get(name)).getName());
		}

		return false;
	}
}