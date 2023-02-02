package de.elias177.shop.inventorys;

import de.elias177.shop.Shop;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.utils.ItemCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EditItemInventory implements Listener {
	private Inventory inventory;
	private Player player;
	private NPCShop npcShop;
	private int editSlot;
	private ItemStack editItem;
	private int price;
	private int page;
	private Material priceUnit;
	private String isChatting;
	private String displayName;
	private List<String> lore;

	public EditItemInventory(Player player, NPCShop npcShop, int editSlot, ItemStack editItem, int price,
			Material priceUnit, int page) {
		this.player = player;
		this.npcShop = npcShop;
		this.editSlot = editSlot;
		this.editItem = editItem;
		this.price = price;
		this.priceUnit = priceUnit;
		this.page = page;
		this.isChatting = null;

		this.displayName = editItem.getItemMeta().getDisplayName();
		this.lore = new ArrayList<>();
		if (editItem.hasItemMeta() && editItem.getItemMeta().hasLore()) {
			this.lore.addAll(editItem.getItemMeta().getLore());
			this.lore.remove(this.lore.size() - 1);
		}
		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	public void setupInventory() {
		this.inventory = Bukkit.createInventory(null, 18, "§6§lEDIT ITEM");

		this.inventory.setItem(0, ItemCreator.createItem(Material.GOLD_INGOT, 1, 0, "§6§lPriceUnit",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the price-unit", "§7of the item" })));

		this.inventory.setItem(2, ItemCreator.createItem(Material.PAPER, 1, 0, "§6§lPrice",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the price", "§7of the item" })));

		this.inventory.setItem(3, ItemCreator.createItem(Material.NAME_TAG, 1, 0, "§6§lName",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the name", "§7of the item" })));

		this.inventory.setItem(4, ItemCreator.createItem(Material.BOOK, 1, 0, "§6§lLore",
				Arrays.asList(new String[] { "§7Click here in order", "§7to set the lore", "§7of the item" })));

		this.inventory.setItem(6, ItemCreator.createItem(Material.LIME_WOOL, 1, 0, "§a§lSave",
				Arrays.asList(new String[] { "§7Click here in order", "§7to §asave §7the item" })));

		this.inventory.setItem(8, ItemCreator.createItem(Material.RED_WOOL, 1, 0, "§4§lDelete",
				Arrays.asList(new String[] { "§7Click here in order", "§7to §4delete §7the item" })));

		for (int i = 9; i < 18; i++) {

			if (i == 13) {
				List<String> coloredLore = new ArrayList<>();
				for (String loreStr : this.lore) {
					coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreStr));
				}
				coloredLore.add("§7Cost: §e" + this.price + " " + ItemCreator.repalceTokenNames(this.priceUnit));
				ItemStack item = ItemCreator.createItem(this.editItem.getType(), 1, this.editItem.getData().getData(),
						ChatColor.translateAlternateColorCodes('&', this.displayName), coloredLore);
				ItemMeta meta = item.getItemMeta();
				((Damageable) meta).setDamage(((Damageable) this.editItem.getItemMeta()).getDamage());
				meta.setUnbreakable(true);
				meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
				meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
				meta.setCustomModelData(Integer.valueOf(this.editItem.getItemMeta().hasCustomModelData()
						? this.editItem.getItemMeta().getCustomModelData()
						: 0));
				item.setItemMeta(meta);
				this.inventory.setItem(i, item);
			} else {
				this.inventory.setItem(i,
						ItemCreator.createItem(Material.GRAY_STAINED_GLASS_PANE, 1, 0, "", new ArrayList()));
			}
		}

		this.player.openInventory(this.inventory);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null)
			return;
		if (!event.getClickedInventory().equals(this.inventory))
			return;
		event.setCancelled(true);

		if (event.getSlot() == 0) {
			this.player.closeInventory();
			new SelectCurrencyInventory(this.player, this);

		} else if (event.getSlot() == 2) {
			this.player.closeInventory();
			this.player.sendMessage("§aEnter the price into the chat");
			this.isChatting = "price";

		} else if (event.getSlot() == 3) {
			this.player.closeInventory();
			this.player.sendMessage("§aEnter the name of the item into the chat");
			this.isChatting = "name";

		} else if (event.getSlot() == 4) {
			this.player.closeInventory();
			this.player.sendMessage("§aEnter the lore into the chat. §cUse ; for new lines and & for color");
			this.isChatting = "lore";
		} else if (event.getSlot() == 6) {

			this.lore.add("&7Cost: &e" + this.price + " " + ItemCreator.repalceTokenNames(this.priceUnit));
			this.npcShop.addItem(this.page, this.editSlot, this.editItem.getType().toString(),
					this.editItem.getItemMeta().hasCustomModelData() ? this.editItem.getItemMeta().getCustomModelData()
							: 0,
					this.displayName, this.lore, this.editItem.getAmount(), this.priceUnit.toString(), this.price,
					((Damageable) this.editItem.getItemMeta()).getDamage());
			this.npcShop.read();
			this.player.closeInventory();
			new ShopEditInventory(this.player, this.npcShop);
		}

		if (event.getSlot() == 8) {
			this.npcShop.removeItem(this.page, this.editSlot);
			this.npcShop.read();
			this.player.closeInventory();
			new ShopEditInventory(this.player, this.npcShop);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.getPlayer() != this.player)
			return;
		if (this.isChatting == null)
			return;
		event.setCancelled(true);

		if (this.isChatting.equals("price")) {
			int chatPrice = 0;
			try {
				chatPrice = Integer.parseInt(event.getMessage());
			} catch (NumberFormatException exception) {
				this.player.sendMessage("§cPlease enter a valid number, try again");
				return;
			}
			this.price = chatPrice;
		} else if (this.isChatting.equals("lore")) {

			String[] loreArr = event.getMessage().split(";");
			this.lore.clear();
			for (int i = 0; i < loreArr.length; i++) {
				this.lore.add(loreArr[i]);
			}
		} else if (this.isChatting.equals("name")) {

			this.displayName = event.getMessage();
		}

		this.isChatting = null;

		(new BukkitRunnable() {
			public void run() {
				EditItemInventory.this.setupInventory();
			}
		}).runTask((Plugin) Shop.getInstance());
	}

	public void setPriceUnit(Material priceUnit) {
		this.priceUnit = priceUnit;
	}
}