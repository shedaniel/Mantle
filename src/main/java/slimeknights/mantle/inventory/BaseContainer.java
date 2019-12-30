package slimeknights.mantle.inventory;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import slimeknights.mantle.util.SlimeknightException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Same as Container but provides some extra functionality to simplify things
 */
public abstract class BaseContainer<T extends BlockEntity> extends Container {
    
    protected double maxDist = 8 * 8; // 8 blocks
    protected T tile;
    protected final Block originalBlock; // used to check if the block we interacted with got broken
    protected final BlockPos pos;
    protected final World world;
    
    public List<Container> subContainers = Lists.newArrayList();
    
    public BaseContainer(ContainerType<?> containerType, int windowId, T tile) {
        this(containerType, windowId, tile, null);
    }
    
    public BaseContainer(ContainerType<?> containerType, int windowId, T tile, Direction invDir) {
        super(containerType, windowId);
        this.tile = tile;
        this.world = tile.getWorld();
        this.pos = tile.getPos();
        this.originalBlock = this.world.getBlockState(this.pos).getBlock();
    }
    
    public void syncOnOpen(ServerPlayerEntity playerOpened) {
        // find another player that already has the gui for this tile open
        ServerWorld server = playerOpened.getServerWorld();
        for (PlayerEntity player : server.getPlayers()) {
            if (player == playerOpened) {
                continue;
            }
            if (player.container instanceof BaseContainer) {
                if (this.sameGui((BaseContainer<T>) player.container)) {
                    this.syncWithOtherContainer((BaseContainer<T>) player.container, playerOpened);
                    return;
                }
            }
        }
        
        // no player has a container open for the tile
        this.syncNewContainer(playerOpened);
    }
    
    public T getTile() {
        return this.tile;
    }
    
    /**
     * Called when the container is opened and another player already has a container for this tile open
     * Sync to the same state here.
     */
    protected void syncWithOtherContainer(BaseContainer<T> otherContainer, ServerPlayerEntity player) {
    }
    
    /**
     * Called when the container is opened and no other player has it open.
     * Set the default state here.
     */
    protected void syncNewContainer(ServerPlayerEntity player) {
    }
    
    public boolean sameGui(BaseContainer otherContainer) {
        return this.tile == otherContainer.tile;
    }
    
    @Override
    public boolean canUse(@Nonnull PlayerEntity playerIn) {
        Block block = this.world.getBlockState(this.pos).getBlock();
        // does the block we interacted with still exist?
        if (block == Blocks.AIR || block != this.originalBlock) {
            return false;
        }
        
        // too far away from block?
        return playerIn.squaredDistanceTo((double) this.pos.getX() + 0.5d, (double) this.pos.getY() + 0.5d, (double) this.pos.getZ() + 0.5d) <= this.maxDist;
    }

  /*public String getInventoryDisplayName() {
    INameable nameable = null;
    if (this.itemHandler.orElse(new EmptyHandler()) instanceof InvWrapper) {
      nameable = ((InvWrapper) this.itemHandler.orElse(new EmptyHandler())).getInv();
      // if the inventory doesn't have a name fall back to checking the tileentity
      if (nameable.getDisplayName() == null) {
        nameable = null;
      }
    }
    if(nameable == null && this.tile instanceof INameable) {
      nameable = (INameable) this.tile;
    }
    if (nameable != null) {
      ITextComponent textName = nameable.getDisplayName();
      return textName != null ? textName.getFormattedText() : nameable.getName().toString();
    }
    return null;
  }*/
    
    // standard yOffset calculation for chestlike inventories:
    // yOffset = (numRows - 4) * 18; (the -4 because of the 3 rows of inventory + 1 row of hotbar)
    
    protected int playerInventoryStart = -1;
    
