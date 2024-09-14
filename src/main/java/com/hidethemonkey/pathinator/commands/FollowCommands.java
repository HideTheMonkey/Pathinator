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

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;
import com.hidethemonkey.pathinator.helpers.FollowRegistry;

public class FollowCommands extends PathCommands {

    FollowRegistry followRegistry;

    /**
     * Constructor ensures we use the same instance of the FollowRegistry
     * 
     * @param pathPlugin
     * @param followRegistry
     */
    public FollowCommands(Pathinator pathPlugin, FollowRegistry followRegistry) {
        super(pathPlugin);
        this.followRegistry = followRegistry;
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
        if (!playerHelper.isInCreative()) {
            playerHelper.msg("/" + PathCommands.FOLLOW + " does not work in " + playerHelper.getGameMode() + " mode.");
            return;
        }

        // Get the block under the player
        Block targetBlock = findTargetBlock(blockHelper, playerHelper);
        if (targetBlock == null) {
            playerHelper.msg("Unable to find appropriate block to follow.");
            return;
        }
        Material requestedMaterial = getPathMaterial(args, targetBlock.getBlockData());
        if (requestedMaterial == null || !requestedMaterial.isSolid() || !requestedMaterial.isBlock()) {
            playerHelper.msg("Invalid path material (" + requestedMaterial + ") specified.");
            return;
        }

        // Register player with the movement listener
        followRegistry.register(player, getRadius(args), requestedMaterial);
    }

    /**
     * 
     * @param sender
     * @param args
     */
    public void stopFollowing(CommandSender sender, CommandArguments args) {
        Player player = (Player) sender;
        followRegistry.remove(player);
    }
}
