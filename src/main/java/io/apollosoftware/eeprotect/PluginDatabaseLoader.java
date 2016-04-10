package io.apollosoftware.eeprotect;

import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.eeprotect.data.WaypointData;
import io.apollosoftware.lib.POJO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class PluginDatabaseLoader extends POJO<EEProtect> {

    private File USER_DIRECTORY = null;


    public PluginDatabaseLoader() {
        USER_DIRECTORY = new File(plugin.getDataFolder(), "users");

        if (!USER_DIRECTORY.exists()) {

            if (USER_DIRECTORY.mkdir())
                plugin.getLogger().info("EEProtect data directory has been created!");
        }
    }

    public void load() {
        File[] files = USER_DIRECTORY.listFiles();

        if (files != null) {
            for (File file : files) {
                String filename = file.getName();
                UUID uuid = UUID.fromString(filename.substring(0, filename.lastIndexOf(".")));
                UserData user = new UserData(uuid, file);

                user.load(false);
                plugin.getUsers().put(uuid, user);
            }
        }

        for (UserData user : plugin.getUsers().values()) {
            for (ClaimData claim : user.loadClaims())
                plugin.getClaims().put(claim.getID(), claim);

            for (WaypointData waypoint : user.loadWaypoints())
                plugin.getWaypoints().put(waypoint.getID(), waypoint);
        }

        plugin.getLogger().info("Successfully loaded " + plugin.getWaypoints().size() + " waypoints into memory");
        plugin.getLogger().info("Successfully loaded " + plugin.getClaims().size() + " claims into memory");
        plugin.getLogger().info("Successfully loaded " + plugin.getUsers().size() + " users into memory");
    }

    public UserData createIfNotExists(Player player) throws IOException {
        return createIfNotExists(player.getUniqueId(), player.getName());
    }

    public UserData createIfNotExists(UUID uuid, String name) throws IOException {
        if (plugin.getUsers().containsKey(uuid))
            return plugin.getUsers().get(uuid);

        File newUserFile = new File(USER_DIRECTORY, uuid.toString() + ".yml");
        if (newUserFile.createNewFile())
            plugin.getLogger().info("Created new file for user: " + name);

        UserData user = new UserData(uuid, name, newUserFile);
        user.save();

        plugin.getUsers().put(uuid, user);
        return user;
    }

    public File getUserDirectory() {
        return USER_DIRECTORY;
    }

    public Location getLocationFromMap(Map<String, Object> map) {
        if (map == null) return null;
        double x = (double) map.get("x");
        double y = (double) map.get("y");
        double z = (double) map.get("z");

        float pitch = 0, yaw = 0;

        if (map.containsKey("pitch")) pitch = ((Double) map.get("pitch")).floatValue();
        if (map.containsKey("yaw")) yaw = ((Double) map.get("yaw")).floatValue();

        return new Location(Bukkit.getWorld((String) map.get("world")), x, y, z, yaw, pitch);
    }


    public Map<String, Object> getMapFromLocation(Location location) {
        if (location == null) return null;

        Map<String, Object> map = new HashMap<>();

        map.put("world", location.getWorld().getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());

        if (location.getPitch() != 0) map.put("pitch", location.getPitch());
        if (location.getYaw() != 0) map.put("yaw", location.getYaw());

        return map;
    }

}
