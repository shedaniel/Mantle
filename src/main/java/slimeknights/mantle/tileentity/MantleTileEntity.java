package slimeknights.mantle.tileentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class MantleTileEntity extends BlockEntity {
    
    public MantleTileEntity(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    public boolean isClient() {
        return this.getWorld() != null && this.getWorld().isClient;
    }
    
    /**
     * Marks the chunk dirty without performing comparator updates or block state checks
     * Used since most of our markDirty calls only adjust TE data
     */
    public void markDirtyFast() {
        if (world != null) {
            world.markDirty(pos, this);
        }
    }
}