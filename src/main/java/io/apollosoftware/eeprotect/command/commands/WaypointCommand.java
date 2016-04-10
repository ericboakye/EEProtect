package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.command.commands.sub.*;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.eeprotect.data.WaypointData;
import io.apollosoftware.lib.command.*;
import io.apollosoftware.lib.lang.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@PluginDependant
public class WaypointCommand extends ServerCommand implements SubExecutor, PluginDependable<EEProtect> {


    private EEProtect plugin;


    public WaypointCommand(EEProtect plugin) {
        super("waypoint");

        this.plugin = plugin;

        compartments.add(CreateWaypointCommand.class);
        compartments.add(DeleteWaypointCommand.class);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            plugin.getLogger().info("You must be a player to execute this command");
            return;
        }

        Player player = (Player) sender;
        SubCompartment[] subCompartments = plugin.getCommandWrapper().getCompartments(compartments, this);

        UserData user;

        try {
            user = plugin.getDatabaseLoader().createIfNotExists(player);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException();
        }

        Map<String, WaypointData> waypoints = plugin.getWaypoints(user);

        if (args.length == 0) {

            if (waypoints.size() == 0) {
                new PluginMessage("NO_WAYPOINTS").sendTo(player);
                return;
            }

            Message.create("WAYPOINTS").sendTo(player);

            for (String name : waypoints.keySet())
                player.sendMessage(ChatColor.GOLD + name);

        } else {
            if (waypoints.containsKey(args[0])) {
                WaypointData waypoint = waypoints.get(args[0]);
                plugin.teleport(player, waypoint.getLocation());
                new PluginMessage("WAYPOINT_TELEPORT").param(args[0]).sendTo(player);
                return;
            }

            if (!check(subCompartments, sender, args))
                sender.sendMessage(plugin.getCommandWrapper().getUsages(subCompartments));
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"wp", "waypoints"};
    }

    @Override
    public EEProtect getPlugin() {
        return plugin;
    }
}
