/*
 * MIT License
 *
 * Copyright (c) 2024 HideTheMonkey
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hidethemonkey.pathinator.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.hidethemonkey.pathinator.Pathinator;

import org.bukkit.inventory.meta.Damageable;

public class PlayerHelper {

    Player player;
    Pathinator plugin;

    public PlayerHelper(Player player, Pathinator plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    /**
     * Get the player's game mode
     * 
     * @return
     */
    public String getGameMode() {
        return player.getGameMode().name();
    }

    /**
     * Return if the player is in survival mode
     * 
     * @return
     */
    public boolean isInSurvival() {
        return getGameMode().equals("SURVIVAL");
    }

    /**
     * Return if the player is in adventure mode
     * 
     * @return
     */
    public boolean isInAdventure() {
        return getGameMode().equals("ADVENTURE");
    }

    /**
     * Return if the player is in spectator mode
     * 
     * @return
     */
    public boolean isInSpectator() {
        return getGameMode().equals("SPECTATOR");
    }

    /**
     * Return if the player is in creative mode
     * 
     * @param block
     * @return
     */
    public boolean isInCreative() {
        return getGameMode().equals("CREATIVE");
    }

    /**
     * Check if the player is required to have the right tool
     * to mine a block.
     * 
     * @return
     */
    public boolean requiresTools() {
        return plugin.getPConfig().getRequireTool();
    }

    /**
     * Get the number of blocks in the player's inventory
     * 
     * @param block
     * @return
     */
    public int getItemCount(final Block block) {
        int count = 0;
        if (player.getInventory().contains(block.getType())) {
            for (ItemStack items : player.getInventory().getContents()) {
                if (items != null && items.getType().equals(block.getType())) {
                    count += items.getAmount();
                }
            }
        }
        return count;
    }

    /**
     * Check if the player has the tool in their inventory
     * Returns the tool with the least amount of durability remaining.
     * 
     * @param type
     * @return
     */
    public ItemStack getMineableTool(Material material) {
        String type = "";
        if (Tag.MINEABLE_PICKAXE.isTagged(material)) {
            type = "PICKAXE";
        } else if (Tag.MINEABLE_SHOVEL.isTagged(material)) {
            type = "SHOVEL";
        } else if (Tag.MINEABLE_AXE.isTagged(material)) {
            type = "AXE";
        } else if (Tag.MINEABLE_HOE.isTagged(material)) {
            type = "HOE";
        }

        if (type.isEmpty()) {
            // If the material doesn't match with a tool, return null
            // so we can handle that differently.
            return null;
        }

        PlayerInventory inventory = player.getInventory();
        List<ItemStack> tools = new ArrayList<ItemStack>();
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType().name().endsWith(type)) {
                tools.add(item);
            }
        }
        if (!tools.isEmpty()) {
            // Sort tools by least amount of remaining durability
            Comparator<ItemStack> comp = (ItemStack a, ItemStack b) -> {
                int acomp = a.getType().getMaxDurability() - ((Damageable) a.getItemMeta()).getDamage();
                int bcomp = b.getType().getMaxDurability() - ((Damageable) b.getItemMeta()).getDamage();
                return acomp - bcomp;
            };
            Collections.sort(tools, comp);

            return tools.get(0);
        }
        // Player doesn't have a tool so pass back what it should be but with 0 amount.
        return new ItemStack(Material.getMaterial("MINEABLE_" + type), 0);
    }

    /**
     * Add damage to the item
     * 
     * @param item
     * @param damage
     */
    public void addToolDamage(ItemStack item, int damage) {
        if (item != null && plugin.getPConfig().getTakeToolDamage()) {
            String toolName = item.getType().name();
            if (!toolName.contains("AXE") && !toolName.contains("SHOVEL") && !toolName.contains("HOE")) {
                return;
            }
            Damageable damageMeta = (Damageable) item.getItemMeta();
            damageMeta.setDamage(damageMeta.getDamage() + damage);
            item.setItemMeta(damageMeta);

            if (item.getType().getMaxDurability() <= damageMeta.getDamage() + damage) {
                player.getInventory().remove(item);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 3.0F, 0.69F); // giggity giggity
                msg("Your " + toolName + " broke!");
            }
        }
    }

    /**
     * Check if the player has the block in their inventory
     * 
     * @param material
     * @return
     */
    public boolean hasBlock(Material material) {
        if (isInSurvival()) {
            return player.getInventory().contains(material) || material.isAir();
        }
        return true;
    }

    /**
     * Remove the block from the player's inventory
     * 
     * @param material
     * @return
     */
    public boolean removeBlock(Material material) {
        if (player.getInventory().contains(material)) {
            HashMap<Integer, ItemStack> items = player.getInventory().removeItem(new ItemStack(material, 1));
            return items.isEmpty();
        }
        return true;
    }

    /**
     * Give the player a block
     * 
     * @param material
     * @return
     */
    public boolean giveBlock(Material material) {
        if (plugin.getPConfig().getKeepMaterial()) {
            HashMap<Integer, ItemStack> items = player.getInventory().addItem(new ItemStack(material, 1));
            return items.isEmpty();
        }
        return true;
    }

    /**
     * Send a message to the player
     * 
     * @param message
     */
    public void msg(String message) {
        player.sendMessage("[" + plugin.getName() + "]: " + message);
    }
}
