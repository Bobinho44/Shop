package de.elias177.shop.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem {
	int slot;
	int price;
	ItemStack item;
	Material priceType;

	public ShopItem(int slot, ItemStack item, Material priceType, int price) {
		this.slot = slot;
		this.item = item;
		this.priceType = priceType;
		this.price = price;
	}

	public int getSlot() {
		return this.slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public Material getPriceType() {
		return this.priceType;
	}

	public void setPriceType(Material priceType) {
		this.priceType = priceType;
	}
}