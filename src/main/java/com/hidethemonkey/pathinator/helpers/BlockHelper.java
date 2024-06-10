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
     * Mine and replace a block
     * 
     * @param toPlace
     * @param block
     * @param ph
     * @return
     */
    private boolean mineAndReplace(Material toPlace, Block block, PlayerHelper ph) {
        Material toRemove = block.getType();
        if (toRemove != toPlace && ph.hasBlock(toPlace)) {
            if (ph.isInSurvival()) {
                // Handle tool damage
                if (!toRemove.isAir() && toRemove != Material.WATER && toRemove.getHardness() >= 0.5) {
                    ItemStack tool = ph.getMineableTool(toRemove);
                    if (tool != null && tool.getAmount() == 0 && ph.requiresTools()) {
                        // Don't allow the block to be placed if the player doesn't have the right tool
                        // ph.msg("You need a tool to mine " + toRemove);
                        return false;
                    }
                    // Apply damage to the appropriate tool in inventory
                    ph.addToolDamage(tool, 1);
                }
                // Always remove from inventory
                ph.removeBlock(toPlace);
                // Add the mined material to the inventory
                ph.giveBlock(toRemove);
            }
            block.setType(toPlace);
        }
        return true;
    }

    /**
     * Place a block at the specified location
     * 
     * @param data
     * @param delay
     * @param playerHelper
     */
    public void placeBlock(final SegmentData data, final int delay, final PlayerHelper playerHelper) {
        Location location = data.getBaseLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Block baseBlock = data.getWorld().getBlockAt(x, y, z);

        // Schedule the block placement
        Bukkit.getScheduler().runTaskLater(this.plugin, task -> {

            // Place the base block
            mineAndReplace(data.getBaseMaterial(), baseBlock, playerHelper);

            // Clear the air...
            int clearance = data.getClearance();
            for (int i = 1; i <= clearance; i++) {
                Block airBlock = data.getWorld().getBlockAt(x, y + i, z);
                if (!mineAndReplace(data.getClearanceMaterial(), airBlock, playerHelper)) {
                    continue;
                }
            }

            // Add some lights
            if (data.getUseLighting()) {
                Location lightingLocation = data.getLightingLocation();
                // Make sure it has a base to stand on
                Block lightingBase = data.getWorld().getBlockAt(lightingLocation);
                if (!mineAndReplace(data.getBaseMaterial(), lightingBase, playerHelper)) {
                    return;
                }

                // Loop through the lighting stack and place the lighting materials
                for (int i = 0; i < data.getLightingStacks().size(); i++) {
                    Block lightBlock = data.getWorld().getBlockAt(lightingLocation.getBlockX(),
                            lightingLocation.getBlockY() + i + 1,
                            lightingLocation.getBlockZ());
                    Material lighting = data.getLightingStacks().get(i).getType();
                    if (!mineAndReplace(lighting, lightBlock, playerHelper)) {
                        continue;
                    }
                }
            }

        }, delay);

        // Add Rails
        if (data.getUseRails()) {
            Bukkit.getScheduler().runTaskLater(this.plugin, task -> {
                // railBlock is the block above the just placed base block
                // and as such should always be AIR
                Block railBlock = data.getWorld().getBlockAt(x, y + 1, z);
                Material railMaterial = data.getUsePower() ? Material.POWERED_RAIL : Material.RAIL;
                mineAndReplace(railMaterial, railBlock, playerHelper);

                // Add power if needed
                if (data.getUsePower()) {
                    Location powerLocation = data.getPowerLocation();
                    // Make sure the REDSTONE_TORCH has a base to stand on
                    Block baseForRedstoneTorch = data.getWorld().getBlockAt(powerLocation);
                    if (!mineAndReplace(data.getBaseMaterial(), baseForRedstoneTorch, playerHelper)) {
                        return;
                    }
                    Block redstoneTorchBlock = data.getWorld().getBlockAt(powerLocation.getBlockX(),
                            powerLocation.getBlockY() + 1,
                            powerLocation.getBlockZ());
                    if (!mineAndReplace(Material.REDSTONE_TORCH, redstoneTorchBlock, playerHelper)) {
                        return;
                    }
                }
            }, delay + 2);
        }
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
        switch (facing) {
            case WEST:
                location.setX(location.getX() - 1);
                break;
            case EAST:
                location.setX(location.getX() + 1);
                break;
            case NORTH:
                location.setZ(location.getZ() - 1);
                break;
            case SOUTH:
                location.setZ(location.getZ() + 1);
                break;
            default:
                break;
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
        BlockFace newFacing;

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
