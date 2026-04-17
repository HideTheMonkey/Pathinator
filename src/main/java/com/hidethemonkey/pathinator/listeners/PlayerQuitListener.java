package com.hidethemonkey.pathinator.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.hidethemonkey.pathinator.helpers.FollowRegistry;

public class PlayerQuitListener implements Listener {

    private final FollowRegistry followRegistry;

    public PlayerQuitListener(FollowRegistry followRegistry) {
        this.followRegistry = followRegistry;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        followRegistry.remove(event.getPlayer());
    }
}
