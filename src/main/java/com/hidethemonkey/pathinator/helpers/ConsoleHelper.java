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

package com.hidethemonkey.pathinator.helpers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleHelper {
    private static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();

    public static void logMessage(String message) {
        CONSOLE.sendMessage("");
        CONSOLE.sendMessage(Component.text(message, NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage("");
    }

    public static void sendNewVersionNotice(String newVersion, String currentVersion) {
        CONSOLE.sendMessage("");
        CONSOLE.sendMessage(Component.text("************************************************************************",
                NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage(Component.text("* ",
                NamedTextColor.DARK_AQUA).append(
                        Component.text(
                                "A new version of "))
                .append(Component.text("Pathinator", Style.style(
                        TextDecoration.BOLD)))
                .append(Component.text(" is available!")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage(Component.text("* ", NamedTextColor.DARK_AQUA)
                .append(Component.text("Current Version: " + currentVersion)));
        CONSOLE.sendMessage(Component.text("* ", NamedTextColor.DARK_AQUA)
                .append(Component.text("New Version: " + newVersion, Style.style(TextDecoration.BOLD))));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage(Component.text("* ", NamedTextColor.DARK_AQUA)
                .append(Component.text("Please update to take advantage of the latest features and bug fixes.")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage(Component.text("* ", NamedTextColor.DARK_AQUA)
                .append(Component.text("Download from your preferred site:")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA)
                .append(Component.text("     Hangar: https://hangar.papermc.io/HideTheMonkey/Pathinator")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA)
                .append(Component.text("     Modrinth: https://modrinth.com/plugin/pathinator")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA)
                .append(Component.text("     Spigot: https://www.spigotmc.org/resources/pathinator.118803")));
        CONSOLE.sendMessage(Component.text("*", NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage(Component.text("************************************************************************",
                NamedTextColor.DARK_AQUA));
        CONSOLE.sendMessage("");
    }
}
