package de.elias177.shop;

import de.elias177.shop.commands.ShopCommand;
import de.elias177.shop.listener.AcceptConversionListener;
import de.elias177.shop.listener.InteractAtEntityListener;
import de.elias177.shop.manager.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class Shop extends JavaPlugin {
	public void onEnable() {
		instance = this;

		ShopManager.loadShops();

		register();

		PluginDescriptionFile pdf = getDescription();
		Bukkit.getConsoleSender().sendMessage("§7§m------------------------");
		Bukkit.getConsoleSender().sendMessage("§eName: §6" + pdf.getName());
		Bukkit.getConsoleSender().sendMessage("§eDeveloper: §b" + pdf.getAuthors());
		Bukkit.getConsoleSender().sendMessage("§eWebsite: §9" + pdf.getWebsite());
		Bukkit.getConsoleSender().sendMessage("§eVersion: §f" + pdf.getVersion());
		Bukkit.getConsoleSender().sendMessage("§eStatus: §aOnline");
		Bukkit.getConsoleSender().sendMessage("§7§m-----------------------");
	}

	private static Shop instance;

	public void onDisable() {
		PluginDescriptionFile pdf = getDescription();
		Bukkit.getConsoleSender().sendMessage("§7§m------------------------");
		Bukkit.getConsoleSender().sendMessage("§eName: §6" + pdf.getName());
		Bukkit.getConsoleSender().sendMessage("§eDeveloper: §b" + pdf.getAuthors());
		Bukkit.getConsoleSender().sendMessage("§eWebsite: §9" + pdf.getWebsite());
		Bukkit.getConsoleSender().sendMessage("§eVersion: §f" + pdf.getVersion());
		Bukkit.getConsoleSender().sendMessage("§eStatus: §4Offline");
		Bukkit.getConsoleSender().sendMessage("§7§m-----------------------");
	}

	void register() {
		Bukkit.getPluginManager().registerEvents((Listener) new InteractAtEntityListener(), (Plugin) this);

		getCommand("shop").setExecutor((CommandExecutor) new ShopCommand());
	}

	public static Shop getInstance() {
		return instance;
	}
}
