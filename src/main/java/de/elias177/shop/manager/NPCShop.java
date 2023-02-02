package de.elias177.shop.manager;

import de.elias177.shop.Shop;
import de.elias177.shop.utils.ItemCreator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class NPCShop {
	String Name;
	private String strDisplayName;
	private String strInventoryTitle;
	private int intInventorySize;
	private Material matInventoryFillItem;
	private Location locLocation;
	private HashMap<Integer, List<ShopItem>> listShopItems;
	private HashMap<Integer, HashMap<Integer, ShopItem>> hashShopItems;

	public NPCShop(String name) {
		this.Name = name;

		read();
	}

	public String getName() {
		return this.Name;
	}

	public String getStrDisplayName() {
		return this.strDisplayName;
	}

	public String getStrInventoryTitle() {
		return this.strInventoryTitle;
	}

	public Location getLocLocation() {
		return this.locLocation;
	}

	public int getIntInventorySize() {
		return this.intInventorySize;
	}

	public Material getMatInventoryFillItem() {
		return this.matInventoryFillItem;
	}

	public HashMap<Integer, List<ShopItem>> getListShopItems() {
		return this.listShopItems;
	}

	public HashMap<Integer, HashMap<Integer, ShopItem>> getHashShopItems() {
		return this.hashShopItems;
	}

	private File getFolder() {
		return new File(Shop.getInstance().getDataFolder().getPath());
	}

	private File getFile() {
		return new File(getFolder(), this.Name + ".yml");
	}

	public FileConfiguration getFileConfiguration(File file) {
		return (FileConfiguration) YamlConfiguration.loadConfiguration(file);
	}

	public void save(FileConfiguration cfg, File file) {
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean exists() {
		return getFile().exists();
	}

	public boolean delete() {
		return getFile().delete();
	}

	public void create() {
		if (!getFolder().exists()) {
			getFolder().mkdir();
		}
		if (!exists()) {
			try {
				FileUtils.copyToFile(Shop.getInstance().getResource("shop.yml"), getFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void read() {
		if (!exists()) {
			return;
		}
		FileConfiguration cfg = getFileConfiguration(getFile());

		this.strDisplayName = getString("displayName", getFile()).replace("&", "§");
		this.strInventoryTitle = getString("Inventory.Title", getFile()).replace("&", "§");

		this.intInventorySize = getInt("Inventory.Size", getFile()).intValue();

		this.matInventoryFillItem = Material.valueOf(getString("Inventory.fillItem", getFile()));

		if (getString("Location.World", getFile()) != null) {
			String locWorld = getString("Location.World", getFile());
			double x = getDouble("Location.X", getFile()).doubleValue();
			double y = getDouble("Location.Y", getFile()).doubleValue();
			double z = getDouble("Location.Z", getFile()).doubleValue();
			double yaw = getDouble("Location.Yaw", getFile()).doubleValue();
			double pitch = getDouble("Location.Pitch", getFile()).doubleValue();
			this.locLocation = new Location(Bukkit.getWorld(locWorld), x, y, z);
			this.locLocation.setYaw((float) yaw);
			this.locLocation.setPitch((float) pitch);
		}

		this.listShopItems = new HashMap<>();
		this.hashShopItems = new HashMap<>();

		for (String pageSection : cfg.getConfigurationSection("Inventory.Items").getKeys(false)) {
			int page = Integer.parseInt(pageSection);
			ArrayList<ShopItem> items = new ArrayList<>();
			HashMap<Integer, ShopItem> itemHash = new HashMap<>();
			for (String section : cfg.getConfigurationSection("Inventory.Items." + page).getKeys(false)) {
				int slot = Integer.parseInt(section);
				String item = getString("Inventory.Items." + page + "." + slot + ".Item", getFile());
				int modelData = getInt("Inventory.Items." + page + "." + slot + ".ItemModelData", getFile()).intValue();
				String itemName = getString("Inventory.Items." + page + "." + slot + ".ItemName", getFile());

				if (itemName != null) {
					itemName = itemName.replace("&", "§");
				}
				int itemAmount = getInt("Inventory.Items." + page + "." + slot + ".ItemAmount", getFile()).intValue();
				Material priceType = Material
						.valueOf(getString("Inventory.Items." + page + "." + slot + ".PriceType", getFile()));
				int price = getInt("Inventory.Items." + page + "." + slot + ".Price", getFile()).intValue();

				List<String> rawLore = getStringList("Inventory.Items." + page + "." + slot + ".ItemLore", getFile());
				List<String> lore = new ArrayList<>();
				rawLore.forEach(l -> lore.add(l.replace("&", "§")));

				ItemStack itemStack = ItemCreator.createItem(Material.valueOf(item), itemAmount, 0, itemName, lore);
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setCustomModelData(Integer.valueOf(modelData));
				((Damageable) itemMeta).setDamage(
						getInt("Inventory.Items." + page + "." + slot + ".Durability", getFile()).intValue());
				itemMeta.setUnbreakable(true);
				itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_UNBREAKABLE });
				itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
				itemStack.setItemMeta(itemMeta);

				ShopItem shopItem = new ShopItem(slot, itemStack, priceType, price);
				items.add(shopItem);
				itemHash.put(Integer.valueOf(slot), shopItem);
			}
			this.hashShopItems.put(Integer.valueOf(page), itemHash);
			this.listShopItems.put(Integer.valueOf(page), items);
		}
	}

	public void addItem(int page, int slot, String item, int itemModelData, String itemName, List<String> itemLore,
			int itemAmount, String priceType, int price, int durability) {
		FileConfiguration cfg = getFileConfiguration(getFile());

		cfg.set("Inventory.Items." + page + "." + slot + ".Item", item);
		cfg.set("Inventory.Items." + page + "." + slot + ".ItemModelData", Integer.valueOf(itemModelData));
		cfg.set("Inventory.Items." + page + "." + slot + ".ItemName", itemName);
		cfg.set("Inventory.Items." + page + "." + slot + ".ItemLore", itemLore);
		cfg.set("Inventory.Items." + page + "." + slot + ".ItemAmount", Integer.valueOf(itemAmount));
		cfg.set("Inventory.Items." + page + "." + slot + ".PriceType", priceType);
		cfg.set("Inventory.Items." + page + "." + slot + ".Price", Integer.valueOf(price));
		cfg.set("Inventory.Items." + page + "." + slot + ".Durability", Integer.valueOf(durability));

		save(cfg, getFile());
	}

	public void removeItem(int page, int slot) {
		FileConfiguration cfg = getFileConfiguration(getFile());

		cfg.set("Inventory.Items." + page + "." + slot, null);
		save(cfg, getFile());
	}

	public void setLocation(Location loc) {
		FileConfiguration cfg = getFileConfiguration(getFile());

		cfg.set("Location.World", loc.getWorld().getName());
		cfg.set("Location.X", Double.valueOf(loc.getX()));
		cfg.set("Location.Y", Double.valueOf(loc.getY()));
		cfg.set("Location.Z", Double.valueOf(loc.getZ()));
		cfg.set("Location.Yaw", Float.valueOf(loc.getYaw()));
		cfg.set("Location.Pitch", Float.valueOf(loc.getPitch()));

		save(cfg, getFile());
	}

	private String getString(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return cfg.getString(slot);
	}

	private List<String> getStringList(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return cfg.getStringList(slot);
	}

	private Integer getInt(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return Integer.valueOf(cfg.getInt(slot));
	}

	private Double getDouble(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return Double.valueOf(cfg.getDouble(slot));
	}

	private Long getLong(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return Long.valueOf(cfg.getLong(slot));
	}

	private boolean getBoolean(String slot, File file) {
		FileConfiguration cfg = getFileConfiguration(file);
		return cfg.getBoolean(slot);
	}

	public void setInventorySize(int size) {
		FileConfiguration cfg = getFileConfiguration(getFile());
		cfg.set("Inventory.Size", Integer.valueOf(size));
		this.intInventorySize = size;
		save(cfg, getFile());
	}

	public void setPlaceholderMaterial(Material material) {
		FileConfiguration cfg = getFileConfiguration(getFile());
		cfg.set("Inventory.fillItem", material.toString());
		this.matInventoryFillItem = material;
		save(cfg, getFile());
	}

	public void setDisplayName(String displayName) {
		FileConfiguration cfg = getFileConfiguration(getFile());
		cfg.set("displayName", displayName);
		this.strDisplayName = displayName;
		save(cfg, getFile());
	}

	public void setStrInventoryTitle(String newTitle) {
		FileConfiguration cfg = getFileConfiguration(getFile());
		cfg.set("Inventory.Title", newTitle);
		this.strInventoryTitle = ChatColor.translateAlternateColorCodes('&', newTitle);
		save(cfg, getFile());
	}
}