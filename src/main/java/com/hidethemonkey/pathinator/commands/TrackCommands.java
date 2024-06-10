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
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.PathinatorConfig;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;
import com.hidethemonkey.pathinator.helpers.SegmentData;

public class TrackCommands {

    private Pathinator plugin;
    private PathinatorConfig config;

    public TrackCommands(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
        this.config = pathPlugin.getPConfig();
    }

    /**
     * 
     * @param sender
     * @param args
     */
    public void tracksPath(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        PlayerHelper playerHelper = new PlayerHelper(player, plugin);

        // Check if the player is in the correct game mode
        if (playerHelper.isInAdventure() || playerHelper.isInSpectator()) {
            playerHelper.msg("Pathinator does not work in " + playerHelper.getGameMode() + " mode.");
            return;
        }
        // Check if we're enabled in survival mode
        if (playerHelper.isInSurvival() && !config.getEnabledInSurvival()) {
            playerHelper.msg("Pathinator is disabled in survival mode.");
            return;
        }

        BlockHelper blockHelper = new BlockHelper(plugin);

        // Find the block under the player
        Block block = blockHelper.getBlockUnderPlayer(player);
        int inventoryCount = playerHelper.getItemCount(block);
        Material blockMaterial = block.getBlockData().getMaterial();
        if (blockMaterial == Material.AIR && playerHelper.isInSurvival()) {
            playerHelper.msg("Standing on AIR. Please stand on a solid block to place a path.");
            return;
        }

        Location blockLocation = block.getLocation();

        BlockFace facing = player.getFacing();

        Integer blockCount = (Integer) args.getOrDefault(PathCommands.DISTANCE, 0);
        if (blockCount <= 0) {
            return;
        }
        int requestedCount = blockCount;

        // Don't allow more blocks to be placed than are in the player's inventory
        if (playerHelper.isInSurvival() && blockCount > inventoryCount) {
            blockCount = inventoryCount;
        }

        Boolean withLights = args.getByClass(PathCommands.WITH_LIGHTS, Boolean.class);
        withLights = withLights != null ? withLights : false;

        ArrayList<ItemStack> lightingStacks = new ArrayList<ItemStack>();
        if (withLights) {
            int clearance = config.getClearance();
            List<String> configuredStack = config.getLightingStack();
            for (int i = 0; i < configuredStack.size(); i++) {
                // Don't allow the lighting stack to exceed the clearance height in survival
                if (playerHelper.isInSurvival()) {
                    --clearance;
                }
                if (clearance >= 0) {
                    lightingStacks.add(new ItemStack(Material.getMaterial(configuredStack.get(i))));
                }
            }
        }

        int lightingInterval = config.getLightingInterval();
        Location placedLocation = blockLocation.clone();

        Boolean withPower = args.getByClass(PathCommands.WITH_POWER, Boolean.class);
        withPower = withPower != null ? withPower : false;
        int poweredInterval = config.getPoweredInterval();

        for (int i = 0; i < blockCount; i++) {
            blockHelper.adjustLocationForward(placedLocation, facing);

            SegmentData segmentData = new SegmentData();
            segmentData.setBaseFacing(facing);
            segmentData.setBaseMaterial(blockMaterial);
            segmentData.setWorld(player.getWorld());
            segmentData.setClearance(config.getClearance());
            segmentData.setClearanceMaterial(Material.getMaterial(config.getClearanceMaterial()));
            segmentData.setBaseLocation(placedLocation);
            segmentData.setUseRails(true);

            if (withPower && i != 0 && i % poweredInterval == 0) {
                segmentData.addPower();
            }

            if (withLights && i != 0 && i % lightingInterval == 0) {
                segmentData.addLightingStacks(lightingStacks);
                segmentData.addLighting();
            }

            // This is where the magic happens
            blockHelper.placeBlock(segmentData, i, playerHelper);
        }
        if (requestedCount != blockCount) {
            playerHelper.msg(
                    "Requested " + requestedCount + " blocks of " + block.getType().name() + ", but only able to place "
                            + blockCount + ".");
        } else {
            String prefix = playerHelper.isInSurvival() ? "Attempting to place " : "Placed ";
            String suffix = withPower ? " with powered RAILS." : " with RAILS.";
            playerHelper.msg(prefix + blockCount + " blocks of " + block.getType().name() + suffix);
        }
    }
}
