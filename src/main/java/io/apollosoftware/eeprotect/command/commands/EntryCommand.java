package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.command.commands.sub.EntryExitMessageCommand;
import io.apollosoftware.eeprotect.command.commands.sub.EntryMessageCommand;
import io.apollosoftware.eeprotect.command.commands.sub.EntryProtectCommand;
import io.apollosoftware.eeprotect.command.commands.sub.EntryUnprotectCommand;
import io.apollosoftware.lib.command.*;
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
public class EntryCommand extends ServerCommand implements SubExecutor, PluginDependable<EEProtect> {


    private EEProtect plugin;


    public EntryCommand(EEProtect plugin) {
        super("entry");

        this.plugin = plugin;

        compartments.add(EntryMessageCommand.class);
        compartments.add(EntryProtectCommand.class);
        compartments.add(EntryUnprotectCommand.class);
        compartments.add(EntryExitMessageCommand.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        SubCompartment[] subCompartments = plugin.getCommandWrapper().getCompartments(compartments, this);

        if (args.length > 0) {
            if (!check(subCompartments, sender, args))
                sender.sendMessage(plugin.getCommandWrapper().getUsages(subCompartments));
            return;
        }

        sender.sendMessage(plugin.getCommandWrapper().getUsages(subCompartments));
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
