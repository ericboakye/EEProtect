package io.apollosoftware.eeprotect.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;


/**
 * Copyright © 2016 APOLLOSOFTWARE.IO
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 */

public class RegionSelection {

    @Setter
    private Vector pos1;

    @Setter
    private Vector pos2;

    @Getter
    @Setter
    private String world;

    public RegionSelection() {

    }

    public RegionSelection(World world, Location loc1, Location loc2) {
        this.world = world.getName();

        this.pos1 = new Vector(loc1.getX(), 0, loc1.getZ());
        this.pos2 = new Vector(loc2.getX(), 0, loc2.getZ());
    }

    public void setFirstPosition(Location location) {
        this.pos1 = new Vector(location.getX(), 0, location.getZ());
    }

    public void setSecondPosition(Location location) {
        this.pos2 = new Vector(location.getX(), 0, location.getZ());
    }

    public Vector getMinimumPoint() {
        return new Vector(Math.min(pos1.getX(), pos2.getX()), 0,
                Math.min(pos1.getZ(), pos2.getZ()));
    }

    public Vector getMaximumPoint() {
        return new Vector(Math.max(pos1.getX(), pos2.getX()), 0,
                Math.max(pos1.getZ(), pos2.getZ()));
    }


    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }

}
