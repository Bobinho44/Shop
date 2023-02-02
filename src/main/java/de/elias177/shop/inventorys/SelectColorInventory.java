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

public class SelectColorInventory implements Listener {
	private Inventory inv;
	private Player player;
	private ShopEditInventory parentInventory;

	public SelectColorInventory(Player player, ShopEditInventory parentInventory) {
		this.player = player;
		this.parentInventory = parentInventory;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private void setupInventory() {
		this.inv = Bukkit.createInventory(null, 27, "§6§lSelect Color");

		this.inv.setItem(13, ItemCreator.createItem(Material.WHITE_WOOL, 1, 0, "§6§lSelect Color",
				Arrays.asList(new String[] { "§7Click on the glass pane", "§7to select it as a filler" })));

		this.inv.setItem(10, ItemCreator.createItem(Material.WHITE_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(2, ItemCreator.createItem(Material.YELLOW_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(11, ItemCreator.createItem(Material.ORANGE_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(20, ItemCreator.createItem(Material.RED_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(3, ItemCreator.createItem(Material.PINK_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(12, ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(21, ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(4, ItemCreator.createItem(Material.LIME_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(22, ItemCreator.createItem(Material.GREEN_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(5, ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(14, ItemCreator.createItem(Material.CYAN_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(23, ItemCreator.createItem(Material.BLUE_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(6, ItemCreator.createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(15, ItemCreator.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(24, ItemCreator.createItem(Material.BROWN_STAINED_GLASS_PANE, 1, 0, "", null));
		this.inv.setItem(16, ItemCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, 0, "", null));

		this.player.openInventory(this.inv);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (!event.getClickedInventory().equals(this.inv))
			return;
		event.setCancelled(true);
		if (event.getSlot() == 13)
			return;
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;
		this.parentInventory.getNpcShop().setPlaceholderMaterial(event.getCurrentItem().getType());
		this.parentInventory.setupInventory();
	}
}