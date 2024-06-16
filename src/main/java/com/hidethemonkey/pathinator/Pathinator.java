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

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.hidethemonkey.pathinator.commands.PathCommands;
import com.hidethemonkey.pathinator.commands.BasicCommands;
import com.hidethemonkey.pathinator.commands.CustomCommands;
import com.hidethemonkey.pathinator.commands.TrackCommands;
import com.hidethemonkey.pathinator.helpers.StringUtils;
import com.hidethemonkey.pathinator.helpers.VersionChecker;
import com.hidethemonkey.pathinator.helpers.VersionData;

public class Pathinator extends JavaPlugin {

    private PathinatorConfig pConfig;
    private Metrics metrics;
    private VersionData versionData;

    /**
     * 
     */
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false));
        versionData = VersionChecker.getLatestReleaseVersion();
    }

    /**
    *
    */
    @Override
    public void onEnable() {
        // Do this first, in case we're upgrading from an older version
        PathinatorConfig.updateConfig(this);
        saveDefaultConfig();

        pConfig = new PathinatorConfig(getConfig());

        // Check for new versions
        compareVersions();

        // Store name on config for easy access later (not saved to file)
        pConfig.setPluginName(this.getName());

        // Initialize bStats metrics
        setupMetrics(pConfig);

        // Register Player Join Listener
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        CommandAPI.onEnable();

        // Create basic path command
        BasicCommands basic = new BasicCommands(this);
        new CommandAPICommand(PathCommands.BASIC)
                .withAliases("pb")
                .withArguments(new IntegerArgument(PathCommands.DISTANCE))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_LIGHTS))
                .executesPlayer(basic::createPath)
                .register();

        // Create track path command
        TrackCommands track = new TrackCommands(this);
        new CommandAPICommand(PathCommands.TRACKS)
                .withAliases("pt")
                .withArguments(new IntegerArgument(PathCommands.DISTANCE))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_POWER))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_LIGHTS))
                .executesPlayer(track::createPath)
                .register();

        // Create custom path command
        // CustomCommands custom = new CustomCommands(this, config);
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        getServer().getScheduler().cancelTasks(this);
    }

    /**
     * 
     * @return
     */
    public PathinatorConfig getPConfig() {
        return pConfig;
    }

    /**
     * 
     * @return bstats metrics object
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * 
     * @param config
     */
    private void setupMetrics(PathinatorConfig config) {
        if (metrics == null) {
            // Init bStats if it's enabled
            if (config.getEnableStats()) {
                // Please don't change the ID. This helps me keep track of generic usage data.
                // The uploaded stats do not include any private information.
                metrics = new Metrics(this, 21949);

                metrics.addCustomChart(new SimplePie("system_language", () -> {
                    return System.getProperty("user.language") + "_" + System.getProperty("user.country").toUpperCase();
                }));

                metrics.addCustomChart(
                        new SimplePie("config_clearance_height", () -> String.valueOf(pConfig.getClearance())));

                metrics.addCustomChart(
                        new SimplePie("config_clearance_material", () -> pConfig.getClearanceMaterial()));

                metrics.addCustomChart(new SimplePie("config_lighting_interval",
                        () -> String.valueOf(pConfig.getLightingInterval())));

                metrics.addCustomChart(
                        new SimplePie("config_lighting_stack", () -> pConfig.getLightingStack().toString()));

                metrics.addCustomChart(new SimplePie("config_survival_enabled",
                        () -> pConfig.getEnabledInSurvival() ? "true" : "false"));

                metrics.addCustomChart(new SimplePie("config_require_tool",
                        () -> pConfig.getRequireTool() ? "true" : "false"));

                metrics.addCustomChart(new SimplePie("config_tool_damage",
                        () -> pConfig.getTakeToolDamage() ? "true" : "false"));

                metrics.addCustomChart(new SimplePie("config_keep_material",
                        () -> pConfig.getKeepMaterial() ? "true" : "false"));

                metrics.addCustomChart(new SimplePie("config_powered_interval",
                        () -> Integer.toString(pConfig.getPoweredInterval())));
            } else {
                getLogger().warning(
                        "bStats is not enabled! Please consider activating this service to help me keep track of Pathinator usage. 🙇");
            }
        }
    }

    /**
     * 
     */
    private void compareVersions() {
        if (versionData == null) {
            getLogger().warning(
                    "Could not check for new versions. Please see https://hangar.papermc.io/HideTheMonkey/Pathinator for updates.");
            return;
        }
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(versionData.getVersion());
        DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(getDescription().getVersion());
        if (latestVersion.compareTo(currentVersion) > 0) {
            getLogger().warning("**************************************************************");
            getLogger().warning("* A new version of Pathinator is available!");
            getLogger().warning("*");
            getLogger().warning("* New version: " + versionData.getVersion());
            getLogger().warning("* Your version: " + getDescription().getVersion());
            getLogger().warning("*");
            getLogger().warning("* Please update to take advantage of the latest features and bug fixes.");
            getLogger().warning("* Download here: https://hangar.papermc.io/HideTheMonkey/Pathinator");
            getLogger().warning("**************************************************************");
        }
    }

    public class PlayerJoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            getMetrics().addCustomChart(
                    new SimplePie("player_locale",
                            () -> String.valueOf(StringUtils.formatLocale(event.getPlayer().getLocale()))));
        }
    }
}