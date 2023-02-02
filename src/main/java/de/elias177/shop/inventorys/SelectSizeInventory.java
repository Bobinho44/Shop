package de.elias177.shop.inventorys;

import de.elias177.shop.Shop;
import de.elias177.shop.utils.ItemCreator;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class SelectSizeInventory implements Listener {
	private ShopEditInventory parentInventory;
	private Player player;
	private Inventory inventory;

	public SelectSizeInventory(Player player, ShopEditInventory parentInventory) {
		this.player = player;
		this.parentInventory = parentInventory;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private void setupInventory() {
		this.inventory = Bukkit.createInventory(null, 9, "§6§lSelect size");

		this.inventory.setItem(1, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L1 Row", null));
		this.inventory.setItem(2, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L2 Rows", null));
		this.inventory.setItem(3, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L3 Rows", null));
		this.inventory.setItem(5, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L4 Rows", null));
		this.inventory.setItem(6, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L5 Rows", null));
		this.inventory.setItem(7, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§6§L6 Rows", null));

		this.inventory.setItem(0, ItemCreator.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, 0, " ", null));
		this.inventory.setItem(8, ItemCreator.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, 0, " ", null));

		this.inventory.setItem(4, ItemCreator.createItem(Material.HOPPER, 1, 0, "§6§lSelect size",
				Arrays.asList(new String[] { "§7Click on the amount", "§7of rows you want the", "§7shop to have" })));

		this.player.openInventory(this.inventory);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!event.getClickedInventory().equals(this.inventory))
			return;
		event.setCancelled(true);
		if (event.getSlot() >= 1 && event.getSlot() <= 7 && event.getSlot() != 4) {
			this.player.closeInventory();
			if (event.getSlot() >= 1 && event.getSlot() <= 3) {
				this.parentInventory.getNpcShop().setInventorySize(event.getSlot() * 9);
			} else {
				this.parentInventory.getNpcShop().setInventorySize((event.getSlot() - 1) * 9);
			}
			this.parentInventory.setupInventory();
		}
	}
}