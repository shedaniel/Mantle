package slimeknights.mantle.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Used to wrap the slots inside Modules/Subcontainers
 */
public class WrapperSlot extends Slot {
    
    public final Slot parent;
    
    public WrapperSlot(Slot slot) {
        super(slot.inventory, slot.id, slot.xPosition, slot.yPosition);
        this.parent = slot;
    }
    
    @Override
    public void onStackChanged(ItemStack p_75220_1_, ItemStack p_75220_2_) {
        this.parent.onStackChanged(p_75220_1_, p_75220_2_);
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return this.parent.canInsert(stack);
    }
    
    @Override
    public boolean canTakeItems(PlayerEntity playerIn) {
        return this.parent.canTakeItems(playerIn);
    }
    
    @Override
    public void setStack(@Nonnull ItemStack stack) {
        this.parent.setStack(stack);
    }
    
    @Override
    @Nonnull
    public ItemStack onTakeItem(PlayerEntity playerIn, ItemStack stack) {
        this.parent.onTakeItem(playerIn, stack);
        
        return stack;
    }
    
    @Override
    @Nonnull
    public ItemStack getStack() {
        return this.parent.getStack();
    }
    
    @Override
    public boolean hasStack() {
        return this.parent.hasStack();
    }
    
    @Override
    public int getMaxStackAmount() {
        return this.parent.getMaxStackAmount();
    }
    
    @Override
    public int getMaxStackAmount(ItemStack stack) {
        return this.parent.getMaxStackAmount(stack);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public String getBackgroundSprite() {
        return this.parent.getBackgroundSprite();
    }
    
    @Nonnull
    @Override
    public ItemStack takeStack(int amount) {
        return this.parent.takeStack(amount);
    }
    
    @Override
    public void markDirty() {
        this.parent.markDirty();
    }
    
    @Override
    public boolean doDrawHoveringEffect() {
        return this.parent.doDrawHoveringEffect();
    }
}
