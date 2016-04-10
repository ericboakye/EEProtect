package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.RegionSelection;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
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
public class UnclaimCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public UnclaimCommand(EEProtect plugin) {
        super("unclaim");

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
            final UserData user = plugin.getDatabaseLoader().createIfNotExists(player);

            ClaimData claim = plugin.getClaim(player.getLocation());

            if (claim == null) {
                new PluginMessage("NO_CLAIM").sendTo(sender);
                return;
            }

            if (!claim.getOwner().equals(user)) {
                new PluginMessage("NOT_CLAIM_OWNER").sendTo(sender);
                return;
            }


            int returningGems = claim.volume() / 2;

            if (!user.isUnclaimConfirmation()) {
                user.setUnclaimConfirmation(true);
                new PluginMessage("UNCLAIM_CONFIRMATION").param(returningGems).sendTo(player);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        user.setUnclaimConfirmation(false);
                    }
                }, 200);

                return;
            }

            user.setUnclaimConfirmation(false);
            user.setGems(user.getGems() + returningGems);
            plugin.getClaims().remove(claim.getID());
            user.save();

            new PluginMessage("UNCLAIM").param(returningGems).sendTo(sender);
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
