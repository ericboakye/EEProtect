package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.command.PluginCommandWrapper;
import io.apollosoftware.eeprotect.command.commands.sub.AddGemsCommand;
import io.apollosoftware.eeprotect.command.commands.sub.EntryMessageCommand;
import io.apollosoftware.eeprotect.command.commands.sub.RemoveGemsCommand;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.*;
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
public class GemsCommand extends ServerCommand implements SubExecutor, PluginDependable<EEProtect> {


    private EEProtect plugin;


    public GemsCommand(EEProtect plugin) {
        super("gems");

        this.plugin = plugin;
        compartments.add(AddGemsCommand.class);
        compartments.add(RemoveGemsCommand.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        SubCompartment[] subCompartments = plugin.getCommandWrapper().getCompartments(compartments, this);


        if (args.length == 0) {
            try {
                if (!(sender instanceof Player)) {
                    System.out.println("Only players are able to run this command.");

                    return;
                }
                Player player = (Player) sender;
                UserData user = plugin.getDatabaseLoader().createIfNotExists(player);


                int balance = user.getGems();
                if (balance != 0) {
                    new PluginMessage("GEM_BALANCE").param(user.getGems(), (balance > 1 ? "gems" : "gem")).sendTo(sender);
                    return;
                }

                new PluginMessage("NO_GEMS").sendTo(sender);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (args[0].equalsIgnoreCase(p.getName())) {
                        if (!sender.hasPermission(PluginCommandWrapper.getAdminPermission())) {
                            sender.sendMessage(ChatColor.RED + "You do not have permission to view other players' balances.");
                            return;
                        }
                        UserData user = plugin.getDatabaseLoader().createIfNotExists(p);

                        int balance = user.getGems();
                        if (balance != 0) {
                            new PluginMessage("GEM_BALANCE_OTHER").param(user.getName(), user.getGems(), (balance > 1 ? "gems" : "gem")).sendTo(sender);

                            return;
                        }

                        new PluginMessage("NO_GEMS_OTHER").param(user.getName()).sendTo(sender);
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new CommandException();
            }

            if (!check(subCompartments, sender, args))
                sender.sendMessage(plugin.getCommandWrapper().getUsages(subCompartments));
        }

    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public EEProtect getPlugin() {
        return plugin;
    }
}
