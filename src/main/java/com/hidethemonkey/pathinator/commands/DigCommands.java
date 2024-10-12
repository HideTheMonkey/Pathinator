package com.hidethemonkey.pathinator.commands;

import dev.jorel.commandapi.executors.CommandArguments;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.PlayerHelper;
import com.hidethemonkey.pathinator.helpers.SegmentData;

public class DigCommands extends PathCommands {

    public DigCommands(Pathinator pathPlugin) {
        super(pathPlugin);
    }

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
        Integer distance = getDistance(args);
        Integer height = getHeight(args, config.getClearance() - 1);

        Enum<?> digDirection = getDigDirection(args);

        Location startingLocation = targetBlock.getLocation().clone().add(0, 1, 0);

        if (digDirection == DigDirection.VUP || digDirection == DigDirection.VDOWN) {
            startingLocation = blockHelper.adjustLocationForward(startingLocation, facing);
            height = -1;
        }

        for (int i = 0; i < distance; i++) {
            if (digDirection != DigDirection.VUP && digDirection != DigDirection.VDOWN) {
                startingLocation = blockHelper.adjustLocationForward(startingLocation, facing);
            }

            if (digDirection == DigDirection.UP) {
                startingLocation = startingLocation.add(0, 1, 0);
            } else if (digDirection == DigDirection.DOWN) {
                startingLocation = startingLocation.subtract(0, 1, 0);
            } else if (digDirection == DigDirection.VUP && i > 0) {
                startingLocation = startingLocation.add(0, 1, 0);
            } else if (digDirection == DigDirection.VDOWN && i > 0) {
                startingLocation = startingLocation.subtract(0, 1, 0);
            }

            SegmentData segmentData = new SegmentData();
            segmentData.setWorld(player.getWorld());
            segmentData.setBaseFacing(facing);
            segmentData.setBaseLocation(startingLocation.clone());
            segmentData.setClearance(height);

            blockHelper.digBlocks(segmentData, i, playerHelper);
        }
    }

}
