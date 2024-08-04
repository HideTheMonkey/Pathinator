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

package com.hidethemonkey.pathinator;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class PathinatorConfig {
    private final FileConfiguration config;
    private String pluginName = "";

    public static final String ENABLE_STATS = "enableStats";

    /**
     * @param fileConf
     */
    public PathinatorConfig(FileConfiguration fileConf) {
        this.config = fileConf;
        config.addDefault(ENABLE_STATS, true);
        config.options().copyDefaults(true);
    }

    /**
     *
     * @return
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     *
     * @param name
     */
    public void setPluginName(String name) {
        pluginName = name;
    }

    /**
     * Gets the enable stats configuration value.
     *
     * @return true if stats are enabled, false otherwise
     */
    public boolean getEnableStats() {
        return config.getBoolean(ENABLE_STATS);
    }

    /**
     * Gets the clearance height configuration value.
     *
     * @return the clearance height
     */
    public int getClearance() {
        return config.getInt("clearance.height");
    }

    /**
     * Gets the lighting stack configuration value.
     *
     * @return the lighting stack as a list of strings
     */
    public List<String> getLightingStack() {
        return config.getStringList("lighting.stack");
    }

    /**
     * Gets the lighting interval configuration value.
     *
     * @return the lighting interval
     */
    public int getLightingInterval() {
        return config.getInt("lighting.interval");
    }

    /**
     * Gets the powered rail interval configuration value.
     *
     * @return the powered rail interval
     */
    public int getPoweredInterval() {
        return config.getInt("tracks.powerInterval");
    }

    /**
     * Gets the clearance material configuration value.
     *
     * @return the clearance material as a string
     */
    public String getClearanceMaterial() {
        return config.getString("clearance.material");
    }

    /**
     * Gets the take tool damage configuration value.
     *
     * @return true if tool damage is enabled, false otherwise
     */
    public boolean getTakeToolDamage() {
        return config.getBoolean("survival.toolDamage");
    }

    /**
     * Gets the enabled in survival configuration value.
     *
     * @return true if enabled in survival, false otherwise
     */
    public boolean getEnabledInSurvival() {
        return config.getBoolean("survival.enabled");
    }

    /**
     * Gets the keep material configuration value.
     *
     * @return true if keep material, false otherwise
     */
    public boolean getKeepMaterial() {
        return config.getBoolean("survival.keepMaterial");
    }

    /**
     * Gets the require tool configuration value.
     *
     * @return true if require tool, false otherwise
     */
    public boolean getRequireTool() {
        return config.getBoolean("survival.requireTool");
    }

    /**
     * Updates the configuration file.
     *
     * @param plugin the JavaPlugin instance
     */
    public static void updateConfig(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            // Nothing to update...
            return;
        }
        YamlConfiguration ymlConfig = YamlConfiguration.loadConfiguration(file);
        String configVersion = ymlConfig.getString("pluginVersion");
        if (configVersion == null || configVersion.isBlank()) {
            configVersion = "unknown";
        }
        String currentVersion = plugin.getDescription().getVersion();
        if (!currentVersion.equals(configVersion)) {
            // backup current config
            File sourceFile = new File(plugin.getDataFolder(), file.getName());
            File destFile = new File(plugin.getDataFolder() + "/config." + configVersion + ".yml");

            plugin.getLogger().info("Found mismatched plugin version, updating config...");
            plugin.getLogger().info("old: " + configVersion + ", new: " + currentVersion);
            plugin.getLogger().info("Backing up config.yml to " + destFile.getName());
            if (sourceFile.renameTo(destFile)) {
                plugin.saveResource(file.getName(), true);
                try {
                    FileConfiguration fileConfig = plugin.getConfig();
                    fileConfig.load(file);
                    Set<String> keys = ymlConfig.getKeys(true);
                    Object newValue;
                    Object oldValue;
                    plugin.getLogger().info("Restoring previous configuration settings...");
                    for (String key : keys) {
                        oldValue = fileConfig.get(key);
                        newValue = ymlConfig.get(key);
                        if (!key.equals("pluginVersion") &&
                                !(newValue instanceof MemorySection) &&
                                newValue != null &&
                                !newValue.equals(oldValue) &&
                                fileConfig.contains(key)) {
                            plugin.getLogger().info("Updating " + key + " from " + oldValue + " to " + newValue);
                            fileConfig.set(key, newValue);
                        }
                    }
                    fileConfig.save(file);
                    plugin.getLogger().info("Completed updating config.yml to latest version!");
                } catch (IOException | InvalidConfigurationException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error processing updated config.yml", e);
                    // Something went wrong, so make sure the current config is saved. This could
                    // overwrite the current settings, but it's safer to have a current config.yml.
                    plugin.saveResource(file.getName(), true);
                }
            } else {
                plugin.getLogger().warning("Failed to backup config.yml");
            }
        }
    }
}
