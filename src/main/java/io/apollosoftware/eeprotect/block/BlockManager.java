package io.apollosoftware.eeprotect.block;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.block.blocks.UserProtectedBlock;
import io.apollosoftware.eeprotect.command.PluginCommandWrapper;
import io.apollosoftware.eeprotect.data.ClaimData;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.EventListener;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import java.io.IOException;
import java.util.*;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class BlockManager extends EventListener<EEProtect> {

    private final Material[] PROTECTED_BLOCKS = {Material.CHEST, Material.TRAPPED_CHEST,
            Material.FURNACE, Material.ENCHANTMENT_TABLE, Material.ANVIL, Material.BEACON, Material.BREWING_STAND, Material.ENDER_CHEST};

    @Getter
    private Map<BlockPosition, BlockData> blocks = new HashMap<>();

    @SuppressWarnings("unchecked")
    public BlockManager() {
        register();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        for (UserData user : plugin.getUsers().values()) {

            YamlConfiguration conf = user.getDB();

            if (!conf.contains("blocks")) continue;
            for (Map<String, Object> blockDataMap : (List<Map<String, Object>>) conf.get("blocks")) {

                Material material = Material.valueOf((String) blockDataMap.get("type"));

                World world = Bukkit.getWorld((String) blockDataMap.get("world"));

                int x = (int) blockDataMap.get("x");
                int y = (int) blockDataMap.get("y");
                int z = (int) blockDataMap.get("z");

                if (world == null) {
                    plugin.getLogger().info("Unable to load world: " + blockDataMap.get("world") + " for block at x: " + x + " y: " + y + " z: " + z);
                    continue;
                }

                BlockPosition pos = new BlockPosition(world, x, y, z);
                Block block = pos.toLocation().getBlock();

                if (!material.equals(block.getType())) {
                    plugin.getLogger().info("Unable to locate block in world: " + blockDataMap.get("world") + " x: " + x + " y: " + y + " z: " + z);
                    continue;
                }

                UserProtectedBlock protectedBlock = new UserProtectedBlock(plugin, user, pos, block);
                blocks.put(pos, protectedBlock);
            }

            saveAllBlocks(user);
        }

        plugin.getLogger().info("Successfully loaded " + blocks.size() + " blocks");
    }


    public void saveAllBlocks(UserData user) {
        List<Map<String, Object>> blockMaps = new ArrayList<>();

        for (BlockPosition pos : blocks.keySet()) {
            BlockData data = blocks.get(pos);

            if (data instanceof UserProtectedBlock) {
                if (!((UserProtectedBlock) data).getOwner().equals(user))
                    continue;
            }

            Map<String, Object> map = new HashMap<>();

            map.put("type", data.getType().name());
            map.put("world", pos.getWorld().getName());
            map.put("x", pos.getRelativeX());
            map.put("y", pos.getRelativeY());
            map.put("z", pos.getRelativeZ());

            blockMaps.add(map);
        }

        user.getDB().set("blocks", blockMaps);
        user.save();
    }

    public void disable() {
        //TODO
    }

    public boolean canProtect(Material material) {
        return Arrays.asList(PROTECTED_BLOCKS).contains(material);
    }

    public BlockData registerProtectedBlock(UserData owner, Block block) {
        BlockPosition pos = new BlockPosition(block.getLocation());
        if (contains(pos)) return null;

        UserProtectedBlock data = new UserProtectedBlock(plugin, owner, pos, block);
        return registerBlock(data);
    }

    public BlockData registerBlock(BlockData data) {
        if (data == null) return null;
        blocks.put(data.getPos(), data);
        return data;
    }

    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public boolean contains(Location location) {
        return getBlock(location) != null;
    }

    public boolean contains(BlockPosition pos) {
        return getBlock(pos) != null;
    }

    public BlockData getBlock(Location location) {
        return getBlock(new BlockPosition(location));
    }

    public BlockData getBlock(BlockPosition pos) {
        return blocks.containsKey(pos) ? blocks.get(pos) : null;
    }

    public void removeBlock(BlockPosition pos, boolean breakNormally) {
        BlockData data = getBlock(pos);
        Block block = data.toBlock();

        if (breakNormally)
            block.breakNaturally();

        //if (dropItems) for (ItemStack i : pos.toLocation().getBlock().getDrops())
        //  pos.getWorld().dropItem(pos.toLocation(), i);

        blocks.remove(pos);

        if (data instanceof UserProtectedBlock)
            saveAllBlocks(((UserProtectedBlock) data).getOwner());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        BlockPosition pos = new BlockPosition(event.getClickedBlock().getLocation());

        if (!contains(pos)) return;

        BlockData block = getBlock(pos);

        if (block instanceof InteractiveBlock) ((InteractiveBlock) block).onInteract(event);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) throws IOException {
        UserData user = plugin.getDatabaseLoader().createIfNotExists(event.getPlayer());
        BlockPosition pos = new BlockPosition(event.getBlock().getLocation());

        if (plugin.getClaim(pos.toLocation()) != null) {
            ClaimData claim = plugin.getClaim(pos.toLocation());

            UserData userData = plugin.getUsers().get(claim.getOwner().getUUID());

            if (!userData.canAccess(event.getPlayer())) {
                event.setCancelled(true);

                new PluginMessage("CANNOT_MODIFY_BLOCKS").param(userData.getName()).sendTo(event.getPlayer());
                return;
            }
        }

        if (canProtect(event.getBlock().getType())) {
            registerProtectedBlock(user, event.getBlock());
            saveAllBlocks(user);

            new PluginMessage("PROTECT").sendTo(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        BlockPosition pos = new BlockPosition(event.getBlock().getLocation());

        if (plugin.getClaim(pos.toLocation()) != null) {
            ClaimData claim = plugin.getClaim(pos.toLocation());

            UserData userData = plugin.getUsers().get(claim.getOwner().getUUID());

            if (!userData.canAccess(event.getPlayer())) {
                event.setCancelled(true);

                new PluginMessage("CANNOT_MODIFY_BLOCKS").param(userData.getName()).sendTo(event.getPlayer());
            }

            return;
        }

        if (!contains(pos)) return;

        UserProtectedBlock block = (UserProtectedBlock) getBlock(pos);

        if (block.canModify(event.getPlayer()))
            removeBlock(pos, !event.getPlayer().getGameMode().equals(GameMode.CREATIVE));
        else {
            new PluginMessage("CANNOT_MODIFY_BLOCKS").param(block.getOwner().getName()).sendTo(event.getPlayer());
            event.setCancelled(true);
        }
    }

}
