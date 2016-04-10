/**
 * Copyright (c) 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */


package io.apollosoftware.eeprotect.command;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.command.commands.*;
import io.apollosoftware.lib.command.CommandManager;

public class PluginCommandWrapper extends CommandManager<EEProtect> {

    public PluginCommandWrapper() throws Exception {
        super("eeprotect.admin");

        registerCommand(ClaimCommand.class);
        registerCommand(CancelClaimCommand.class);
        registerCommand(ProtectCommand.class);
        registerCommand(UnprotectCommand.class);
        registerCommand(UnclaimCommand.class);
        registerCommand(CheckClaimCommand.class);
        registerCommand(RemoveClaimCommand.class);
        registerCommand(EntryCommand.class);
        registerCommand(HomeCommand.class);
        registerCommand(SethomeCommand.class);
        registerCommand(GemsCommand.class);
        registerCommand(TrustCommand.class);
        registerCommand(UntrustCommand.class);
        registerCommand(VisitCommand.class);
        registerCommand(WaypointCommand.class);
        registerCommand(PtpcoCommand.class);
        registerCommand(PtpoCommand.class);
        registerCommand(PtpacceptCommand.class);
        registerCommand(PtpdenyCommand.class);
        registerCommand(PtpCommand.class);
        registerCommand(TrustedCommand.class);
        registerCommand(PtptoggleCommand.class);
    }

    public static PluginCommandWrapper register() throws Exception {
        return new PluginCommandWrapper();
    }

}
