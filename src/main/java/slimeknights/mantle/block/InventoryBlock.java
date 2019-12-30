package slimeknights.mantle.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import slimeknights.mantle.tileentity.InventoryTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class InventoryBlock extends BlockWithEntity {
    
    protected InventoryBlock(Block.Settings builder) {
        super(builder);
    }
    
    // inventories usually need a blockEntity
    @Override
    public boolean hasBlockEntity() {
        return true;
    }
    
    @Nullable
    @Override
    public abstract BlockEntity createBlockEntity(BlockView view);
    
    /**
     * Called when the block is activated. Return true if a GUI is opened, false if the block has no GUI.
     */
    protected abstract boolean openGui(PlayerEntity player, World world, BlockPos pos);
    
    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult rayTraceResult) {
        if (player.isSneaking()) {
            return false;
        }
        
        if (!world.isClient) {
            return this.openGui(player, world, pos);
        }
        
        return true;
    }
    
    @Override
    public void onPlaced(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(worldIn, pos, state, placer, stack);
        
        // set custom name from named stack
        if (stack.hasCustomName()) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            
            if (tileentity instanceof InventoryTileEntity) {
                ((InventoryTileEntity) tileentity).setCustomName(stack.getName());
            }
        }
    }
    
    @Override
    public void onBlockRemoved(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onBlockRemoved(state, worldIn, pos, newState, isMoving);
            
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            
            if (tileentity instanceof InventoryTileEntity) {
                ItemScatterer.spawn(worldIn, pos, (Inventory) tileentity);
                worldIn.updateHorizontalAdjacent(pos, this);
            }
        }
    }
    
    // BlockContainer sets this to invisible
    // we need model for standard forge rendering
    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}