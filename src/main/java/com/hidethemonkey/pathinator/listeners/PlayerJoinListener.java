package com.hidethemonkey.pathinator.listeners;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.commands.PathCommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerJoinListener implements Listener {

    // command hints keyed by the permission required to use that command
    private static final Map<String, String[]> COMMAND_HINTS = new LinkedHashMap<>();
    static {
        COMMAND_HINTS.put(PathCommands.PERM_BASIC,
                new String[] { "/pb <distance>", "lay a quick path in the direction you're facing" });
        COMMAND_HINTS.put(PathCommands.PERM_TRACKS,
                new String[] { "/pt <distance>", "lay a rail track" });
        COMMAND_HINTS.put(PathCommands.PERM_CUSTOM,
                new String[] { "/pc <distance> <width> <height>", "lay a custom sized path" });
        COMMAND_HINTS.put(PathCommands.PERM_FOLLOW,
                new String[] { "/pf start", "lay a path automatically as you walk" });
        COMMAND_HINTS.put(PathCommands.PERM_DIG,
                new String[] { "/pd <up|down|ahead|vup|vdown> <distance>", "dig out a path" });
    }

    // players who've already seen the hint this server session
    private static final Set<UUID> notified = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Pathinator plugin;

    public PlayerJoinListener(Pathinator plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getMetrics().addCustomChart(
                new SimplePie("player_locale",
                        () -> String.valueOf(event.getPlayer().locale().toString())));

        Player player = event.getPlayer();
        if (notified.add(player.getUniqueId())) {
            sendWelcomeHint(player);
        }
    }

    private void sendWelcomeHint(Player player) {
        for (Map.Entry<String, String[]> hint : COMMAND_HINTS.entrySet()) {
            if (player.hasPermission(hint.getKey())) {
                String command = hint.getValue()[0];
                String description = hint.getValue()[1];
                player.sendMessage(
                        Component.text("[" + plugin.getName() + "]: ", NamedTextColor.DARK_AQUA)
                                .append(Component.text("Try ", NamedTextColor.GRAY))
                                .append(Component.text(command, NamedTextColor.GOLD))
                                .append(Component.text(" - " + description, NamedTextColor.GRAY)));
            }
        }
    }
}
