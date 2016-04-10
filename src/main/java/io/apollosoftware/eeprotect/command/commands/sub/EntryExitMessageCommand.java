package io.apollosoftware.eeprotect.command.commands.sub;


import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.command.commands.EntryCommand;
import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.SubCompartment;
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
public class EntryExitMessageCommand extends SubCompartment<EntryCommand> {


    private EEProtect plugin;

    public EntryExitMessageCommand(EEProtect plugin) {
        super(ChatColor.GREEN + "/entry exitmsg <message>", "exitmsg", "exitmessage");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {

        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return;
        }

        try {
            Player player = (Player) sender;
            UserData user = plugin.getDatabaseLoader().createIfNotExists(player);

            ClaimData claim = plugin.getClaim(player.getLocation());

            if (claim == null) {
                new PluginMessage("NO_CLAIM").sendTo(sender);
                return;
            }

            if (!claim.getOwner().equals(user)) {
                new PluginMessage("NOT_CLAIM_OWNER").sendTo(sender);
                return;
            }

            StringBuilder message = new StringBuilder(args[0]);
            for (int arg = 1; arg < args.length; arg++) message.append(" ").append(args[arg]);

            claim.setExitMessage(ChatColor.YELLOW + message.toString());
            user.save();

            new PluginMessage("SET_EXIT_MESSAGE").param(message.toString()).sendTo(player);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException();
        }

    }
}
