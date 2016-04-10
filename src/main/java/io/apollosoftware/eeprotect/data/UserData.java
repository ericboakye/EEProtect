package io.apollosoftware.eeprotect.data;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.command.PluginCommandWrapper;
import io.apollosoftware.lib.configuration.Configuration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class UserData extends Configuration<EEProtect> {

    private UUID uuid;


    private String name;

    @Getter
    @Setter
    private int gems;

    @Getter
    @Setter
    private Location homeLocation;

    @Getter
    @Setter
    private boolean teleportToggle = true;

    @Getter
    @Setter
    private boolean unclaimConfirmation;

    @Getter
    @Setter
    private RegionSelection selection;

    @Getter
    private List<String> trustedUsers = new ArrayList<>();


    public UserData(UUID uuid, File file) {
        super(file);

        this.uuid = uuid;
    }

    public UserData(UUID uuid, String name, File file) {
        super(file);

        this.uuid = uuid;
        this.name = name;
    }

    public UserData(Player player, File file) {
        super(file);

        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    public YamlConfiguration getDB() {
        return conf;
    }

    public boolean canAccess(Player player) {
        return player.getUniqueId().equals(uuid)
                || getAllTrustedUsers().contains(player.getUniqueId())
                || ((player.isOp() || player.hasPermission(PluginCommandWrapper.getAdminPermission().getName())));
    }

    public List<UUID> getAllTrustedUsers() {
        List<UUID> trustedUsers = new ArrayList<>();
        for (String user : this.trustedUsers) trustedUsers.add(UUID.fromString(user));
        return trustedUsers;
    }

    public String getName() {
        if (Bukkit.getPlayer(uuid) != null && !Bukkit.getPlayer(uuid).getName().equals(name)) {
            this.name = Bukkit.getPlayer(uuid).getName();
            save();
        }
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterLoad() {
        name = conf.getString("playerName");
        gems = conf.getInt("gems");
        trustedUsers = conf.getStringList("trustedUsers");
        teleportToggle = conf.getBoolean("teleportToggle");
        if (conf.contains("homeLocation"))
            homeLocation = plugin.getDatabaseLoader().getLocationFromMap(conf.getConfigurationSection("homeLocation").getValues(false));
    }


    @SuppressWarnings("unchecked")
    public List<WaypointData> loadWaypoints() {
        List<WaypointData> waypoints = new ArrayList<>();

        if (!conf.contains("waypoints")) return waypoints;

        for (Map<String, Object> map : (List<Map<String, Object>>) conf.get("waypoints")) {
            int ID = (Integer) map.get("ID");
            String name = (String) map.get("name");
            Location location = plugin.getDatabaseLoader().getLocationFromMap((Map<String, Object>) map.get("location"));
            WaypointData waypoint = new WaypointData(ID, this, name, location);
            waypoints.add(waypoint);
        }

        return waypoints;
    }

    @SuppressWarnings("unchecked")
    public List<ClaimData> loadClaims() {
        List<ClaimData> claims = new ArrayList<>();

        if (!conf.contains("claims")) return claims;

        for (Map<String, Object> map : (List<Map<String, Object>>) conf.get("claims")) {
            ClaimData claim = new ClaimData();

            claim.setOwner(this);
            claim.setWorld((String) map.get("world"));
            claim.setID((Integer) map.get("ID"));
            claim.setEntryProtection(((Boolean) map.get("entryProtection")));

            claim.setEntryMessage(((String) map.get("entryMessage")));
            claim.setExitMessage(((String) map.get("exitMessage")));
            claim.setTimeCreated(((Long) map.get("timeCreated")));

            claim.setMaximumBoundaryCorner(plugin.toVector(plugin.getDatabaseLoader().getLocationFromMap((Map<String, Object>) map.get("maximumCorner"))));
            claim.setMinimumBoundaryCorner(plugin.toVector(plugin.getDatabaseLoader().getLocationFromMap((Map<String, Object>) map.get("minimumCorner"))));

            claims.add(claim);
        }


        return claims;
    }

    @Override
    public void onSave() {
        conf.set("playerName", name);
        conf.set("trustedUsers", trustedUsers);
        conf.set("gems", gems);
        conf.set("homeLocation", plugin.getDatabaseLoader().getMapFromLocation(homeLocation));
        conf.set("teleportToggle", teleportToggle);

        List<Map<String, Object>> claimMaps = new ArrayList<>();
        List<Map<String, Object>> waypointsMaps = new ArrayList<>();

        for (ClaimData claim : plugin.getClaims(this)) {

            Map<String, Object> map = new HashMap<>();

            map.put("ID", claim.getID());
            map.put("minimumCorner", plugin.getDatabaseLoader().getMapFromLocation(plugin.toLocation(claim.getWorld(), claim.getMinimumPoint())));
            map.put("maximumCorner", plugin.getDatabaseLoader().getMapFromLocation(plugin.toLocation(claim.getWorld(), claim.getMaximumPoint())));
            map.put("world", claim.getWorld().getName());
            map.put("timeCreated", claim.getTimeCreated());
            map.put("exitMessage", claim.getExitMessage());
            map.put("entryProtection", claim.isEntryProtection());
            map.put("entryMessage", claim.getEntryMessage());

            claimMaps.add(map);
        }

        for (WaypointData waypointData : plugin.getWaypoints(this).values()) {

            Map<String, Object> map = new HashMap<>();
            map.put("ID", waypointData.getID());
            map.put("name", waypointData.getName());
            map.put("location", plugin.getDatabaseLoader().getMapFromLocation(waypointData.getLocation()));

            waypointsMaps.add(map);
        }

        conf.set("claims", claimMaps);
        conf.set("waypoints", waypointsMaps);
    }


}
