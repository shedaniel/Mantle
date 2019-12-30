package slimeknights.mantle.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiModuleContainer<T extends BlockEntity & Inventory> extends BaseContainer<T> {
    
    public List<Container> subContainers = Lists.newArrayList();
    
    // lookup used to redirect slot specific things to the appropriate container
    protected Map<Integer, Container> slotContainerMap = Maps.newHashMap();
    protected Map<Container, Pair<Integer, Integer>> subContainerSlotRanges = Maps.newHashMap();
    protected int subContainerSlotStart = -1;
    protected Set<Container> shiftClickContainers = Sets.newHashSet();
    
    public MultiModuleContainer(ContainerType<?> containerType, int windowId, T tile) {
        super(containerType, windowId, tile);
    }
    
    /**
     * @param subcontainer        The container to add
     * @param preferForShiftClick If true shift clicking on slots of the main-container will try to move to this module before the player inventory
     */
    public void addSubContainer(Container subcontainer, boolean preferForShiftClick) {
        if (this.subContainers.isEmpty()) {
            this.subContainerSlotStart = this.slotList.size();
        }
        this.subContainers.add(subcontainer);
        
        if (preferForShiftClick) {
            this.shiftClickContainers.add(subcontainer);
        }
        
        int begin = this.slotList.size();
        for (Object slot : subcontainer.slotList) {
            WrapperSlot wrapper = new WrapperSlot((Slot) slot);
            this.addSlot(wrapper);
            this.slotContainerMap.put(wrapper.id, subcontainer);
        }
        int end = this.slotList.size();
        this.subContainerSlotRanges.put(subcontainer, Pair.of(begin, end));
    }
    
    public <TC extends Container> TC getSubContainer(Class<TC> clazz) {
        return this.getSubContainer(clazz, 0);
    }
    
    public <TC extends Container> TC getSubContainer(Class<TC> clazz, int index) {
        for (Container sub : this.subContainers) {
            if (clazz.isAssignableFrom(sub.getClass())) {
                index--;
            }
            if (index < 0) {
                return (TC) sub;
            }
        }
        
        return null;
    }
    
    public Container getSlotContainer(int slotNumber) {
        if (this.slotContainerMap.containsKey(slotNumber)) {
            return this.slotContainerMap.get(slotNumber);
        }
        
        return this;
    }
    
    @Override
    public boolean canUse(@Nonnull PlayerEntity playerIn) {
        // check if subcontainers are valid
        for (Container sub : this.subContainers) {
            if (!sub.canUse(playerIn)) {
                return false;
            }
        }
        
        return super.canUse(playerIn);
    }
    
    @Override
    public void close(PlayerEntity playerIn) {
        for (Container sub : this.subContainers) {
            sub.close(playerIn);
        }
        
        super.close(playerIn);
    }
    
    @Nonnull
    @Override
    public ItemStack onSlotClick(int slotId, int dragType, SlotActionType type, PlayerEntity player) {
        if (slotId == -999 && type == SlotActionType.QUICK_CRAFT) {
            for (Container container : this.subContainers) {
                container.onSlotClick(slotId, dragType, type, player);
            }
        }
        
        return super.onSlotClick(slotId, dragType, type, player);
    }
    
    // More sophisticated version of the one in BaseContainer
    // Takes submodules into account when shiftclicking!
    
    @Nonnull
    @Override
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.slotList.get(index);
        
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        
        ItemStack ret = slot.getStack().copy();
        ItemStack itemstack = slot.getStack().copy();
        
        Container container = this.getSlotContainer(index);
        boolean nothingDone = true;
        
        // Is the slot from a module?
        if (container != this) {
            // Try moving module -> tile inventory
            nothingDone &= this.moveToTileInventory(itemstack);
            
            // Try moving module -> player inventory
            nothingDone &= this.moveToPlayerInventory(itemstack);
        }
        // Is the slot from the tile?
        else if (index < this.subContainerSlotStart || (index < this.playerInventoryStart && this.subContainerSlotStart < 0)) {
            // Try moving tile -> preferred modules
            nothingDone &= this.refillAnyContainer(itemstack, this.subContainers);
            
            // Try moving module -> player inventory
            nothingDone &= this.moveToPlayerInventory(itemstack);
            
            // Try moving module -> all submodules
            nothingDone &= this.moveToAnyContainer(itemstack, this.subContainers);
        }
        // Slot is from the player inventory (if present)
        else if (index >= this.playerInventoryStart && this.playerInventoryStart >= 0) {
            // Try moving player -> tile inventory
            nothingDone &= this.moveToTileInventory(itemstack);
            
            // try moving player -> modules
            nothingDone &= this.moveToAnyContainer(itemstack, this.subContainers);
        }
        // you violated some assumption or something. Shame on you.
        else {
            return ItemStack.EMPTY;
        }
        
        if (nothingDone) {
            return ItemStack.EMPTY;
        }
        
        return this.notifySlotAfterTransfer(playerIn, itemstack, ret, slot);
    }
    
    @Nonnull
    protected ItemStack notifySlotAfterTransfer(PlayerEntity player, @Nonnull ItemStack stack, @Nonnull ItemStack original, Slot slot) {
        // notify slot
        slot.onStackChanged(stack, original);
        
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        
        // update slot we pulled from
        slot.setStack(stack);
        slot.onTakeItem(player, stack);
        
        if (slot.hasStack() && slot.getStack().isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        }
        
        return original;
    }
    
    protected boolean moveToTileInventory(@Nonnull ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        }
        
        int end = this.subContainerSlotStart;
        if (end < 0) {
            end = this.playerInventoryStart;
        }
        return !this.insertItem(itemstack, 0, end, false);
    }
    
    protected boolean moveToPlayerInventory(@Nonnull ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        }
        
        return this.playerInventoryStart > 0 && !this.insertItem(itemstack, this.playerInventoryStart, this.slotList.size(), true);
    }
    
    protected boolean moveToAnyContainer(@Nonnull ItemStack itemstack, Collection<Container> containers) {
        if (itemstack.isEmpty()) {
            return false;
        }
        
        for (Container submodule : containers) {
            if (this.moveToContainer(itemstack, submodule)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean moveToContainer(@Nonnull ItemStack itemstack, Container container) {
        Pair<Integer, Integer> range = this.subContainerSlotRanges.get(container);
        return !this.insertItem(itemstack, range.getLeft(), range.getRight(), false);
    }
    
    protected boolean refillAnyContainer(@Nonnull ItemStack itemstack, Collection<Container> containers) {
        if (itemstack.isEmpty()) {
            return false;
        }
        
        for (Container submodule : containers) {
            if (this.refillContainer(itemstack, submodule)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean refillContainer(@Nonnull ItemStack itemstack, Container container) {
        Pair<Integer, Integer> range = this.subContainerSlotRanges.get(container);
        return !this.mergeItemStackRefill(itemstack, range.getLeft(), range.getRight(), false);
    }
    
    /**
     * Searches for a sidechest to display in the UI
     */
    public <TE extends BlockEntity> TE detectTE(Class<TE> clazz) {
        return ObjectUtils.firstNonNull(this.detectChest(this.pos.north(), clazz), this.detectChest(this.pos.east(), clazz), this.detectChest(this.pos.south(), clazz), this.detectChest(this.pos.west(), clazz));
    }
    
    private <TE extends BlockEntity> TE detectChest(BlockPos pos, Class<TE> clazz) {
        BlockEntity te = this.world.getBlockEntity(pos);
        
        if (te != null && clazz.isAssignableFrom(te.getClass())) {
            return (TE) te;
        }
        return null;
    }
}
