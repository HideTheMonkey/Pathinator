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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.PathinatorConfig;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;

public abstract class PathCommands {

    // primary commands
    public static final String BASIC = "path:basic";
    public static final String CUSTOM = "path:custom";
    public static final String TRACKS = "path:tracks";

    // parameters
    public static final String DISTANCE = "distance";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String PATH_MATERIAL = "path material";
    public static final String CLEARANCE_MATERIAL = "clearance material";
    public static final String WITH_LIGHTS = "with lights";
    public static final String WITH_POWER = "with power";

    protected Pathinator plugin;
    protected PathinatorConfig config;

    /**
     * Constructor for PathCommands.
     *
     * @param pathPlugin The Pathinator plugin instance.
     */
    public PathCommands(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
        this.config = pathPlugin.getPConfig();
    }

    /**
     * Gets the distance argument from the command arguments.
     *
     * @param args The command arguments.
     * @return The distance argument, or 0 if not provided.
     */
    protected Integer getDistance(CommandArguments args) {
        return (Integer) args.getOrDefault(DISTANCE, 0);
    }

    /**
     * Gets the width argument from the command arguments.
     *
     * @param args The command arguments.
     * @return The width argument, or 1 if not provided.
     */
    protected Integer getWidth(CommandArguments args) {
        return (Integer) args.getOrDefault(WIDTH, 1);
    }

    /**
     * Gets the height argument from the command arguments.
     *
     * @param args          The command arguments.
     * @param defaultHeight The default height.
     * @return The height argument, or the default height if not provided.
     */
    protected Integer getHeight(CommandArguments args, int defaultHeight) {
        return (Integer) args.getOrDefault(HEIGHT, defaultHeight);
    }

    /**
     * Gets the path material from the command arguments.
     *
     * @param args       The command arguments.
     * @param targetData The target block data.
     * @return The path material.
     */
    protected Material getPathMaterial(CommandArguments args, BlockData targetData) {
        return ((BlockData) args.getOrDefault(PATH_MATERIAL, targetData)).getMaterial();
    }

    /**
     * Gets the clearance material from the command arguments.
     *
     * @param args           The command arguments.
     * @param configMaterial The config material.
     * @return The clearance material.
     */
    protected Material getClearanceMaterial(CommandArguments args, String configMaterial) {
        BlockData clearanceData = (BlockData) args.get(CLEARANCE_MATERIAL);
        if (clearanceData != null) {
            return clearanceData.getMaterial();
        }
        return Material.getMaterial(configMaterial);
    }

    /**
     * Gets the withLights argument from the command arguments.
     *
     * @param args The command arguments.
     * @return The withLights argument, or false if not provided.
     */
    protected boolean getWithLights(CommandArguments args) {
        return (boolean) args.getOrDefault(WITH_LIGHTS, false);
    }

    /**
     * Gets the withPower argument from the command arguments.
     *
     * @param args The command arguments.
     * @return The withPower argument, or false if not provided.
     */
    protected boolean getWithPower(CommandArguments args) {
        return (boolean) args.getOrDefault(WITH_POWER, false);
    }

    /**
     * Checks if the player is in a supported game mode.
     *
     * @param playerHelper The player helper instance.
     * @return True if the player is in the correct game mode, false otherwise.
     */
    protected boolean modeCheck(PlayerHelper playerHelper) {
        // Check if the player is in the correct game mode
        if (playerHelper.isInAdventure() || playerHelper.isInSpectator()) {
            playerHelper.msg("Pathinator does not work in " + playerHelper.getGameMode() + " mode.");
            return false;
        }
        // Check if we're enabled in survival mode
        if (playerHelper.isInSurvival() && !config.getEnabledInSurvival()) {
            playerHelper.msg("Pathinator is disabled in survival mode.");
            return false;
        }
        return true;
    }

    /**
     * Finds the target block for the path.
     *
     * @param blockHelper  The block helper instance.
     * @param playerHelper The player helper instance.
     * @return The target block, or null if not found.
     */
    protected Block findTargetBlock(BlockHelper blockHelper, PlayerHelper playerHelper) {
        Block block = blockHelper.getBlockUnderPlayer(playerHelper.getPlayer());
        Material blockMaterial = block.getBlockData().getMaterial();
        if (blockMaterial.isAir() && playerHelper.isInSurvival()) {
            playerHelper.msg("Found a block of AIR. Please stand on a solid block to place a path.");
            return null;
        }
        return block;
    }

    /**
     * Gets the lighting stack for the path.
     *
     * @param args         The command arguments.
     * @param playerHelper The player helper instance.
     * @return The lighting stack.
     */
    protected ArrayList<ItemStack> getLightingStack(CommandArguments args, PlayerHelper playerHelper) {
        ArrayList<ItemStack> lightingStack = new ArrayList<ItemStack>();
        if (getWithLights(args)) {
            int clearance = config.getClearance();
            List<String> configuredStack = config.getLightingStack();
            for (int i = 0; i < configuredStack.size(); i++) {
                // Don't allow the lighting stack to exceed the clearance height in survival
                if (playerHelper.isInSurvival()) {
                    --clearance;
                }
                if (clearance >= 0) {
                    Material material = Material.getMaterial(configuredStack.get(i));
                    if (material != null) {
                        lightingStack.add(new ItemStack(material));
                    }
                }
            }
        }
        return lightingStack;
    }

    /**
     * Creates a path.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     */
    public abstract void createPath(CommandSender sender, CommandArguments args);

}