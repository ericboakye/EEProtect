package io.apollosoftware.eeprotect.block;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Class created by xenojava on 12/31/2015.
 */
public class BlockPosition {


    @Getter
    private final World world;

    /**
     * The relative x
     */

    @Getter
    private final int relativeX;

    @Getter
    private final int relativeY;
    /**
     * The relative y
     */

    @Getter
    private final int relativeZ;

    /**
     * The relative z
     */

    public BlockPosition(World world, int relativeX, int relativeY, int relativeZ) {
        this.world = world;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
    }

    public BlockPosition(Location location) {
        this.world = location.getWorld();
        this.relativeX = location.getBlockX();
        this.relativeY = location.getBlockY();
        this.relativeZ = location.getBlockZ();
    }

    /**
     * Returns a stringed version of this, to be used in storage
     *
     * @return stringed version
     */
    @Override
    public String toString() {
        return relativeX + "!" + relativeY + "!" + relativeZ;
    }

    /**
     * Checks if another position is the same relatively
     *
     * @param object to check again
     * @return true of they're at the same spot
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BlockPosition) && !(object instanceof Location)) {
            return false;
        }

        if (object instanceof BlockPosition) {
            BlockPosition other = (BlockPosition) object;
            return other.getRelativeZ() == relativeZ && other.getRelativeX() == relativeX && other.getRelativeY() == relativeY;
        }

        Location other = (Location) object;
        return other.getBlockZ() == relativeZ && other.getBlockX() == relativeX && other.getBlockY() == relativeY;
    }


    @Override
    public int hashCode() {
        return relativeX * relativeY * relativeZ;
    }

    public Location toLocation() {
        return new Location(world, relativeX, relativeY, relativeZ);
    }

    /**
     * A quick way to check coordinates.
     *
     * @param x the x coord
     * @param y the y coord
     * @param z the z coord
     * @return if the coordinates are the same
     */
    public boolean equals(int x, int y, int z) {
        return x == this.relativeX && y == this.relativeY && z == this.relativeZ;
    }
}
