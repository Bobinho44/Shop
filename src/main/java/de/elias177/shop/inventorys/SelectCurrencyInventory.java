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

public class SelectCurrencyInventory implements Listener {
	private Player player;
	private EditItemInventory parentInventory;
	private Inventory inventory;

	public SelectCurrencyInventory(Player player, EditItemInventory parentInventory) {
		this.player = player;
		this.parentInventory = parentInventory;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private void setupInventory() {
		this.inventory = Bukkit.createInventory(null, 9, "§6§lSelect currency");

		this.inventory.setItem(1, ItemCreator.createItem(Material.IRON_NUGGET, 1, 0, "§6§lPenny Token",
				Arrays.asList(new String[] { "§7Click here in order", "§7to select this currency" })));

		this.inventory.setItem(3, ItemCreator.createItem(Material.IRON_INGOT, 1, 0, "§6§lMedallion Token",
				Arrays.asList(new String[] { "§7Click here in order", "§7to select this currency" })));

		this.inventory.setItem(5, ItemCreator.createItem(Material.GOLD_NUGGET, 1, 0, "§6§lCastle Token",
				Arrays.asList(new String[] { "§7Click here in order", "§7to select this currency" })));

		this.inventory.setItem(7, ItemCreator.createItem(Material.GOLD_INGOT, 1, 0, "§6§lKingdom Token",
				Arrays.asList(new String[] { "§7Click here in order", "§7to select this currency" })));

		this.player.openInventory(this.inventory);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!event.getClickedInventory().equals(this.inventory))
			return;
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;
		this.player.closeInventory();
		this.parentInventory.setPriceUnit(event.getCurrentItem().getType());
		this.parentInventory.setupInventory();
	}
}