package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.AdminAccess;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class CheckClaimCommand extends ServerCommand implements AdminAccess {

    @Getter
    private EEProtect plugin;

    public CheckClaimCommand(EEProtect plugin) {
        super("checkclaim");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        Player player = (Player) sender;
        ClaimData claim = plugin.getClaim(player.getLocation());

        if (claim == null) {
            new PluginMessage("NO_CLAIM").sendTo(sender);
            return;
        }

        String dateCreated = new SimpleDateFormat
                ("yyyy/MM/dd hh:mma z").format(new Date(claim.getTimeCreated()));

        new PluginMessage("CHECK_CLAIM").param(claim.getOwner().getName(), dateCreated, claim.volume(), claim.isEntryProtection() ? "enabled" : "disabled").sendTo(sender);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String error() {
        return "&cYou are not allowed to check claims.";
    }
}
