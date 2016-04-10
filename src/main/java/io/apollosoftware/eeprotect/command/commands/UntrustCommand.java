package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
public class UntrustCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public UntrustCommand(EEProtect plugin) {
        super("untrust");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/untrust <playername>");
            return;
        }

        try {
            Player player = (Player) sender;
            UserData user = plugin.getDatabaseLoader().createIfNotExists(player);


            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                new PluginMessage("NOT_ONLINE").param(args[0]).sendTo(sender);
                return;
            }

            if (!user.getAllTrustedUsers().contains(target.getUniqueId())) {
                new PluginMessage("NOT_TRUSTED").param(target.getName()).sendTo(sender);
                return;
            }

            user.getTrustedUsers().remove(target.getUniqueId().toString());
            user.save();

            new PluginMessage("UNTRUST").param(target.getName()).sendTo(player);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException();
        }
    }


    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
