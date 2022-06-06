package me.chaotisch3r.lobby.util;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright © TaskID, All Rights Reserved
 * Chaotisch3r has the permission to use this class
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:18 20.04.2022
 **/

public class ItemBuilder {

    private final ItemStack itemStack;
    private ArrayList<String> itemLore = new ArrayList<>();
    private ItemMeta itemMeta;
    private SkullMeta skullMeta;
    private Inventory inventory;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int shortid) {
        this.itemStack = new ItemStack(material, 1, (short) shortid);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, String displayName) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
        assert this.itemMeta != null;
        this.itemMeta.setDisplayName(displayName);
    }

    public ItemBuilder(Material material, String displayName, int shortid) {
        this.itemStack = new ItemStack(material, 1, (short) shortid);
        this.itemMeta = this.itemStack.getItemMeta();
        assert this.itemMeta != null;
        this.itemMeta.setDisplayName(displayName);
    }

    public ItemBuilder(Material material, String displayName, DyeColor shortid) {
        this.itemStack = new ItemStack(material, 1, shortid.getWoolData());
        this.itemMeta = this.itemStack.getItemMeta();
        assert this.itemMeta != null;
        this.itemMeta.setDisplayName(displayName);
    }

    public ItemBuilder(Player player) {
        this.itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
        this.skullMeta = (SkullMeta) itemStack.getItemMeta();
        assert this.skullMeta != null;
        this.skullMeta.setOwningPlayer(player);
    }

    public ItemBuilder(Player player, String displayName) {
        this.itemStack = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
        this.skullMeta = (SkullMeta) itemStack.getItemMeta();
        assert this.skullMeta != null;
        this.skullMeta.setOwningPlayer(player);
        this.skullMeta.setDisplayName(displayName);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
        if (itemMeta.getLore() == null) return;
        this.itemLore = (ArrayList<String>) this.itemMeta.getLore();
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setSkullName(String skullName) {
        if (this.skullMeta == null) return null;
        this.skullMeta.setDisplayName(skullName);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public ItemBuilder addFlags(List<ItemFlag> itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            this.itemMeta.addItemFlags(itemFlag);
        }
        return this;
    }

    public ItemBuilder addLore(String lore) {
        this.itemLore.add(lore);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean b) {
        this.itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemStack get() {
        if (skullMeta != null) {
            if (!this.itemLore.isEmpty()) this.skullMeta.setLore(itemLore);
            this.itemStack.setItemMeta(this.skullMeta);
            return itemStack;
        }
        if (!this.itemLore.isEmpty()) this.itemMeta.setLore(itemLore);
        this.itemStack.setItemMeta(this.itemMeta);
        return itemStack;
    }

    public ItemBuilder build(Inventory inventory, int line, int slot) {
        this.inventory = inventory;
        while (line > 0) {
            line--;
            slot = slot + 9;
        }
        if (slot == -1) {
            this.inventory.addItem(get());
        } else {
            this.inventory.setItem(slot, get());
        }
        return this;
    }

    public ItemBuilder build(List<Inventory> inventories, int line, int slot) {
        while (line > 0) {
            line--;
            slot = slot + 9;
        }
        Iterator<Inventory> iterator = inventories.iterator();
        while (iterator.hasNext()) {
            if (slot != -1) {
                iterator.next().setItem(slot, get());
            } else {
                iterator.next().addItem(get());
            }
        }
        return this;
    }

    public ItemBuilder build(List<Inventory> inventories, List<Integer> slots) {
        Iterator<Inventory> iterator = inventories.iterator();
        Iterator<Integer> iterator1 = slots.iterator();
        while (iterator.hasNext()) {
            while (iterator1.hasNext()) {
                if (iterator1.next() != -1) {
                    iterator.next().setItem(iterator1.next(), get());
                } else {
                    iterator.next().addItem(get());
                }
            }
        }
        return this;
    }

    public ItemBuilder build(List<Inventory> inventories, int slot) {
        Iterator<Inventory> iterator = inventories.iterator();
        while (iterator.hasNext()) {
            if (slot != -1) {
                iterator.next().setItem(slot, get());
            } else {
                iterator.next().addItem(get());
            }
        }
        return this;
    }

    public ItemBuilder build(Inventory inventory, List<Integer> slots) {
        Iterator<Integer> iterator = slots.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() != -1) {
                inventory.setItem(iterator.next(), get());
            } else {
                inventory.addItem(get());
            }
        }
        return this;
    }

    public ItemBuilder build(Inventory inventory, int slot) {
        this.inventory = inventory;
        if (slot == -1) {
            this.inventory.addItem(get());
        } else {
            this.inventory.setItem(slot, get());
        }
        return this;
    }

}
