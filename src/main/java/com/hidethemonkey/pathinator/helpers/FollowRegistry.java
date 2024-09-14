package com.hidethemonkey.pathinator.helpers;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FollowRegistry {
    private Map<UUID, Integer> players;
    private Map<UUID, Material> materials;

    public FollowRegistry() {
        Properties props = new Properties();
        int MAX_PLAYERS = 20;
        try {
            props.load(new FileInputStream("server.properties"));
            MAX_PLAYERS = Integer.parseInt(props.getProperty("max-players"));
        } catch (Exception e) {
            // ignore
        }
        players = new HashMap<>(MAX_PLAYERS);
        materials = new HashMap<>(MAX_PLAYERS);
    }

    public void register(Player player, Integer radius, Material material) {
        players.put(player.getUniqueId(), radius);
        materials.put(player.getUniqueId(), material);
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
        materials.remove(player.getUniqueId());
    }

    public Integer getRadius(Player player) {
        return players.get(player.getUniqueId());
    }

    public Material getMaterial(Player player) {
        return materials.get(player.getUniqueId());
    }

    public boolean isRegistered(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public int size() {
        return players.size();
    }
}
