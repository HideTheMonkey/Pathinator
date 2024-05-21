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

package com.hidethemonkey.pathinator.commands;

import dev.jorel.commandapi.executors.CommandArguments;

import org.bukkit.command.CommandSender;

import com.hidethemonkey.pathinator.Pathinator;
import com.hidethemonkey.pathinator.PathinatorConfig;

public class CustomCommands {

    private Pathinator plugin;
    private PathinatorConfig config;

    public CustomCommands(Pathinator pathPlugin) {
        this.plugin = pathPlugin;
        this.config = pathPlugin.getPConfig();
    }

    /**
     * 
     * @param sender
     * @param args
     */
    public void customPath(CommandSender sender, CommandArguments args) {
    }
}
