package slimeknights.mantle.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/** Slot that can only be taken out of */
public class SlotOut extends Slot {

  public SlotOut(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return false;
  }
}
