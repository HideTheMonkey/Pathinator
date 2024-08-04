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

package com.hidethemonkey.pathinator.commands;

import dev.jorel.commandapi.executors.CommandArguments;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;
import com.hidethemonkey.pathinator.helpers.SegmentData;

public class BasicCommands extends PathCommands {

    public BasicCommands(Pathinator pathPlugin) {
        super(pathPlugin);
    }

    /**
     * 
     * @param sender
     * @param args
     */

    @Override
    public void createPath(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;

        // init some helpers
        PlayerHelper playerHelper = new PlayerHelper(player, plugin);
        BlockHelper blockHelper = new BlockHelper(plugin);

        // Check if the player is in a supported game mode
        if (!modeCheck(playerHelper)) {
            return;
        }

        // Get the block under the player
        Block targetBlock = findTargetBlock(blockHelper, playerHelper);
        if (targetBlock == null) {
            return;
        }

        BlockFace facing = player.getFacing();

        int inventoryCount = playerHelper.getItemCount(targetBlock);
        Integer blockCount = getDistance(args);
        if (blockCount <= 0) {
            return;
        }

        // copy the original count
        int requestedCount = blockCount;

        // Don't allow more blocks to be placed than are in the player's inventory
        if (playerHelper.isInSurvival() && blockCount > inventoryCount) {
            blockCount = inventoryCount; // update count
        }

        final ArrayList<ItemStack> lightingStack = getLightingStack(args, playerHelper);

        Location placedLocation = targetBlock.getLocation().clone();

        for (int i = 0; i < blockCount; i++) {
            placedLocation = blockHelper.adjustLocationForward(placedLocation, facing);

            SegmentData segmentData = new SegmentData();
            segmentData.setWorld(player.getWorld());
            segmentData.setBaseFacing(facing);
            segmentData.setBaseMaterial(targetBlock.getBlockData().getMaterial());
            segmentData.setBaseLocation(placedLocation);
            segmentData.setClearance(config.getClearance());
            segmentData.setClearanceMaterial(Material.getMaterial(config.getClearanceMaterial()));

            if (getWithLights(args) && i != 0 && (i % config.getLightingInterval()) == 0) {
                segmentData.addLightingStacks(lightingStack);
                segmentData.addLighting();
            }

            // This is where the magic happens
            blockHelper.placeBlock(segmentData, i, playerHelper);
        }

        if (requestedCount != blockCount) {
            playerHelper.msg(
                    "Requested " + requestedCount + " blocks of " + targetBlock.getType().name()
                            + ", but only able to place "
                            + blockCount + ".");
        } else {
            String prefix = playerHelper.isInSurvival() ? "Attempting to place " : "Placed ";
            playerHelper.msg(prefix + blockCount + " blocks of " + targetBlock.getType().name() + ".");
        }
    }

}