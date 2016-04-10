package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.AdminAccess;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class PtpcoCommand extends ServerCommand implements AdminAccess {

    @Getter
    private EEProtect plugin;

    public PtpcoCommand(EEProtect plugin) {
        super("ptpco");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.GREEN + "/ptpco <x> <y> <z>");
            return;
        }


        Player player = (Player) sender;
        try {

            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);


            Location target = new Location(player.getWorld(), x, y, z);
            plugin.teleport(player, target);
            new PluginMessage("PTPCO").param(x, y, z).sendTo(player);
        } catch (NumberFormatException e) {
            new PluginMessage("NOT_NUMBER_FORMAT").sendTo(sender);
        }
    }


    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String error() {
        return "You are not allowed to teleport to coordinates";
    }
}
