package io.apollosoftware.eeprotect.command.commands.sub;


import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.command.commands.GemsCommand;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.AdminAccess;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.SubCompartment;
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
public class AddGemsCommand extends SubCompartment<GemsCommand> implements AdminAccess {


    private EEProtect plugin;

    public AddGemsCommand(EEProtect plugin) {
        super(ChatColor.GREEN + "/gems add <player> <amount>", "add", "give");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }

        try {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                new PluginMessage("NOT_ONLINE").param(args[0]).sendTo(sender);
                return;
            }

            UserData user = plugin.getDatabaseLoader().createIfNotExists(target);

            int amount = Integer.parseInt(args[1]);
            user.setGems(user.getGems() + amount);
            user.save();

            new PluginMessage("ADD_GEMS").param(amount, target.getName()).sendTo(sender);
            new PluginMessage("ADD_GEMS_OTHER").param(amount).sendTo(target);
        } catch (NumberFormatException e) {
            new PluginMessage("NOT_NUMBER").param(args[1]).sendTo(sender);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException();
        }
    }

    @Override
    public String error() {
        return "You are not allowed to add gems";
    }
}
