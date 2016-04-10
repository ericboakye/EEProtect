package io.apollosoftware.eeprotect;


import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.RegionSelection;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.EventListener;
import io.apollosoftware.lib.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class PluginEventListener extends EventListener<EEProtect> {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        plugin.getDatabaseLoader().createIfNotExists(player).getName();
    }

    @EventHandler
    public void onClaim(PlayerInteractEvent event) throws IOException {
        Player player = event.getPlayer();
        UserData user = plugin.getDatabaseLoader().createIfNotExists(player);
        RegionSelection selection = user.getSelection();

        if (selection == null) return;

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            selection.setFirstPosition(event.getClickedBlock().getLocation());
            selection.setWorld(event.getClickedBlock().getLocation().getWorld().getName());
            new PluginMessage("SELECT_POS_1").sendTo(player);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            selection.setSecondPosition(event.getClickedBlock().getLocation());
            selection.setWorld(event.getClickedBlock().getLocation().getWorld().getName());
            new PluginMessage("SELECT_POS_2").sendTo(player);
        }

        if (selection.isComplete()) {
            user.setSelection(null);

            if (plugin.createNewClaim(user, selection)) new PluginMessage("CLAIM").sendTo(player);
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        ClaimData claim = plugin.getClaim(event.getTo());

        if (claim != null && claim.isEntryProtection() && !claim.getOwner().canAccess(player)) {
            new PluginMessage("CANNOT_CLAIM_TELEPORT").param(claim.getOwner().getName()).sendTo(player);
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimEntry(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        ClaimData claim = plugin.getClaim(event.getTo());

        if (claim != null && plugin.getClaim(event.getFrom()) == null) {
            if (claim.isEntryProtection() && !claim.getOwner().canAccess(player)) {
                final Location override = event.getFrom();
                override.setPitch(event.getTo().getPitch());
                override.setYaw(event.getTo().getYaw());

                event.setTo(override.clone());

                Entity vehicle = player.getVehicle();
                if (vehicle != null) {
                    vehicle.eject();

                    Entity current = vehicle;
                    while (current != null) {
                        current.eject();
                        vehicle.setVelocity(new org.bukkit.util.Vector());
                        if (vehicle instanceof LivingEntity) {
                            vehicle.teleport(override.clone());
                        } else {
                            vehicle.teleport(override.clone().add(0, 1, 0));
                        }
                        current = current.getVehicle();
                    }


                    new PluginMessage("CANNOT_ENTER_CLAIM").param(claim.getOwner().getName()).sendTo(player);
                    player.teleport(override.clone().add(0, 1, 0));

                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        public void run() {
                            player.teleport(override.clone().add(0, 1, 0));
                        }
                    }, 1);
                }
            } else if (claim.getEntryMessage() != null && !claim.getEntryMessage().isEmpty()) {
                new Title(ChatColor.translateAlternateColorCodes('&', claim.getEntryMessage())).sendActionBar(player);
            }

        } else if (claim == null && plugin.getClaim(event.getFrom()) != null) {
            claim = plugin.getClaim(event.getFrom());

            if (claim.getExitMessage() != null && !claim.getExitMessage().isEmpty())
                new Title(ChatColor.translateAlternateColorCodes('&', claim.getExitMessage())).sendActionBar(player);
        }
    }

}