    /**
     * Draws the player inventory starting at the given position
     *
     * @param playerInventory The players inventory
     * @param xCorner         Default Value: 8
     * @param yCorner         Default Value: (rows - 4) * 18 + 103
     */
    protected void addPlayerInventory(PlayerInventory playerInventory, int xCorner, int yCorner) {
        int index = 9;
        
        int start = this.slotList.size();
        
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, index, xCorner + col * 18, yCorner + row * 18));
                index++;
            }
        }
        
        index = 0;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, index, xCorner + col * 18, yCorner + 58));
            index++;
        }
        
        this.playerInventoryStart = start;
    }
    
    @Nonnull
    @Override
    protected Slot addSlot(Slot slotIn) {
        if (this.playerInventoryStart >= 0) {
            throw new SlimeknightException("BaseContainer: Player inventory has to be last slots. Add all slots before adding the player inventory.");
        }
        return super.addSlot(slotIn);
    }
    
    @Nonnull
    @Override
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        // we can only support inventory <-> playerInventory
        if (this.playerInventoryStart < 0) {
            // so we don't do anything if no player inventory is present because we don't know what to do
            return ItemStack.EMPTY;
        }
        
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(index);
        
        // slot that was clicked on not empty?
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int end = this.slotList.size();
            
            // Is it a slot in the main inventory? (aka not player inventory)
            if (index < this.playerInventoryStart) {
                // try to put it into the player inventory (if we have a player inventory)
                if (!this.insertItem(itemstack1, this.playerInventoryStart, end, true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Slot is in the player inventory (if it exists), transfer to main inventory
            else if (!this.insertItem(itemstack1, 0, this.playerInventoryStart, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemstack;
    }
    
    // Fix for a vanilla bug: doesn't take Slot.getMaxStackSize into account
    @Override
    protected boolean insertItem(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        boolean ret = this.mergeItemStackRefill(stack, startIndex, endIndex, useEndIndex);
        if (!stack.isEmpty() && stack.getCount() > 0) {
            ret |= this.mergeItemStackMove(stack, startIndex, endIndex, useEndIndex);
        }
        return ret;
    }
    
    // only refills items that are already present
    protected boolean mergeItemStackRefill(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        if (stack.getCount() <= 0) {
            return false;
        }
        
        boolean flag1 = false;
        int k = startIndex;
        
        if (useEndIndex) {
            k = endIndex - 1;
        }
        
        Slot slot;
        ItemStack itemstack1;
        
        if (stack.isStackable()) {
            while (stack.getCount() > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)) {
                slot = this.slotList.get(k);
                itemstack1 = slot.getStack();
                
                if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && ItemStack.areTagsEqual(stack, itemstack1) && this.canInsertIntoSlot(stack, slot)) {
                    int l = itemstack1.getCount() + stack.getCount();
                    int limit = Math.min(stack.getMaxCount(), slot.getMaxStackAmount(stack));
                    
                    if (l <= limit) {
                        stack.setCount(0);
                        itemstack1.setCount(l);
                        slot.markDirty();
                        flag1 = true;
                    } else if (itemstack1.getCount() < limit) {
                        stack.decrement(limit - itemstack1.getCount());
                        itemstack1.setCount(limit);
                        slot.markDirty();
                        flag1 = true;
                    }
                }
                
                if (useEndIndex) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        
        return flag1;
    }
    
    // only moves items into empty slots
    protected boolean mergeItemStackMove(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
        if (stack.getCount() <= 0) {
            return false;
        }
        
        boolean flag1 = false;
        int k;
        
        if (useEndIndex) {
            k = endIndex - 1;
        } else {
            k = startIndex;
        }
        
        while (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex) {
            Slot slot = this.slotList.get(k);
            ItemStack itemstack1 = slot.getStack();
            
            if (itemstack1.isEmpty() && slot.canInsert(stack) && this.canInsertIntoSlot(stack, slot)) {
                int limit = slot.getMaxStackAmount(stack);
                ItemStack stack2 = stack.copy();
                if (stack2.getCount() > limit) {
                    stack2.setCount(limit);
                    stack.decrement(limit);
                } else {
                    stack.setCount(0);
                }
                slot.setStack(stack2);
                slot.markDirty();
                flag1 = true;
                
                if (stack.isEmpty()) {
                    break;
                }
            }
            
            if (useEndIndex) {
                --k;
            } else {
                ++k;
            }
        }
        
        return flag1;
    }
    
}
