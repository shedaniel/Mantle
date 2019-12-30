package slimeknights.mantle.inventory;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class RestrictedItemSlot extends Slot {
    
    private final Item allowedItem;
    
    public RestrictedItemSlot(Item item, Inventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.allowedItem = item;
    }
    
    @Override
    public boolean canInsert(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == this.allowedItem;
    }
}
