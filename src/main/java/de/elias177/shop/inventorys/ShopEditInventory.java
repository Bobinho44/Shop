package de.elias177.shop.inventorys;

import de.elias177.shop.Shop;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.manager.ShopItem;
import de.elias177.shop.utils.ItemCreator;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopEditInventory implements Listener {
	private Player player;
	private NPCShop npcShop;

	public ShopEditInventory(Player player, NPCShop shop) {
		this.player = player;
		this.npcShop = shop;
		this.currentPage = 1;
		this.isChatting = false;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private Inventory inventory;
	private int currentPage;
	private boolean isChatting;

	public void setupInventory() {
		this.inventory = Bukkit.createInventory(null, this.npcShop.getIntInventorySize(), "§6§lEDIT");

		if (this.npcShop.getListShopItems().containsKey(Integer.valueOf(this.currentPage))) {
			for (ShopItem item : this.npcShop.getListShopItems().get(Integer.valueOf(this.currentPage))) {
				this.inventory.setItem(item.getSlot(), item.getItem());
			}
		}
		if (this.currentPage != 1) {
			this.inventory.setItem(this.inventory.getSize() - 9,
					ItemCreator.createItem(Material.ARROW, 1, 0, "§6Previous page", null));
		}
		this.inventory.setItem(this.inventory.getSize() - 1,
				ItemCreator.createItem(Material.ARROW, 1, 0, "§6Next page", null));

		this.player.getInventory().setItem(21, ItemCreator.createItem(Material.HOPPER, 1, 0, "§6§lSize",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the size of the shop" })));

		this.player.getInventory().setItem(22, ItemCreator.createItem(Material.PAPER, 1, 0, "§6§lDisplayname",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the name of the shop" })));

		this.player.getInventory().setItem(23, ItemCreator.createItem(Material.WHITE_DYE, 1, 0, "§6§lColor",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the color of the shop" })));

		this.player.openInventory(this.inventory);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null)
			return;
		if (!event.getView().getTopInventory().equals(this.inventory))
			return;
		if (event.getCurrentItem().hasItemMeta()
				&& event.getCurrentItem().getItemMeta().getDisplayName().equals("§6§lSize")) {
			this.player.closeInventory();
			new SelectSizeInventory(this.player, this);
			return;
		}
		if (event.getCurrentItem().hasItemMeta()
				&& event.getCurrentItem().getItemMeta().getDisplayName().equals("§6§lColor")) {
			this.player.closeInventory();
			new SelectColorInventory(this.player, this);
			return;
		}
		if (event.getCurrentItem().hasItemMeta()
				&& event.getCurrentItem().getItemMeta().getDisplayName().equals("§6§lDisplayname")) {
			this.player.closeInventory();
			this.player.sendMessage("§7Please enter the §aname §7of the shop into the chat. Use §a& §7for color");
			this.isChatting = true;

			return;
		}
		if (!event.getClickedInventory().equals(this.inventory))
			return;
		event.setCancelled(true);

		if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Previous page")
				|| event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Next page")) {
			this.player.closeInventory();
			if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Next page")) {
				this.currentPage++;
			} else {
				this.currentPage--;
			}
			(new BukkitRunnable() {
				public void run() {
					ShopEditInventory.this.setupInventory();
				}
			}).runTaskLater((Plugin) Shop.getInstance(), 1L);

			return;
		}

		if (!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().isUnbreakable()) {
			event.getCurrentItem().getItemMeta().setUnbreakable(true);
			event.getCurrentItem().getItemMeta().addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
			event.getCurrentItem().getItemMeta().addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
		}
		new EditItemInventory(this.player, this.npcShop, event.getSlot(), event.getCurrentItem(), 0,
				Material.IRON_NUGGET, this.currentPage);
	}

	@EventHandler
	public void onMove(InventoryMoveItemEvent event) {
		if (!event.getSource().equals(this.inventory))
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!event.getInventory().equals(this.inventory))
			return;
		this.player.getInventory().setItem(21, null);
		this.player.getInventory().setItem(22, null);
		this.player.getInventory().setItem(23, null);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (!this.isChatting)
			return;
		event.setCancelled(true);
		this.npcShop.setStrInventoryTitle(event.getMessage());
		this.isChatting = false;
		(new BukkitRunnable() {
			public void run() {
				ShopEditInventory.this.setupInventory();
			}
		}).runTask((Plugin) Shop.getInstance());
	}

	public NPCShop getNpcShop() {
		return this.npcShop;
	}
}