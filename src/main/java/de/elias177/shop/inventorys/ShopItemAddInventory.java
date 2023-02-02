package de.elias177.shop.inventorys;

import de.elias177.shop.Shop;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.manager.ShopManager;
import de.elias177.shop.utils.ItemCreator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ShopItemAddInventory implements Listener {
	Player p;
	String shopName;
	private Inventory inv;
	private ChatState chatState;
	private Material item;
	private String itemName;
	private String priceType;
	private List<String> itemLore;
	private int price;
	private int modelData;
	private int itemAmount;
	private int slot;
	private int page;

	public ShopItemAddInventory(Player p, String shopName) {
		this.p = p;
		this.shopName = shopName;

		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private void setupInventory() {
		this.inv = Bukkit.createInventory(null, 27, "§7Add Item");

		for (int i = 0; i < this.inv.getSize(); i++) {
			this.inv.setItem(i, ItemCreator.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, 0, null, null));
		}

		this.inv.setItem(13, null);
		this.inv.setItem(22, ItemCreator.createItem(Material.GREEN_STAINED_GLASS_PANE, 1, 0, "§aSave", null));

		this.p.openInventory(this.inv);
	}

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() == null) {
			return;
		}
		if (e.getCurrentItem().getItemMeta() == null) {
			return;
		}
		if (e.getInventory().equals(this.inv)) {
			if (e.getSlot() != 13 && e.getClickedInventory().equals(this.inv)) {
				e.setCancelled(true);
			}
			if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aSave")
					&& e.getView().getItem(13) != null) {
				ItemStack item = e.getView().getItem(13);
				this.item = item.getType();
				this.itemAmount = item.getAmount();
				if (item.hasItemMeta()) {
					this.itemName = item.getItemMeta().getDisplayName();
					this.itemLore = item.getItemMeta().getLore();
					this.modelData = item.getItemMeta().getCustomModelData();
				}

				this.chatState = ChatState.PriceType;
				p.closeInventory();
				p.sendMessage("§7What price unit should it have ? (IRON_INGOT, GOLD_INGOT ...)");
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		Material mat;
		int price, slot;
		NPCShop npcShop;
		Player p = e.getPlayer();
		String message = e.getMessage();
		String[] msg = message.split(Pattern.quote(" "));

		switch (this.chatState) {

		case PriceType:
			e.setCancelled(true);
			if (msg.length > 1) {
				p.sendMessage("§cNo valid item!");

				return;
			}
			mat = Material.getMaterial(msg[0]);
			this.priceType = mat.toString();
			this.chatState = ChatState.Price;
			p.sendMessage("§7How much should it cost ? (amount of price unit)");
			return;

		case Price:
			e.setCancelled(true);
			if (msg.length > 1 || !msg[0].matches("[0-9]+")) {
				p.sendMessage("§cNo valid number!");

				return;
			}
			price = Integer.parseInt(msg[0]);
			this.price = price;
			this.chatState = ChatState.Slot;
			p.sendMessage("§7On which slot should the item be located ? (Number)");
			return;

		case Slot:
			e.setCancelled(true);
			if (msg.length > 1 || !msg[0].matches("[0-9]+")) {
				p.sendMessage("§cNo valid number!");

				return;
			}
			slot = Integer.parseInt(msg[0]);
			this.slot = slot;
			npcShop = (NPCShop) ShopManager.shopsName.get(this.shopName);

			if (this.itemLore == null) {
				this.itemLore = new ArrayList<>();
			}
			this.itemLore.add("&7Price: &65x " + ItemCreator.repalceTokenNames(Material.getMaterial(this.priceType)));
			npcShop.addItem(this.page, this.slot, this.item.toString(), this.modelData, this.itemName, this.itemLore,
					this.itemAmount, this.priceType, this.price, 0);
			this.chatState = null;
			ShopManager.loadShops();
			p.sendMessage("§aItem successfully added!");
			return;
		}
	}
}
