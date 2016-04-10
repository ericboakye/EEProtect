package io.apollosoftware.eeprotect;

import io.apollosoftware.eeprotect.block.BlockManager;
import io.apollosoftware.eeprotect.command.PluginCommandWrapper;
import io.apollosoftware.eeprotect.configuration.DefaultConfiguration;
import io.apollosoftware.eeprotect.configuration.LanguageConfiguration;
import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.RegionSelection;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.eeprotect.data.WaypointData;
import io.apollosoftware.lib.Manifest;
import io.apollosoftware.lib.ServerPlugin;
import io.apollosoftware.lib.lang.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.*;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

@Manifest(name = "EEProtect", version = "v1.0")
public class EEProtect extends ServerPlugin implements Listener {

    public static final int MIN_RELATIVE_SIZE = 25, MAX_RELATIVE_SIZE = 10000;

    @Getter
    private DefaultConfiguration configuration;

    @Getter
    private LanguageConfiguration languageConfiguration;

    @Getter
    private PluginDatabaseLoader databaseLoader;

    @Getter
    private PluginCommandWrapper commandWrapper;

    @Getter
    private static EEProtect plugin;

    @Getter
    private BlockManager blockManager;

    @Getter
    private Map<UUID, UserData> users = new HashMap<>();

    @Getter
    private Map<Integer, WaypointData> waypoints = new HashMap<>();

    @Getter
    private Map<Integer, ClaimData> claims = new HashMap<>();


    @Override
    public void init(Manifest manifest) throws Exception {
        plugin = this;
        setupConfiguration();
        setupDatabase();
        setupPermissions();

        commandWrapper = PluginCommandWrapper.register();
        registerListeners();

        getLogger().info("EEProtect has been enabled");
    }

    public void setupConfiguration() {
        this.configuration = new DefaultConfiguration();
        configuration.load();

        this.languageConfiguration = new LanguageConfiguration();
        languageConfiguration.load();
    }

    public Vector toVector(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    public Location toLocation(World world, Vector vec) {
        return new Location(world, vec.getX(), vec.getY(), vec.getZ());
    }


    public boolean createNewWaypoint(UserData owner, String name, Location location) {
        Player player = owner.getPlayer();

        if (getWaypoints(owner).containsKey(name)) {
            new PluginMessage("ALREADY_WAYPOINT").param(name).sendTo(player);
            return false;
        }

        if (!player.hasPermission("eeprotect.waypoints.limit." + getWaypoints(owner) + 1)) {
            new PluginMessage("WAYPOINT_LIMIT_REACHED").sendTo(player);
            return false;
        }

        int ID = generateID(waypoints);

        WaypointData waypoint = new WaypointData(ID, owner, name, location);
        this.waypoints.put(ID, waypoint);
        owner.save();
        return true;
    }


    public void setupPermissions() {
        for (int i = 0; i < configuration.getMaximumWaypoints(); i++) {
            Permission limitPermission = new Permission("eeprotect.waypoints.limit." + i, PermissionDefault.FALSE);
            getServer().getPluginManager().addPermission(limitPermission);
        }
    }


    public boolean createNewClaim(UserData owner, RegionSelection selection) {
        ClaimData claim = new ClaimData();

        claim.setOwner(owner);
        claim.setWorld(selection.getWorld());
        claim.setTimeCreated(System.currentTimeMillis());
        claim.setEntryMessage(ChatColor.translateAlternateColorCodes('&', Message.create("DEFAULT_CLAIM_ENTRY_MESSAGE").param(owner.getName()).toString()));
        claim.setExitMessage(ChatColor.translateAlternateColorCodes('&', Message.create("DEFAULT_CLAIM_EXIT_MESSAGE").param(owner.getName()).toString()));
        claim.setMinimumBoundaryCorner(selection.getMinimumPoint());
        claim.setMaximumBoundaryCorner(selection.getMaximumPoint());

        int volume = claim.volume();

        if (volume < MIN_RELATIVE_SIZE) {
            new PluginMessage("CLAIM_TOO_SMALL").param((int) Math.sqrt(MIN_RELATIVE_SIZE)).sendTo(owner.getUUID());
            return false;
        } else if (volume > MAX_RELATIVE_SIZE) {
            new PluginMessage("CLAIM_TOO_BIG").param((int) Math.sqrt(MAX_RELATIVE_SIZE)).sendTo(owner.getUUID());
            return false;
        }

        if (isOverlappingAnyClaims(claim)) {
            new PluginMessage("SELECTION_OVERLAPPING").sendTo(owner.getUUID());
            return false;
        }

        if (owner.getGems() < volume) {
            new PluginMessage("NOT_ENOUGH_GEMS_CLAIM").param(claim.volume()).sendTo(owner.getUUID());
            return false;
        }

        owner.setGems(owner.getGems() - claim.volume());

        int ID = generateID(claims);

        claim.setID(ID);
        claims.put(ID, claim);
        owner.save();
        return true;
    }


    public void teleport(Player e1, Player e2) {
        teleport(e1, e2.getLocation());
    }

    public void teleport(final Player entity, final Location location) {
        Entity vehicle = entity.getVehicle();

        if (vehicle != null && (entity.isOp() || entity.hasPermission("eeprotect.horsetp"))) {
            vehicle.eject();

            Entity current = vehicle;
            while (current != null) {
                current.eject();
                vehicle.setVelocity(new org.bukkit.util.Vector());
                if (vehicle instanceof LivingEntity) {
                    vehicle.teleport(location.clone());
                } else {
                    vehicle.teleport(location.clone().add(0, 1, 0));
                }
                current = current.getVehicle();
            }
            entity.teleport(location);
            vehicle.setPassenger(entity);
        } else {
            entity.teleport(location);
        }
    }

    public Map<String, WaypointData> getWaypoints(UserData owner) {
        Map<String, WaypointData> waypoints = new HashMap<>();

        for (WaypointData waypoint : this.waypoints.values())
            if (waypoint.getOwner().equals(owner))
                waypoints.put(waypoint.getName(), waypoint);

        return waypoints;
    }

    public Set<ClaimData> getClaims(UserData owner) {
        Set<ClaimData> claims = new HashSet<>();

        for (ClaimData claim : this.claims.values())
            if (claim.getOwner().equals(owner))
                claims.add(claim);

        return claims;
    }

    public ClaimData getClaim(Location loc) {
        return getClaim(toVector(loc));
    }

    public ClaimData getClaim(Vector vec) {
        for (ClaimData claim : claims.values()) {
            if (claim.contains(vec))
                return claim;
        }
        return null;
    }

    public int generateID(Map<Integer, ?> map) {
        int greatest = 0;

        for (Map.Entry<Integer, ?> e : map.entrySet())
            if (e.getKey() > greatest) greatest = e.getKey();

        return greatest + 1;
    }

    public boolean isOverlappingAnyClaims(ClaimData claim) {
        for (ClaimData c : claims.values()) if (claim.overlaps(c)) return true;
        return false;
    }

    public void setupDatabase() {
        databaseLoader = new PluginDatabaseLoader();
        databaseLoader.load();

        blockManager = new BlockManager();
        blockManager.load();
    }

    public void registerListeners() {
        new PluginEventListener().register();
    }


    public static String getPrefix() {
        return DefaultConfiguration.getChatPrefix();
    }

}
