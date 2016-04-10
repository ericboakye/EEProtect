package io.apollosoftware.eeprotect.block;

import io.apollosoftware.eeprotect.EEProtect;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Class created by xenojava on 12/31/2015.
 */
public abstract class BlockData {

    @Getter
    private final BlockPosition pos;

    @Getter
    protected Material type;

    @Getter
    protected boolean isCustom;

    @Getter
    protected EEProtect plugin;

    /**
     * @param plugin   Server plugin
     * @param isCustom Is it a custom block?
     * @param type     Type of material
     * @param pos      Block position
     */
    public BlockData(EEProtect plugin, BlockPosition pos, Material type, boolean isCustom) {
        this.plugin = plugin;
        this.pos = pos;
        this.type = type;
        this.isCustom = isCustom;
    }

    public abstract boolean canModify(Player player);

    public Block toBlock() {
        return getPos().toLocation().getBlock();
    }

}
