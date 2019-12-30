package slimeknights.mantle.tileentity;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Nameable;
import slimeknights.mantle.util.ItemStackList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Updated version of InventoryLogic in Mantle. Also contains a few bugfixes DOES NOT OVERRIDE createMenu
public abstract class InventoryTileEntity extends MantleTileEntity implements Inventory, NameableContainerProvider, Nameable {
    
    private DefaultedList<ItemStack> inventory;
    protected Text inventoryTitle;
    protected boolean hasCustomName;
    protected int stackSizeLimit;
    
    /**
     * @param name Localization String for the inventory title. Can be overridden through setCustomName
     */
    public InventoryTileEntity(BlockEntityType<?> tileEntityTypeIn, Text name, int inventorySize) {
        this(tileEntityTypeIn, name, inventorySize, 64);
    }
    
    /**
     * @param name Localization String for the inventory title. Can be overridden through setCustomName
     */
    public InventoryTileEntity(BlockEntityType<?> tileEntityTypeIn, Text name, int inventorySize, int maxStackSize) {
        super(tileEntityTypeIn);
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        this.stackSizeLimit = maxStackSize;
        this.inventoryTitle = name;
    }
    
    /* Inventory management */
    
    @Nonnull
    @Override
    public ItemStack getInvStack(int slot) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return ItemStack.EMPTY;
        }
        
        return this.inventory.get(slot);
    }
    
    @Override
    public int getInvMaxStackAmount() {
        return getInventoryStackLimit();
    }
    
    public boolean isStackInSlot(int slot) {
        return !this.getInvStack(slot).isEmpty();
    }
    
    /**
     * Same as resize, but does not call markDirty. Used on loading from NBT
     */
    private void resizeInternal(int size) {
        // save effort if the size did not change
        if (size == this.inventory.size()) {
            return;
        }
        ItemStackList newInventory = ItemStackList.withSize(size);
        
        for (int i = 0; i < size && i < this.inventory.size(); i++) {
            newInventory.set(i, this.inventory.get(i));
        }
        this.inventory = newInventory;
    }
    
    public void resize(int size) {
        this.resizeInternal(size);
        this.markDirtyFast();
    }
    
    @Override
    public int getInvSize() {
        return this.inventory.size();
    }
    
    public int getInventoryStackLimit() {
        return this.stackSizeLimit;
    }
    
    @Override
    public void setInvStack(int slot, @Nonnull ItemStack itemstack) {
        if (slot < 0 || slot >= this.inventory.size()) {
            return;
        }
        
        ItemStack current = this.inventory.get(slot);
        this.inventory.set(slot, itemstack);
        
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit()) {
            itemstack.setCount(this.getInventoryStackLimit());
        }
        if (!ItemStack.areEqualIgnoreDamage(current, itemstack)) {
            this.markDirtyFast();
        }
    }
    
    @Nonnull
    @Override
    public ItemStack takeInvStack(int slot, int quantity) {
        if (quantity <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.getInvStack(slot);
        
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        // whole itemstack taken out
        if (itemStack.getCount() <= quantity) {
            this.setInvStack(slot, ItemStack.EMPTY);
            this.markDirtyFast();
            return itemStack;
        }
        
        // split itemstack
        itemStack = itemStack.split(quantity);
        // slot is empty, set to ItemStack.EMPTY
        // isn't this redundant to the above check?
        if (this.getInvStack(slot).getCount() == 0) {
            this.setInvStack(slot, ItemStack.EMPTY);
        }
        
        this.markDirtyFast();
        // return remainder
        return itemStack;
    }
    
    @Nonnull
    @Override
    public ItemStack removeInvStack(int slot) {
        ItemStack itemStack = this.getInvStack(slot);
        this.setInvStack(slot, ItemStack.EMPTY);
        return itemStack;
    }
    
    @Override
    public boolean isValidInvStack(int slot, @Nonnull ItemStack itemstack) {
        if (slot < this.getInvSize()) {
            return this.inventory.get(slot).isEmpty() || itemstack.getCount() + this.inventory.get(slot).getCount() <= this.getInventoryStackLimit();
        }
        return false;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.inventory.size(); i++) {
            this.inventory.set(i, ItemStack.EMPTY);
        }
    }
    
    @Nonnull
    @Override
    public Text getName() {
        return this.inventoryTitle;
    }
    
    @Override
    public boolean hasCustomName() {
        return this.hasCustomName;
    }
    
    public void setCustomName(Text customName) {
        this.hasCustomName = true;
        this.inventoryTitle = customName;
    }
    
    @Nullable
    @Override
    public Text getCustomName() {
        return this.inventoryTitle;
    }
    
    @Nonnull
    @Override
    public Text getDisplayName() {
        if (this.hasCustomName()) {
            return new LiteralText(this.getName().asFormattedString());
        }
        
        return new TranslatableText(this.getName().asFormattedString());
    }
    
    /* Supporting methods */
    @Override
    public boolean canPlayerUseInv(@Nonnull PlayerEntity entityplayer) {
        // block changed/got broken?
        if (this.world.getBlockEntity(this.pos) != this || this.world.getBlockState(this.pos).getBlock() == Blocks.AIR) {
            return false;
        }
        
        return entityplayer.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64D;
    }
    
    @Override
    public void onInvOpen(@Nonnull PlayerEntity player) {
    
    }
    
    @Override
    public void onInvClose(@Nonnull PlayerEntity player) {
    
    }
    
    
    /* NBT */
    @Override
    public void fromTag(CompoundTag tags) {
        super.fromTag(tags);
        this.resizeInternal(tags.getInt("InventorySize"));
        
        this.readInventoryFromNBT(tags);
        
        if (tags.containsKey("CustomName", 8)) {
            this.inventoryTitle = Text.Serializer.fromJson(tags.getString("CustomName"));
        }
    }
    
    @Nonnull
    @Override
    public CompoundTag toTag(CompoundTag tags) {
        super.toTag(tags);
        
        tags.putInt("InventorySize", this.inventory.size());
        
        this.writeInventoryToNBT(tags);
        
        if (this.hasCustomName()) {
            tags.putString("CustomName", Text.Serializer.toJson(this.inventoryTitle));
        }
        return tags;
    }
    
    /**
     * Writes the contents of the inventory to the tag
     */
    public void writeInventoryToNBT(CompoundTag tag) {
        Inventory inventory = this;
        ListTag nbttaglist = new ListTag();
        
        for (int i = 0; i < inventory.getInvSize(); i++) {
            if (!inventory.getInvStack(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                inventory.getInvStack(i).toTag(itemTag);
                nbttaglist.add(itemTag);
            }
        }
        
        tag.put("Items", nbttaglist);
    }
    
    /**
     * Reads a an inventory from the tag. Overwrites current content
     */
    public void readInventoryFromNBT(CompoundTag tag) {
        ListTag nbttaglist = tag.getList("Items", 10);
        
        int limit = this.getInventoryStackLimit();
        ItemStack stack;
        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundTag itemTag = nbttaglist.getCompoundTag(i);
            int slot = itemTag.getByte("Slot") & 255;
            
            if (slot >= 0 && slot < this.inventory.size()) {
                stack = ItemStack.fromTag(itemTag);
                if (!stack.isEmpty() && stack.getCount() > limit) {
                    stack.setCount(limit);
                }
                this.inventory.set(slot, stack);
            }
        }
    }
    
    /* Default implementations of hardly used methods */
    @Nonnull
    public ItemStack getStackInSlotOnClosing(int slot) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
