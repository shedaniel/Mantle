package slimeknights.mantle.inventory;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Slot that can only be taken out of
 */
public class OutSlot extends Slot {
    
    public OutSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }
    
    @Override
    public boolean canInsert(@Nonnull ItemStack stack) {
        return false;
    }
}
