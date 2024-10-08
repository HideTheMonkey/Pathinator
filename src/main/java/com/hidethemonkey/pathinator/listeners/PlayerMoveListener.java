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

package com.hidethemonkey.pathinator.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.helpers.BlockHelper;
import com.hidethemonkey.pathinator.helpers.FollowRegistry;

public class PlayerMoveListener implements Listener {

    private Pathinator plugin;
    private FollowRegistry followRegistry;
    private BlockHelper blockHelper;

    public PlayerMoveListener(Pathinator plugin, FollowRegistry followRegistry) {
        this.plugin = plugin;
        this.followRegistry = followRegistry;
        this.blockHelper = new BlockHelper(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (followRegistry.size() > 0 && followRegistry.isRegistered(e.getPlayer())) {
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                Player player = e.getPlayer();
                Block block = blockHelper.getBlockUnderPlayer(player);
                if (block.getType().isSolid()) {
                    int radius = followRegistry.getRadius(player);
                    Material material = followRegistry.getMaterial(player);
                    Bukkit.getScheduler().runTaskLater(this.plugin, task -> {
                        blockHelper.setBlocksInRadius(block, radius, material);
                    }, 1);
                }
            }
        }
    }
}
