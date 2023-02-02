package de.elias177.shop.inventorys;

import de.elias177.bank.Bank;
import de.elias177.bank.database.UserDatabase;
import de.elias177.bank.database.keys.UserKeys;
import de.elias177.shop.Shop;
import de.elias177.shop.listener.AcceptConversionListener;
import de.elias177.shop.manager.NPCShop;
import de.elias177.shop.manager.ShopItem;
import de.elias177.shop.utils.ItemCreator;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ShopInventory implements Listener {

	private final UserDatabase userDatabase = Bank.getInstance().getUserDatabase();

	Player p;
	NPCShop npcShop;
	private int currentPage;
	private Inventory inv;
	private boolean hasMultiplePages;

	private final Map<String, Long> moneyKey = Map.of(
			Material.IRON_NUGGET.toString(), 1L,
			Material.IRON_INGOT.toString(), 6L,
			Material.GOLD_NUGGET.toString(), 36L,
			Material.GOLD_INGOT.toString(), 216L
	);

	public ShopInventory(Player p, NPCShop npcShop) {
		this.p = p;
		this.npcShop = npcShop;
		this.currentPage = 1;
		this.hasMultiplePages = (npcShop.getListShopItems().keySet().size() != 1);
		Bukkit.getPluginManager().registerEvents(this, (Plugin) Shop.getInstance());

		setupInventory();
	}

	private void setupInventory() {
		this.inv = Bukkit.createInventory(null, this.npcShop.getIntInventorySize(),
				this.npcShop.getStrInventoryTitle());

		for (int i = 0; i < this.inv.getSize(); i++) {
			this.inv.setItem(i, ItemCreator.createItem(this.npcShop.getMatInventoryFillItem(), 1, 0, null, null));
		}

		if (this.npcShop.getListShopItems().containsKey(this.currentPage)) {
			for (ShopItem item : this.npcShop.getListShopItems().get(this.currentPage)) {
				if (item.getItem().getType() == Material.BARRIER) {
					this.inv.setItem(item.getSlot(), null);
					continue;
				}
				this.inv.setItem(item.getSlot(), item.getItem());
			}
		}

		if (this.hasMultiplePages) {

			if (this.currentPage != 1) {
				this.inv.setItem(this.inv.getSize() - 9,
						ItemCreator.createItem(Material.ARROW, 1, 0, "§6Previous page", null));
			}

			if (this.currentPage != ((Integer) Collections.<Integer>max(this.npcShop.getListShopItems().keySet()))
					.intValue()) {
				this.inv.setItem(this.inv.getSize() - 1,
						ItemCreator.createItem(Material.ARROW, 1, 0, "§6Next page", null));
			}
		}

		this.p.openInventory(this.inv);
	}

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent e) throws Exception {
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() == null) {
			return;
		}
		if (e.getCurrentItem().getItemMeta() == null) {
			return;
		}
		if (e.getClickedInventory().equals(this.inv)) {
			e.setCancelled(true);

			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
					|| e.getAction() == InventoryAction.HOTBAR_SWAP)
				return;
			if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§6Previous page")
					|| e.getCurrentItem().getItemMeta().getDisplayName().equals("§6Next page")) {
				p.closeInventory();
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§6Next page")) {
					this.currentPage++;
				} else {
					this.currentPage--;
				}
				(new BukkitRunnable() {
					public void run() {
						ShopInventory.this.setupInventory();
					}
				}).runTaskLater(Shop.getInstance(), 1L);

				return;
			}
			int slot = e.getSlot();

			if (this.npcShop.getHashShopItems().get(this.currentPage)
					.containsKey(slot)) {
				ShopItem shopItem = (ShopItem) ((HashMap<?, ?>) this.npcShop.getHashShopItems()
						.get(this.currentPage)).get(slot);

				int val = 0;
				for (ItemStack itemStack : p.getInventory().getContents()) {
					if (itemStack != null && itemStack.getType() != Material.AIR
							&& itemStack.getType().equals(shopItem.getPriceType())) {
						int am = itemStack.getAmount();
						val += am;
					}
				}

				int remove = shopItem.getPrice();

				if (val < shopItem.getPrice()) {
					long rest = shopItem.getPrice() - val;
					remove = val;
					HashMap<UserKeys, Object> user = userDatabase.getInfosByUUID(p.getUniqueId().toString());

					long actual = (long) user.get(UserKeys.valueOf(shopItem.getPriceType().toString()));
					if (actual >= rest) {
						user.put(UserKeys.valueOf(shopItem.getPriceType().toString()), actual - rest);
						userDatabase.updateUser(user);
						p.sendMessage("§a The item was purchased with tokens from your bank.");
					}
					else {
						long bal = moneyKey.entrySet().stream()
								.mapToLong(money -> (long) user.get(UserKeys.valueOf(money.getKey())) * money.getValue())
								.sum();

						if (moneyKey.get(shopItem.getPriceType().toString()) * rest > bal) {
							p.sendMessage("§cYou dont have enough to pay!");
							p.closeInventory();
							return;
						}

						long toPaid = moneyKey.get(shopItem.getPriceType().toString()) * (rest - actual);
						Map<String, Long> toTake = new HashMap<>();
						Map<String, Long> toGive = moneyKey.entrySet().stream()
								.collect(Collectors.toMap(Map.Entry::getKey, money -> money.getKey().equals(shopItem.getPriceType().toString()) ? rest - actual : 0));

						for (String money : moneyKey.entrySet().stream()
								.sorted(Map.Entry.comparingByValue())
								.map(Map.Entry::getKey)
								.toList()) {
							long newMoneyAmount = toPaid <= 0 || money.equals(shopItem.getPriceType().toString()) ?
									0 :
									Math.min((long) Math.ceil(1F * toPaid / moneyKey.get(money)), (long) user.get(UserKeys.valueOf(money)));
							toTake.put(money, newMoneyAmount);
							toPaid -= moneyKey.get(money) * newMoneyAmount;
						}

						if (toPaid <= 0) {
							toPaid *= -1;

							for (Map.Entry<String, Long> money : toTake.entrySet()) {
								long newMoneyAmount = Math.min(toPaid / moneyKey.get(money.getKey()), money.getValue());
								toTake.put(money.getKey(), money.getValue() - newMoneyAmount);
								toPaid -= moneyKey.get(money.getKey()) * newMoneyAmount;
							}

							for (String money : moneyKey.entrySet().stream()
									.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
									.map(Map.Entry::getKey)
									.toList()) {
								long newMoneyAmount = toPaid / moneyKey.get(money);
								toGive.put(money, toGive.get(money) + newMoneyAmount);
								toPaid -= moneyKey.get(money) * newMoneyAmount;
							}
						}

						new AcceptConversionListener(p, toTake, toGive, remove, rest, shopItem);
						return;
					}
				}

				else {
					p.sendMessage("§a The item was purchased with tokens from your inventory..");
				}
				for (ItemStack itemStack : p.getInventory().getContents()) {
					if (remove > 0 && itemStack != null && itemStack.getType() != Material.AIR
							&& itemStack.getType().equals(shopItem.getPriceType())) {
						int am = itemStack.getAmount();
						if (am >= remove) {
							itemStack.setAmount(am - remove);
							remove -= am;
							System.out.println(remove);
						} else {
							p.getInventory().remove(itemStack);
							remove -= am;
						}
					}
				}

				ItemStack item = shopItem.getItem().clone();
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setLore(null);
				item.setItemMeta(itemMeta);
				p.getInventory().addItem(new ItemStack[] { item });
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1F, 1F);
			}
		}
	}

	@EventHandler
	public void onMove(InventoryMoveItemEvent event) {
		if (!event.getSource().equals(this.inv))
			return;
		event.setCancelled(true);
	}
}