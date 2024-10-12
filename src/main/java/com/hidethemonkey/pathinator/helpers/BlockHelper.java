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
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import com.hidethemonkey.pathinator.Pathinator;

public class BlockHelper {

    Plugin plugin;
    int[][] fortunes = new int[3][4];

    public BlockHelper(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
        fortunes[0] = new int[] { 66, 33, 0, 0 };
        fortunes[1] = new int[] { 50, 25, 25, 0 };
        fortunes[2] = new int[] { 40, 20, 20, 20 };
    }

    /**
     * Makes a decent effort to find the block under the player,
     * while avoiding AIR blocks.
     * 
     * @param player
     * @return
     */
    public Block findBlockUnderPlayer(Player player) {
        Block minBlock = getBlockUnderPlayer(player);
        // standing close to the edge?, try to find the block behind the player's
        // current position.
        if (minBlock.getType().isAir()) {
            plugin.getLogger().info("Player is standing on air, trying to find block behind them...");
            BlockFace bf = player.getFacing().getOppositeFace();
            Location min = minBlock.getLocation().add(bf.getModX(), bf.getModY() - .02, bf.getModZ());
            return min.getBlock();
        }
        return minBlock;
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
        return min.getBlock();
    }

    /**
     * Get the space occupied by the player
     * 
     * @param player
     * @return
     */
    public BoundingBox getPlayerSpace(Block targetBlock) {
        Location loc = targetBlock.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return new BoundingBox(x, y, z, x + 1, y + 3, z + 1);
    }

