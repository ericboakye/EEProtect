package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.TeleportRequest;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import io.apollosoftware.lib.lang.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.apollosoftware.eeprotect.TeleportRequest.*;
import static io.apollosoftware.eeprotect.TeleportRequest.Cooldown;
import static io.apollosoftware.eeprotect.TeleportRequest.createRequest;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class PtpacceptCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public PtpacceptCommand(EEProtect plugin) {
        super("ptpaccept");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }


        Player player = (Player) sender;

        TeleportRequest tr = Cooldown.getCooldown(player.getName(),
                getRequester(player.getName()));

        if (tr == null) {
            Message.create("NO_REQUEST").sendTo(player);
            return;
        }
        if (tr.isOver()) {
            Message.create("NO_REQUEST").sendTo(player);
            return;
        }

        Player requester = Bukkit.getServer().getPlayer(getRequester(player.getName()));

        if (requester == null) {
            Message.create("REQUESTER_NOT_ONLINE").sendTo(player);
            return;
        }

        new PluginMessage("REQUEST_ACCEPT").param(requester.getName()).sendTo(player);
        new PluginMessage("REQUEST_ACCEPT_OTHER").sendTo(requester);

        Cooldown.delete(player.getName(),
                getRequester(player.getName()));

        plugin.teleport(requester, player);
    }


    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
