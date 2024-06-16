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
    public static final String WITH_LIGHTS = "with lights";
    public static final String WITH_POWER = "with power";

    protected Pathinator plugin;
    protected PathinatorConfig config;

    public PathCommands(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
        this.config = pathPlugin.getPConfig();
    }

    protected Integer getDistance(CommandArguments args) {
        return (Integer) args.getOrDefault(DISTANCE, 0);
    }

    protected boolean getWithLights(CommandArguments args) {
        return (boolean) args.getOrDefault(WITH_LIGHTS, false);
    }

    protected boolean getWithPower(CommandArguments args) {
        return (boolean) args.getOrDefault(WITH_POWER, false);
    }

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

    protected Block findTargetBlock(BlockHelper blockHelper, PlayerHelper playerHelper) {
        Block block = blockHelper.getBlockUnderPlayer(playerHelper.getPlayer());
        Material blockMaterial = block.getBlockData().getMaterial();
        if (blockMaterial == Material.AIR && playerHelper.isInSurvival()) {
            playerHelper.msg("Found a block of AIR. Please stand on a solid block to place a path.");
            return null;
        }
        return block;
    }

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

    public abstract void createPath(CommandSender sender, CommandArguments args);

}