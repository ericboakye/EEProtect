package io.apollosoftware.eeprotect.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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

public class ClaimData {

    // NEVER CHANGES
    @Getter
    @Setter
    private int ID;

    @Setter
    @Getter
    private String world;


    @Setter
    private Vector minimumBoundaryCorner;

    @Setter
    private Vector maximumBoundaryCorner;

    @Getter
    @Setter
    private String entryMessage;


    @Getter
    @Setter
    private String exitMessage;

    @Getter
    @Setter
    private long timeCreated;

    @Getter
    @Setter
    private boolean entryProtection = false;

    @Getter
    @Setter
    private UserData owner;

    /**
     * Get a vector containing the smallest X, Y, and Z components for the
     * corner of the axis-aligned bounding box that contains this region.
     *
     * @return the minimum point
     */
    public Vector getMinimumPoint() {
        return new Vector(minimumBoundaryCorner.getX(), 0, minimumBoundaryCorner.getZ());
    }

    /**
     * Get a vector containing the highest X, Y, and Z components for the
     * corner of the axis-aligned bounding box that contains this region.
     *
     * @return the maximum point
     */
    public Vector getMaximumPoint() {
        return new Vector(maximumBoundaryCorner.getX(), 0, maximumBoundaryCorner.getZ());
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }


    //used internally to prevent overlaps when creating claims
    public boolean overlaps(ClaimData otherClaim) {
        //NOTE:  if trying to understand this makes your head hurt, don't feel bad - it hurts mine too.
        //try drawing pictures to visualize test cases.

        if (!getWorld().equals(otherClaim.getWorld()))
            return false;

        //first, check the corners of this claim aren't inside any existing claims
        if (otherClaim.contains(getMaximumPoint())) return true;
        if (otherClaim.contains(getMinimumPoint())) return true;
        if (otherClaim.contains(new Location(getWorld(), minimumBoundaryCorner.getBlockX(), 0, maximumBoundaryCorner.getBlockZ()).getDirection()))
            return true;
        if (otherClaim.contains(new Location(getWorld(), maximumBoundaryCorner.getBlockX(), 0, minimumBoundaryCorner.getBlockZ()).getDirection()))
            return true;

        //verify that no claim's lesser boundary point is inside this new claim, to cover the "existing claim is entirely inside new claim" case
        if (this.contains(otherClaim.getMinimumPoint())) return true;

        //verify this claim doesn't band across an existing claim, either horizontally or vertically
        if (this.getMinimumPoint().getBlockZ() <= otherClaim.getMaximumPoint().getBlockZ() &&
                this.getMinimumPoint().getBlockZ() >= otherClaim.getMinimumPoint().getBlockZ() &&
                this.getMinimumPoint().getBlockX() < otherClaim.getMinimumPoint().getBlockX() &&
                this.getMaximumPoint().getBlockX() > otherClaim.getMaximumPoint().getBlockX())
            return true;

        if (this.getMaximumPoint().getBlockZ() <= otherClaim.getMaximumPoint().getBlockZ() &&
                this.getMaximumPoint().getBlockZ() >= otherClaim.getMinimumPoint().getBlockZ() &&
                this.getMinimumPoint().getBlockX() < otherClaim.getMinimumPoint().getBlockX() &&
                this.getMaximumPoint().getBlockX() > otherClaim.getMaximumPoint().getBlockX())
            return true;

        if (this.getMinimumPoint().getBlockX() <= otherClaim.getMaximumPoint().getBlockX() &&
                this.getMinimumPoint().getBlockX() >= otherClaim.getMinimumPoint().getBlockX() &&
                this.getMinimumPoint().getBlockZ() < otherClaim.getMinimumPoint().getBlockZ() &&
                this.getMaximumPoint().getBlockZ() > otherClaim.getMaximumPoint().getBlockZ())
            return true;

        return this.getMaximumPoint().getBlockX() <= otherClaim.getMaximumPoint().getBlockX() &&
                this.getMaximumPoint().getBlockX() >= otherClaim.getMinimumPoint().getBlockX() &&
                this.getMinimumPoint().getBlockZ() < otherClaim.getMinimumPoint().getBlockZ() &&
                this.getMaximumPoint().getBlockZ() > otherClaim.getMaximumPoint().getBlockZ();
    }

    public int getXLength() {
        return maximumBoundaryCorner.getBlockX() - minimumBoundaryCorner.getBlockX() + 1;
    }


    public int getZLength() {
        return maximumBoundaryCorner.getBlockZ() - minimumBoundaryCorner.getBlockZ() + 1;
    }

    public int volume() {
        int xLength = getXLength();
        int zLength = getZLength();

        try {
            long v = xLength * zLength;
            if (v > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return (int) v;
            }
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }

    public boolean contains(Vector pt) {
        final double x = pt.getX();
        final double z = pt.getZ();

        return x >= minimumBoundaryCorner.getX() && x < maximumBoundaryCorner.getBlockX() + 1
                && z >= minimumBoundaryCorner.getBlockZ() && z < maximumBoundaryCorner.getBlockZ() + 1;
    }


}
