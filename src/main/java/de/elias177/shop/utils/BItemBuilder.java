package de.elias177.shop.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Bobinho item builder library
 */
public final class BItemBuilder {

    /**
     * Fields
     */
    private ItemStack item;
    private final ItemMeta meta;

    /**
     * Creates the item builder
     *
     * @param item the item stack
     */
    public BItemBuilder(ItemStack item) {

        //Configures the bukkit item stack
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    /**
     * Creates the item builder
     *
     * @param material the material
     */
    public BItemBuilder(Material material) {

        //Configures the bukkit item stack
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Creates the item builder
     *
     * @param material the material
     * @param amount   the amount
     */
    public BItemBuilder(Material material, int amount) {

        //Configure bukkit item stack.
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Gets the current item stack
     *
     * @return the item stack
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Sets the item stack
     *
     * @param item the item stack
     * @return the item builder
     */
    public BItemBuilder item(ItemStack item) {

        this.item = item.clone();

        return this;
    }

    /**
     * Sets the item material
     *
     * @param material the material
     * @return the item Builder
     */
    public BItemBuilder material(Material material) {

        item.setType(material);

        return this;
    }

    /**
     * Sets the item durability
     *
     * @param durability the durability
     * @return the item builder
     */
    public BItemBuilder durability(int durability) {

        //Checks if the item is not damageable
        if (!(meta instanceof Damageable)) {
            return this;
        }

        //Sets item durability.
        ((Damageable) meta).setDamage(durability);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the item amount
     *
     * @param amount the amount
     * @return the item builder
     */
    public BItemBuilder amount(int amount) {
        item.setAmount(amount);

        return this;
    }

    /**
     * Sets the item name
     *
     * @param name the name
     * @return the item builder
     */
    public BItemBuilder name(String name) {

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Clears the item lore
     *
     * @return the item builder
     */
    public BItemBuilder clearLore() {

        //Sets the item lore
        meta.setLore(null);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the item lore
     *
     * @param lore the lore
     * @return the item builder
     */
    public BItemBuilder setLore(List<String> lore) {

        //Sets the item lore
        meta.setLore(lore);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param lore the lore
     * @return the item builder
     */
    public BItemBuilder lore(List<String> lore) {

        //Sets the item lore
        meta.setLore(Stream.concat(
                Optional.ofNullable(meta.getLore()).orElse(Collections.emptyList()).stream(),
                lore.stream()
        ).toList());
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param lore the lore
     * @return the item builder
     */
    public BItemBuilder lore(String... lore) {

        lore(List.of(lore));

        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param color the color
     * @param lore  the lore
     * @return the item builder
     */
    public BItemBuilder lore(String color, List<String> lore) {

        //Sets the item lore
        meta.setLore(lore.stream()
                .map(line -> color + line)
                .toList());

        return this;
    }

    /**
     * Adds a new enchantment
     *
     * @param enchantment the enchantment
     * @param level       the level
     * @return the item builder
     */
    public BItemBuilder addEnchantment(Enchantment enchantment, int level) {

        item.addUnsafeEnchantment(enchantment, level);

        return this;
    }

    /**
     * Removes an enchantment
     *
     * @param enchantment the enchantment
     * @return the item builder
     */
    public BItemBuilder removeEnchantment(Enchantment enchantment) {

        item.removeEnchantment(enchantment);

        return this;
    }

    /**
     * Sets the item unbreakable
     *
     * @param unbreakable the unbreakable statue
     * @return the item builder
     */
    public BItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Adds an item flag
     *
     * @param flag the flag
     * @return the item builder
     */
    public BItemBuilder addFlag(ItemFlag... flag) {

        meta.addItemFlags(flag);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Removes an item flag
     *
     * @param flag the flag
     * @return the item builder
     */
    public BItemBuilder removeFlag(ItemFlag... flag) {

        meta.removeItemFlags(flag);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the item color
     *
     * @param red   the red
     * @param green the green
     * @param blue  the blue
     * @return the item builder
     */
    public BItemBuilder color(int red, int green, int blue) {

        //Checks if item is colorable
        if (!(meta instanceof LeatherArmorMeta)) {
            return this;
        }

        //Colorizes the item
        ((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Sets the item glow
     *
     * @return the item builder
     */
    public BItemBuilder glow() {
        addFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        addEnchantment(Enchantment.DURABILITY, 1);

        return this;
    }

    /**
     * Removes the item glow
     *
     * @return the item builder
     */
    public BItemBuilder unglow() {
        removeFlag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        removeEnchantment(Enchantment.DURABILITY);

        return this;
    }

    /**
     * Sets the item custom model data
     *
     * @return the item builder
     */
    public BItemBuilder customModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);

        return this;
    }

    /**
     * Builds the item
     *
     * @return the item stack
     */
    public ItemStack build() {
        return item;
    }

}