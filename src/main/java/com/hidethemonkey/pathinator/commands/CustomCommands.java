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

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;
import com.hidethemonkey.pathinator.helpers.SegmentData;
import com.hidethemonkey.pathinator.helpers.SegmentData.Section;

public class CustomCommands extends PathCommands {

    Pathinator plugin;

    public CustomCommands(Pathinator pathPlugin) {
        super(pathPlugin);
        this.plugin = pathPlugin;
    }

    /**
     * 
     * @param sender
     * @param args
     */
    @Override
    public void createPath(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        PlayerHelper playerHelper = new PlayerHelper(player, plugin);

        // Check if the player is in a supported game mode
        if (!modeCheck(playerHelper)) {
            return;
        }

        if (!playerHelper.isInCreative()) {
            playerHelper.msg("The custom path command is only available in creative mode.");
            return;
        }

        BlockHelper blockHelper = new BlockHelper(plugin);

        // Get the block under the player
        Block targetBlock = findTargetBlock(blockHelper, playerHelper);
        if (targetBlock == null) {
            return;
        }

        Integer distance = getDistance(args);
        if (distance < 0) {
            return;
        }

        // Get the custom parameters
        Integer width = getWidth(args);
        Integer height = getHeight(args, config.getClearance());
        Material pathMaterial = getPathMaterial(args, targetBlock.getBlockData());
        Material clearanceMaterial = getClearanceMaterial(args, config.getClearanceMaterial());

        Location placedLocation = targetBlock.getLocation().clone();
        BlockFace facing = player.getFacing();

        ArrayList<Material> leftMaterials = new ArrayList<Material>();
        ArrayList<Material> rightMaterials = new ArrayList<Material>();

        int index = 0;
        if (width > 1) {
            // This ensures the block player is standing on is the starting point for width
            // greater than 1
            index = -1;
            // This ensures the total length matches the distance requested if width is
            // greater than 1
            if (distance > 0) {
                distance--;
            }

            int blocksRight = Math.round((width - 1) / 2);
            int blocksLeft = width - blocksRight - 1;
            // If the path material is not provided, find the pattern of blocks to the
            // left and right
            if (args.get(PATH_MATERIAL) == null) {
                // store material to left and right of player
                leftMaterials = blockHelper.getSideMaterials(targetBlock,
                        BlockHelper.rotate90(facing, true),
                        blocksLeft);
                rightMaterials = blockHelper.getSideMaterials(targetBlock,
                        BlockHelper.rotate90(facing, false),
                        blocksRight);
            }
            // otherwise, fill the left and right materials with the path material
            else {
                leftMaterials.ensureCapacity(blocksLeft);
                for (int i = 0; i < blocksLeft; i++) {
                    leftMaterials.add(pathMaterial);
                }
                rightMaterials.ensureCapacity(blocksRight);
                for (int i = 0; i < blocksRight; i++) {
                    rightMaterials.add(pathMaterial);
                }
            }
        }

        for (int i = index; i < distance; i++) {
            if (i >= 0) {
                placedLocation = blockHelper.adjustLocationForward(placedLocation, facing);
            }
            SegmentData segmentData = new SegmentData();
            segmentData.setWorld(player.getWorld());
            segmentData.setBaseFacing(facing);
            segmentData.setBaseMaterial(pathMaterial);
            segmentData.setBaseLocation(placedLocation);
            segmentData.setClearance(height);
            segmentData.setClearanceMaterial(clearanceMaterial);
            segmentData.setLeftMaterials(leftMaterials);
            segmentData.setRightMaterials(rightMaterials);
            segmentData.setNegativeSpace(blockHelper.getPlayerSpace(targetBlock));

            // This is where the magic happens
            blockHelper.placeBlock(segmentData, i, playerHelper);

            if (width > 1) {
                int blocksRight = Math.round((width - 1) / 2);
                int blocksLeft = width - blocksRight - 1;

                SegmentData rightSegmentData = new SegmentData(segmentData);
                Location rightLocation = placedLocation.clone();
                BlockFace rightFacing = BlockHelper.rotate90(facing, false);
                rightSegmentData.setBaseFacing(rightFacing);
                rightSegmentData.setCurrentSection(Section.RIGHT);

                for (int j = 0; j < blocksRight; j++) {
                    rightLocation = blockHelper.adjustLocationForward(rightLocation, rightFacing);
                    rightSegmentData.setBaseLocation(rightLocation);
                    rightSegmentData.setSideIndex(j);
                    blockHelper.placeBlock(new SegmentData(rightSegmentData), i, playerHelper);
                }

                SegmentData leftSegmentData = new SegmentData(segmentData);
                Location leftLocation = placedLocation.clone();
                BlockFace leftFacing = BlockHelper.rotate90(facing, true);
                leftSegmentData.setBaseFacing(leftFacing);
                leftSegmentData.setCurrentSection(Section.LEFT);

                for (int k = 0; k < blocksLeft; k++) {
                    leftLocation = blockHelper.adjustLocationForward(leftLocation, leftFacing);
                    leftSegmentData.setBaseLocation(leftLocation);
                    leftSegmentData.setSideIndex(k);
                    blockHelper.placeBlock(new SegmentData(leftSegmentData), i, playerHelper);
                }
            }
        }

        if (pathMaterial.name().equals(clearanceMaterial.name())) {
            playerHelper.msg("Placed " + (distance * width * (height + 1)) + " blocks of " + pathMaterial.name() + ".");
        } else if (width > 1) {
            playerHelper.msg("Placed " + (distance * width) + " blocks on the path and " + (distance * width * height)
                    + " blocks in the air.");
        } else {
            playerHelper.msg("Placed " + (distance * width) + " blocks of " + pathMaterial.name() + ".");
        }

    }
}
