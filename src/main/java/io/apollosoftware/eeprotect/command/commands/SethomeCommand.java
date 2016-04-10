package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
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
public class SethomeCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public SethomeCommand(EEProtect plugin) {
        super("sethome", "eeprotect.sethome");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        try {
            Player player = (Player) sender;
            UserData user = plugin.getDatabaseLoader().createIfNotExists(player);

            user.setHomeLocation(player.getLocation());
            user.save();

            new PluginMessage("HOME_SET").sendTo(player);
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
