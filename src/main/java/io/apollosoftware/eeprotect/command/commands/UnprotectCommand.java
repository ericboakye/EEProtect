package io.apollosoftware.eeprotect.command.commands;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.block.blocks.UserProtectedBlock;
import io.apollosoftware.eeprotect.data.UserData;
import io.apollosoftware.lib.command.CommandException;
import io.apollosoftware.lib.command.PluginDependant;
import io.apollosoftware.lib.command.ServerCommand;
import lombok.Getter;
import org.bukkit.block.Block;
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
public class UnprotectCommand extends ServerCommand {

    @Getter
    private EEProtect plugin;

    public UnprotectCommand(EEProtect plugin) {
        super("unprotect");

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
            UserData user = plugin.getDatabaseLoader().createIfNotExists(player);

            Block targetBlock = plugin.getBlockManager().getTargetBlock(player, 5);

            if (!plugin.getBlockManager().contains(targetBlock.getLocation())) {
                new PluginMessage("NOT_PROTECTED").sendTo(player);
                return;
            }

            UserProtectedBlock protectedBlock = (UserProtectedBlock) plugin.getBlockManager().getBlock(targetBlock.getLocation());

            if (!protectedBlock.getOwner().equals(user)) {
                new PluginMessage("NOT_OWNER_PROTECT").sendTo(player);
                return;
            }

            plugin.getBlockManager().removeBlock(protectedBlock.getPos(), false);
            plugin.getBlockManager().saveAllBlocks(user);

            new PluginMessage("UNPROTECT").sendTo(sender);
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
