package com.hidethemonkey.pathinator.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BlockStateArgument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import net.kyori.adventure.text.Component;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.PathinatorConfig;
import com.hidethemonkey.pathinator.helpers.FollowRegistry;
import com.hidethemonkey.pathinator.listeners.PlayerMoveListener;

public class CommandRegistrar {

    public static void register(Pathinator plugin, PathinatorConfig config, FollowRegistry followRegistry) {
        BasicCommands basic = new BasicCommands(plugin);
        new CommandAPICommand(PathCommands.BASIC)
                .withAliases("pb")
                .withArguments(new IntegerArgument(PathCommands.DISTANCE))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_LIGHTS))
                .executesPlayer((PlayerCommandExecutor) basic::createPath)
                .register();

        TrackCommands track = new TrackCommands(plugin);
        new CommandAPICommand(PathCommands.TRACKS)
                .withAliases("pt")
                .withArguments(new IntegerArgument(PathCommands.DISTANCE))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_POWER))
                .withOptionalArguments(new BooleanArgument(PathCommands.WITH_LIGHTS))
                .executesPlayer((PlayerCommandExecutor) track::createPath)
                .register();

        CustomCommands custom = new CustomCommands(plugin);
        new CommandAPICommand(PathCommands.CUSTOM)
                .withAliases("pc")
                .withArguments(new IntegerArgument(PathCommands.DISTANCE))
                .withArguments(new IntegerArgument(PathCommands.WIDTH))
                .withArguments(new IntegerArgument(PathCommands.HEIGHT))
                .withOptionalArguments(new BlockStateArgument(PathCommands.PATH_MATERIAL))
                .withOptionalArguments(new BlockStateArgument(PathCommands.CLEARANCE_MATERIAL))
                .executesPlayer((PlayerCommandExecutor) custom::createPath)
                .register();

        if (config.getFollowEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(
                    new PlayerMoveListener(plugin, followRegistry), plugin);
            FollowCommands follow = new FollowCommands(plugin, followRegistry);
            new CommandTree(PathCommands.FOLLOW).withAliases("pf")
                    .thenNested(new LiteralArgument(PathCommands.START),
                            new IntegerArgument(
                                    PathCommands.RADIUS,
                                    PathinatorConfig.MIN_RADIUS, PathinatorConfig.MAX_RADIUS),
                            new BlockStateArgument(PathCommands.PATH_MATERIAL)
                                    .executesPlayer((PlayerCommandExecutor) follow::createPath))
                    .thenNested(new LiteralArgument(PathCommands.START),
                            new IntegerArgument(PathCommands.RADIUS, PathinatorConfig.MIN_RADIUS,
                                    PathinatorConfig.MAX_RADIUS)
                                    .executesPlayer((PlayerCommandExecutor) follow::createPath))
                    .then(new LiteralArgument(
                            PathCommands.START)
                            .executesPlayer((PlayerCommandExecutor) follow::createPath))
                    .then(new LiteralArgument(
                            PathCommands.STOP)
                            .executesPlayer((PlayerCommandExecutor) follow::stopFollowing))
                    .register();
        }

        DigCommands dig = new DigCommands(plugin);
        new CommandTree(PathCommands.DIG).withAliases("pd")
                .then(new StringArgument(
                        PathCommands.UP)
                        .then(new IntegerArgument(PathCommands.DISTANCE)
                                .executesPlayer((PlayerCommandExecutor) dig::createPath)))
                .then(new StringArgument(
                        PathCommands.DOWN)
                        .then(new IntegerArgument(PathCommands.DISTANCE)
                                .executesPlayer((PlayerCommandExecutor) dig::createPath)))
                .then(new StringArgument(
                        PathCommands.AHEAD)
                        .then(new IntegerArgument(PathCommands.DISTANCE)
                                .executesPlayer((PlayerCommandExecutor) dig::createPath)))
                .then(new StringArgument(
                        PathCommands.VUP)
                        .then(new IntegerArgument(PathCommands.DISTANCE)
                                .executesPlayer((PlayerCommandExecutor) dig::createPath)))
                .then(new StringArgument(
                        PathCommands.VDOWN)
                        .then(new IntegerArgument(PathCommands.DISTANCE)
                                .executesPlayer((PlayerCommandExecutor) dig::createPath)))
                .register();

        new CommandAPICommand("pathinator")
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("pathinator.admin")
                        .executesPlayer((PlayerCommandExecutor) (sender, args) -> {
                            plugin.reloadPlugin();
                            sender.sendMessage(Component.text("[" + plugin.getName() + "]: Config reloaded."));
                        }))
                .register();
    }
}
