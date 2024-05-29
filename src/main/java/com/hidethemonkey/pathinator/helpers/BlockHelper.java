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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import com.hidethemonkey.pathinator.Pathinator;

public class BlockHelper {

    Plugin plugin;

    public BlockHelper(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
    }

    /**
     * Get the block under the player
     * 
     * @param player
     * @return
     */
    public Block getBlockUnderPlayer(Player player) {
        BoundingBox bb = player.getBoundingBox();
        Location min = new Location(player.getWorld(), bb.getMinX(), bb.getMinY() - .02, bb.getMinZ());
        Block minBlock = min.getBlock();
        // standing close to the edge?, try to find the block behind the player's
        // current position.
        if (minBlock.getType() == Material.AIR) {
            plugin.getLogger().info("Player is standing on air, trying to find block behind them...");
            BlockFace bf = player.getFacing().getOppositeFace();
            min = min.add(bf.getModX(), bf.getModY() - .02, bf.getModZ());
            return min.getBlock();
        }
        return min.getBlock();
    }

    /**
     * Place a block at the specified location
     * 
     * @param data
     * @param delay
     * @param playerHelper
     */
    public void placeBlock(final SegmentData data, final int delay, PlayerHelper playerHelper) {
        Location location = data.getBaseLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Block block = data.getWorld().getBlockAt(x, y, z);

        // Schedule the block placement
        Bukkit.getScheduler().runTaskLater(this.plugin, task -> {

            Material originalMaterial = block.getType();

            // Place the base block
            if (originalMaterial != data.getBaseMaterial() && playerHelper.hasBlock(data.getBaseMaterial())) {
                if (playerHelper.isInSurvival()) {
                    ItemStack tool = playerHelper.getMineableTool(originalMaterial);
                    if (tool == null && playerHelper.requiresTools()) {
                        // Don't allow the block to be placed if the player doesn't have the right tool
                        return;
                    }
                    // Remove from inventory
                    playerHelper.removeBlock(data.getBaseMaterial());
                    // Apply damage to the appropriate tool in inventory
                    playerHelper.addToolDamage(tool, 1);
                    // Add the mined material to the inventory
                    playerHelper.giveBlock(originalMaterial);
                }
                block.setType(data.getBaseMaterial());
            }

            // Clear the air...
            int clearance = data.getClearance();
            for (int i = 1; i <= clearance; i++) {
                Block blockToClear = data.getWorld().getBlockAt(x, y + i, z);
                Material materialToClear = blockToClear.getType();
                if (materialToClear != Material.AIR && materialToClear != Material.CAVE_AIR
                        && materialToClear != Material.WATER && playerHelper.isInSurvival()) {
                    ItemStack tool = playerHelper.getMineableTool(materialToClear);
                    if (tool == null && playerHelper.requiresTools()) {
                        // Don't allow the block to be placed if the player doesn't have the right tool
                        continue;
                    }
                    // Apply damage to the appropriate tool in inventory
                    playerHelper.addToolDamage(tool, 1);
                    // Add the mined material to the inventory
                    playerHelper.giveBlock(materialToClear);
                }
                blockToClear.setType(data.getClearanceMaterial());
            }

            // Add some lights
            if (data.getUseLighting()) {
                Location lightingLocation = data.getLightingLocation();
                // Make sure it has a base to stand on
                Block lightingBase = data.getWorld().getBlockAt(lightingLocation.getBlockX(),
                        lightingLocation.getBlockY(),
                        lightingLocation.getBlockZ());
                Material originalLightingBase = lightingBase.getType();
                if (originalLightingBase != data.getBaseMaterial() && playerHelper.hasBlock(data.getBaseMaterial())) {
                    if (playerHelper.isInSurvival()) {
                        ItemStack tool = playerHelper.getMineableTool(originalLightingBase);
                        if (tool == null && playerHelper.requiresTools()) {
                            // Don't allow the block to be placed if the player doesn't have the right tool
                            return;
                        }
                        // Remove from inventory
                        playerHelper.removeBlock(data.getBaseMaterial());
                        // Apply damage to the appropriate tool in inventory
                        playerHelper.addToolDamage(tool, 1);
                        // Add the mined material to the inventory
                        playerHelper.giveBlock(originalLightingBase);
                    }
                    lightingBase.setType(data.getBaseMaterial());
                }

                // Loop through the lighting stack and place the lighting materials
                for (int i = 0; i < data.getLightingStacks().size(); i++) {
                    Block lightBlock = data.getWorld().getBlockAt(lightingLocation.getBlockX(),
                            lightingLocation.getBlockY() + i + 1,
                            lightingLocation.getBlockZ());
                    Material lighting = data.getLightingStacks().get(i).getType();
                    if (playerHelper.isInSurvival()) {
                        if (playerHelper.hasBlock(lighting)) {
                            lightBlock.setType(lighting);
                            // Remove from inventory
                            playerHelper.removeBlock(lighting);
                        }
                    } else {
                        lightBlock.setType(lighting);
                    }

                }
            }

        }, delay);
    }

    /**
     * Increment/Decrement the location based on the facing direction
     * 
     * EAST is positive X
     * WEST is negative X
     * NORTH is negative Z
     * SOUTH is positive Z
     * 
     * @param location
     * @param facing
     */
    public void adjustLocationForward(Location location, BlockFace facing) {
        if (facing == BlockFace.EAST || facing == BlockFace.WEST) {
            if (facing == BlockFace.WEST) {
                location.setX(location.getX() - 1);
            } else {
                location.setX(location.getX() + 1);
            }
        }

        if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
            if (facing == BlockFace.SOUTH) {
                location.setZ(location.getZ() + 1);
            } else {
                location.setZ(location.getZ() - 1);
            }
        }
    }

    /**
     * Rotate the facing direction 90 degrees
     * 
     * @param facing
     * @return
     */
    public static BlockFace rotate90(BlockFace facing) {
        return rotate90(facing, false);
    }

    /**
     * Rotate the facing direction 90 degrees
     * 
     * @param facing
     * @param counterClockwise
     * @return
     */
    public static BlockFace rotate90(BlockFace facing, boolean counterClockwise) {
        BlockFace newFacing = facing;

        switch (facing) {
            case EAST:
                newFacing = counterClockwise ? BlockFace.NORTH : BlockFace.SOUTH;
                break;
            case WEST:
                newFacing = counterClockwise ? BlockFace.SOUTH : BlockFace.NORTH;
                break;
            case NORTH:
                newFacing = counterClockwise ? BlockFace.WEST : BlockFace.EAST;
                break;
            case SOUTH:
                newFacing = counterClockwise ? BlockFace.EAST : BlockFace.WEST;
                break;
            default:
                newFacing = facing;
                break;
        }

        return newFacing;
    }
}
