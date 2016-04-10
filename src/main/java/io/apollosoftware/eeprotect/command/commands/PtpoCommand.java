package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.lib.command.AdminAccess;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class PtpoCommand extends ServerCommand implements AdminAccess {

    @Getter
    private EEProtect plugin;

    public PtpoCommand(EEProtect plugin) {
        super("ptpo");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "/ptpo <playername>");
            return;
        }

        Player player = (Player) sender;

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            new PluginMessage("NOT_ONLINE").param(args[0]).sendTo(sender);
            return;
        }

        plugin.teleport(player, target);

        new PluginMessage("TP_PLAYER").param(target.getName()).sendTo(player);
    }


    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String error() {
        return "You are not allowed to teleport override";
    }
}