    /**
     * Calculate the amount of drops per the fortune level
     * 
     * @param level
     * @return
     */
    public int getFortuneDrops(int level) {
        int numberOfDrops = 1;

        // Ensure fortune level is within a valid range:
        // (We only support natural levels 1-3)
        if (level < 1) {
            level = 1;
        } else if (level > 3) {
            level = 3;
        }

        // adjust for array indexing
        level--;

        int baseChance = fortunes[level][0];
        int firstChance = fortunes[level][1];
        int secondChance = fortunes[level][2];
        int thirdChance = fortunes[level][3];

        int chance = (int) (Math.random() * 100);
        if (chance < baseChance) {
            numberOfDrops = 1;
        } else if (chance > baseChance && chance < (baseChance + firstChance)) {
            numberOfDrops = 2;
        } else if (chance > (baseChance + firstChance) && chance < (baseChance + firstChance + secondChance)) {
            numberOfDrops = 3;
        } else if (chance > (baseChance + firstChance + secondChance) && chance < (baseChance + firstChance
                + secondChance + thirdChance)) {
            numberOfDrops = 4;
        }
        return numberOfDrops;
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
        if (toPlace == null || toRemove != toPlace && ph.hasBlock(toPlace)) {
            if (ph.isInSurvival()) {
                ItemStack tool = ph.getMineableTool(toRemove);
                // ** Handle tool damage **
                if (!toRemove.isAir() && toRemove != Material.WATER && toRemove.getHardness() >= 0.5) {
                    if (tool != null && tool.getAmount() == 0 && ph.requiresTools()) {
                        // Don't allow the block to be placed if the player doesn't have the right tool
                        return false;
                    }
                    // Apply damage to the appropriate tool in inventory
                    ph.addToolDamage(tool);
                }
                if (toPlace != null) {
                    // ** remove from inventory **
                    ph.removeBlock(toPlace);
                }
                if (tool != null) {
                    ItemMeta meta = tool.getItemMeta();
                    // handle SILK_TOUCH
                    if (meta.hasEnchant(Enchantment.SILK_TOUCH)) {
                        // Don't try to figure out all the conditions,
                        // just give the player the block already, geeze.
                        ph.giveBlock(toRemove, 1);
                    }
                    // handle FORTUNE
                    else if (meta.hasEnchant(Enchantment.FORTUNE)) {
                        int fortuneDropCount = getFortuneDrops(tool.getEnchantmentLevel(Enchantment.FORTUNE));

                        // ** Add the mined material to the inventory **
                        block.getDrops(tool).forEach(drop -> {
                            int drops = drop.getAmount() <= fortuneDropCount ? fortuneDropCount : drop.getAmount();
                            ph.giveBlock(drop.getType(), drops);
                        });
                    }
                    // default no enchantments
                    else {
                        // ** Add the mined material to the inventory **
                        block.getDrops(tool).forEach(drop -> ph.giveBlock(drop.getType(), drop.getAmount()));
                    }
                }
            }
            block.setType(toPlace != null ? toPlace : Material.AIR);
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

        // Schedule the block placement
        Bukkit.getScheduler().runTaskLater(this.plugin, task -> {
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Block targetBlock = data.getWorld().getBlockAt(x, y, z);

            // Place the base block
            Material baseMaterial = data.getBaseMaterial();
            if (data.getCurrentSection() == SegmentData.Section.CENTER) {
                mineAndReplace(baseMaterial, targetBlock, playerHelper);
            } else {
                ArrayList<Material> sideMaterials = data.getCurrentSection() == SegmentData.Section.RIGHT
                        ? data.getRightMaterials()
                        : data.getLeftMaterials();
                Material sideMaterial;
                try {
                    sideMaterial = sideMaterials.get(data.getSideIndex());
                } catch (IndexOutOfBoundsException e) {
                    sideMaterial = data.getBaseMaterial();
                }
                mineAndReplace(sideMaterial, targetBlock, playerHelper);
            }

            // Clear the air...
            int clearance = data.getClearance();
            for (int i = 1; i <= clearance; i++) {
                Block airBlock = data.getWorld().getBlockAt(x, y + i, z);
                if (data.getCurrentSection() == SegmentData.Section.CENTER && delay < 4) {
                    if (!data.getNegativeSpace().contains(airBlock.getLocation().toVector())) {
                        if (!mineAndReplace(data.getClearanceMaterial(), airBlock, playerHelper)) {
                            continue;
                        }
                    }
                } else {
                    if (!mineAndReplace(data.getClearanceMaterial(), airBlock, playerHelper)) {
                        continue;
                    }
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
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
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

    public void digBlocks(SegmentData data, int delay, PlayerHelper playerHelper) {

        // Schedule the block placement
        Bukkit.getScheduler().runTaskLater(this.plugin, task -> {
            Location location = data.getBaseLocation();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            Block targetBlock = data.getWorld().getBlockAt(x, y, z);

            mineAndReplace(null, targetBlock, playerHelper);

            // Clear the air...
            int clearance = data.getClearance();
            for (int i = 0; i <= clearance; i++) {
                Block airBlock = data.getWorld().getBlockAt(x, y + i, z);
                if (!mineAndReplace(null, airBlock, playerHelper)) {
                    continue;
                }
            }

        }, delay);
    }

    /**
     * Get the materials to either side of the target block
     * 
     * @param block
     * @param facing
     * @param blocks
     * @return
     */
    public ArrayList<Material> getSideMaterials(Block block, BlockFace facing, int blocks) {
        ArrayList<Material> materials = new ArrayList<Material>();
        for (int i = 1; i <= blocks; i++) {
            Block sideBlock = block.getRelative(facing, i);
            materials.add(sideBlock.getType());
        }
        return materials;
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
    public Location adjustLocationForward(Location location, BlockFace facing) {
        Location tmpLocation = location.clone();
        switch (facing) {
            case WEST:
                tmpLocation.setX(location.getX() - 1);
                break;
            case EAST:
                tmpLocation.setX(location.getX() + 1);
                break;
            case NORTH:
                tmpLocation.setZ(location.getZ() - 1);
                break;
            case SOUTH:
                tmpLocation.setZ(location.getZ() + 1);
                break;
            default:
                break;
        }
        return tmpLocation;
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

    /**
     * Get a list of blocks in the Y plane around a start block, given a specific radius
     * Will not include blocks of the same material as the provided material
     * 
     * Inspired by https://www.spigotmc.org/threads/tutorial-getting-blocks-in-a-cube-radius.64981/#post-717133
     * 
     * @param start
     * @param radius
     * @param material
     * @return
     */
    public List<Block> findBlocksInRadius(Block start, int radius, Material material) {
        int iterations = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<Block>(iterations * iterations);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block toAdd = start.getRelative(x, 0, z);
                Material type = toAdd.getType();
                if (!type.isSolid() || type.equals(material)) {
                    continue;
                }
                blocks.add(toAdd);
            }
        }
        return blocks;
    }

    /**
     * Set blocks in a radius around a start block to a specific material
     * 
     * @param block
     * @param radius
     * @param material
     */
    public void setBlocksInRadius(Block block, int radius, Material material) {
        List<Block> blocks = findBlocksInRadius(block, radius, material);
        for (Block b : blocks) {
            b.setType(material);
        }
    }
}
