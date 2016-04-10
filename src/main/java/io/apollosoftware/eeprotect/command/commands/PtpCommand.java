package io.apollosoftware.eeprotect.command.commands;

import com.bobacadodl.JSONChatLib.JSONChatMessage;
import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.TeleportRequest;
import io.apollosoftware.eeprotect.data.UserData;
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

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class PtpCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public PtpCommand(EEProtect plugin) {
        super("ptp");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "/ptp <playername>");
            return;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            new PluginMessage("NOT_ONLINE").param(args[0]).sendTo(sender);
            return;
        }

        try {

            UserData user = plugin.getDatabaseLoader().createIfNotExists(player);
            UserData targetUser = plugin.getDatabaseLoader().createIfNotExists(target);

            if (user.isTeleportToggle()) {
                new PluginMessage("TP_DISABLED2").sendTo(player);
                return;
            }

            if (!targetUser.isTeleportToggle()) {
                new PluginMessage("TP_DISABLED").param(target.getName()).sendTo(player);
                return;
            }

            if (targetUser.canAccess(player)) {
                player.teleport(target);
                new PluginMessage("TP_PLAYER").param(target.getName()).sendTo(player);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandException();
        }

        TeleportRequest tr = Cooldown.getCooldown(target.getName(), player.getName());

        if (tr == null) {
            Cooldown.addCooldown(target.getName(),
                    player.getName(), plugin.getConfiguration().getTeleportRequestCooldown() * 1000);

            new PluginMessage("TP_REQUEST").param(target.getName()).sendTo(player);
            new PluginMessage("TP_REQUEST_OTHER").param(player.getName()).sendTo(target);
            createRequest(target.getName(), player.getName());
            return;
        }

        if (tr.isOver()) {
            Cooldown.addCooldown(target.getName(),
                    player.getName(), plugin.getConfiguration().getTeleportRequestCooldown() * 1000);

            new PluginMessage("TP_REQUEST").param(target.getName()).sendTo(player);
            new PluginMessage("TP_REQUEST_OTHER").param(player.getName()).sendTo(target);
            createRequest(target.getName(), player.getName());
            return;
        }

        new PluginMessage("TP_COOLDOWN").param(tr.getTimeLeft() / 1000).sendTo(player);
    }


    @Override
    public String[] getAliases() {
        return new String[0];
    }
}
