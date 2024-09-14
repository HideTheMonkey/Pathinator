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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class ConsoleHelper {
    private static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();

    public static void sendNewVersionNotice(String newVersion, String currentVersion) {
        CONSOLE.sendMessage("");
        CONSOLE.sendMessage(
                ChatColor.DARK_AQUA + "************************************************************************");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET + "A new version of " + ChatColor.BOLD
                + "Pathinator" + ChatColor.RESET + " is available!");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "*");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET + "Current Version: " + currentVersion);
        CONSOLE.sendMessage(
                ChatColor.DARK_AQUA + "* " + ChatColor.RESET + ChatColor.BOLD + "New Version: " + newVersion);
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "*");
        CONSOLE.sendMessage(
                ChatColor.DARK_AQUA + "* " + ChatColor.RESET
                        + "Please update to take advantage of the latest features and bug fixes.");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "*");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET + "Download from your preferred site:");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET
                + "     Hangar: https://hangar.papermc.io/HideTheMonkey/Pathinator");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET
                + "     Modrinth: https://modrinth.com/plugin/pathinator");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "* " + ChatColor.RESET
                + "     Spigot: https://www.spigotmc.org/resources/pathinator.118803");
        CONSOLE.sendMessage(ChatColor.DARK_AQUA + "*");
        CONSOLE.sendMessage(
                ChatColor.DARK_AQUA + "************************************************************************");
        CONSOLE.sendMessage("");
    }
}
