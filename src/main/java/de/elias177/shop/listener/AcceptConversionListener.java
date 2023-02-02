package de.elias177.shop.listener;

import de.elias177.bank.Bank;
import de.elias177.bank.database.UserDatabase;
import de.elias177.bank.database.keys.UserKeys;
import de.elias177.shop.Shop;
import de.elias177.shop.manager.ShopItem;
import de.elias177.shop.utils.BItemBuilder;
import de.elias177.shop.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptConversionListener implements Listener {

    private static final Map<String, Long> moneyKey = Map.of(
            Material.IRON_NUGGET.toString(), 1L,
            Material.IRON_INGOT.toString(), 6L,
            Material.GOLD_NUGGET.toString(), 36L,
            Material.GOLD_INGOT.toString(), 216L
    );

    private boolean finished = false;
    private boolean accepted = false;
    private final Player player;
    private int remove;
    private final long rest;
    private final ShopItem shopItem;
    private final Map<String, Long> take;
    private final Map<String, Long> give;

    public AcceptConversionListener(Player player, Map<String, Long> take, Map<String, Long> give, int remove, long rest, ShopItem shopItem) {
        this.player = player;
        this.take = take;
        this.give = give;
        this.remove = remove;
        this.rest = rest;
        this.shopItem = shopItem;

        Bukkit.getPluginManager().registerEvents(this, Shop.getInstance());

        Inventory inventory = Bukkit.createInventory(new ConversionHolder(), 27, "Bank conversion");

        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new BItemBuilder(Material.BLUE_STAINED_GLASS_PANE).name("").build());
        }

        inventory.setItem(13, new BItemBuilder(Material.ARROW).name("§6Accept the conversion").lore(List.of("§aRight-click to accept", "§cLeft-click to decline")).customModelData(1).build());

        int i = 9;
        for (String moneyBefore : moneyKey.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList()) {
            Material moneyMaterial = Material.valueOf(moneyBefore);
            inventory.setItem(i, take.get(moneyBefore) > 0 ?
                    new BItemBuilder(moneyMaterial).name(ItemCreator.repalceTokenNames(moneyMaterial)).lore("§7Amount: §6" + Math.toIntExact(take.get(moneyBefore))).build() :
                    new BItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build());
            i++;
        }
        i = 14;
        for (String moneyAfter : moneyKey.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList()) {
            Material moneyMaterial = Material.valueOf(moneyAfter);
            inventory.setItem(i, give.get(moneyAfter) > 0 ?
                    new BItemBuilder(moneyMaterial).name(ItemCreator.repalceTokenNames(moneyMaterial)).lore("§7Amount: §6" + Math.toIntExact(give.get(moneyAfter))).build() :
                    new BItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build());
            i++;
        }

        player.openInventory(inventory);
        waitPlayerAnswer();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConversionHolder)) {
            return;
        }

        if (!finished) {
            finished = true;
            accepted = false;
        }
    }

    @EventHandler
    public void onClickOnConversionMenu(InventoryClickEvent event) {
        if ((event.getClickedInventory() == null)) {
            return;
        }

        if (event.getClickedInventory().getHolder() == null) {
            return;
        }

        if (!(event.getClickedInventory().getHolder() instanceof ConversionHolder)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClick() == ClickType.RIGHT) {
            finished = true;
            accepted = true;
        }

        if (event.getClick() == ClickType.LEFT) {
            finished = true;
            accepted = false;
        }
    }

    public void waitPlayerAnswer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (finished) {
                    HandlerList.unregisterAll(AcceptConversionListener.this);

                    if (accepted) {
                        UserDatabase userDatabase = Bank.getInstance().getUserDatabase();
                        HashMap<UserKeys, Object> user = userDatabase.getInfosByUUID(player.getUniqueId().toString());

                        user.put(UserKeys.valueOf(shopItem.getPriceType().toString()), -rest);

                        for (Map.Entry<String, Long> money : take.entrySet()) {
                            user.put(UserKeys.valueOf(money.getKey()), (long) user.get(UserKeys.valueOf(money.getKey())) - money.getValue());
                        }

                        for (Map.Entry<String, Long> money : give.entrySet()) {
                            user.put(UserKeys.valueOf(money.getKey()), (long) user.get(UserKeys.valueOf(money.getKey())) + money.getValue());
                        }

                        userDatabase.updateUser(user);
                        for (ItemStack itemStack : player.getInventory().getContents()) {
                            if (remove > 0 && itemStack != null && itemStack.getType() != Material.AIR
                                    && itemStack.getType().equals(shopItem.getPriceType())) {
                                int am = itemStack.getAmount();
                                if (am >= remove) {
                                    itemStack.setAmount(am - remove);
                                    remove -= am;
                                    System.out.println(remove);
                                } else {
                                    player.getInventory().remove(itemStack);
                                    remove -= am;
                                }
                            }
                        }

                        ItemStack item = shopItem.getItem().clone();
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setLore(null);
                        item.setItemMeta(itemMeta);
                        player.getInventory().addItem(item);

                        player.sendMessage("§a The item was purchased with tokens from your bank.");
                        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1F, 1F);
                    }

                    else {
                        player.sendMessage("§cYou have refused the conversion!");
                    }
                    player.closeInventory();
                    cancel();
                }
            }
        }.runTaskTimer(Shop.getInstance(), 0L, 2L);
    }

    private class ConversionHolder implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

}
