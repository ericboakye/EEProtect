package io.apollosoftware.eeprotect.block.blocks;


import io.apollosoftware.eeprotect.EEProtect;
import io.apollosoftware.eeprotect.PluginMessage;
import io.apollosoftware.eeprotect.block.BlockData;
import io.apollosoftware.eeprotect.block.BlockPosition;
import io.apollosoftware.eeprotect.block.InteractiveBlock;
import io.apollosoftware.eeprotect.data.UserData;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Class created by xenojava on 1/3/2016.
 */
public class UserProtectedBlock extends BlockData implements InteractiveBlock {

    @Getter
    protected UserData owner;

    @Getter
    protected Block block;

    /**
     * @param owner Owner of protected block
     * @param pos   Block position
     * @param block Block
     */
    public UserProtectedBlock(EEProtect plugin, UserData owner, BlockPosition pos, Block block) {
        super(plugin, pos, block.getType(), false);

        this.owner = owner;
        this.block = block;
    }

    public boolean canModify(Player player) {
        return owner.canAccess(player);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!canModify(player)) {
            new PluginMessage("CANNOT_MODIFY_BLOCKS").param(owner.getName()).sendTo(event.getPlayer());
            event.setCancelled(true);
        }
    }

}
