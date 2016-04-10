package io.apollosoftware.eeprotect.data;

import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.lib.POJO;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class WaypointData extends POJO<EEProtect> {


    // NEVER CHANGES
    @Getter
    @Setter
    private int ID;

    @Getter
    private String name;


    @Getter
    private Location location;

    @Getter
    private UserData owner;

    public WaypointData(int ID, UserData owner, String name, Location location) {
        this.ID = ID;
        this.owner = owner;
        this.name = name;
        this.location = location;
    }

}
