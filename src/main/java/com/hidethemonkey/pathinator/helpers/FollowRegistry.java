package com.hidethemonkey.pathinator.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FollowRegistry {

    record FollowState(int radius, Material material) {}

    private Map<UUID, FollowState> registry;

    public FollowRegistry() {
        registry = new HashMap<>();
    }

    public void register(Player player, Integer radius, Material material) {
        registry.put(player.getUniqueId(), new FollowState(radius, material));
    }

    public void remove(Player player) {
        registry.remove(player.getUniqueId());
    }

    public Integer getRadius(Player player) {
        FollowState state = registry.get(player.getUniqueId());
        return state != null ? state.radius() : null;
    }

    public Material getMaterial(Player player) {
        FollowState state = registry.get(player.getUniqueId());
        return state != null ? state.material() : null;
    }

    public boolean isRegistered(Player player) {
        return registry.containsKey(player.getUniqueId());
    }

    public int size() {
        return registry.size();
    }
}
