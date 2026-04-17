package com.hidethemonkey.pathinator.listeners;

import org.bstats.charts.SimplePie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hidethemonkey.pathinator.Pathinator;

public class PlayerJoinListener implements Listener {

    private final Pathinator plugin;

    public PlayerJoinListener(Pathinator plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getMetrics().addCustomChart(
                new SimplePie("player_locale",
                        () -> String.valueOf(event.getPlayer().locale().toString())));
    }
}
