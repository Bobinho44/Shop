package de.elias177.shop.listener;

import de.elias177.shop.inventorys.ShopInventory;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.manager.ShopManager;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class InteractAtEntityListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();

		if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
			if (!p.getInventory().getItemInMainHand().hasItemMeta()) {
				return;
			}
			if (!p.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
				return;
			}
			if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§cSET NPC")) {
				List<String> lore = p.getInventory().getItemInMainHand().getItemMeta().getLore();
				String name = lore.get(0);

				NPCShop shop = new NPCShop(name);

				if (!shop.exists()) {
					p.sendMessage("§cThis shop does not exists!");
					p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

					return;
				}
				shop.setLocation(e.getRightClicked().getLocation());
				p.sendMessage("§aSuccessfully set the location of §6" + name);
				p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				ShopManager.loadShops();

				return;
			}
		}
		if (ShopManager.shopsDisplayname.containsKey(e.getRightClicked().getCustomName())) {
			NPCShop shop = (NPCShop) ShopManager.shopsDisplayname.get(e.getRightClicked().getCustomName());
			p.closeInventory();
			new ShopInventory(p, shop);

			return;
		}
		if (ShopManager.shopsLocation.containsKey(e.getRightClicked().getLocation())) {
			NPCShop shop = (NPCShop) ShopManager.shopsLocation.get(e.getRightClicked().getLocation());
			p.closeInventory();
			new ShopInventory(p, shop);
			return;
		}
	}
}