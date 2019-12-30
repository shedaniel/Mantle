package slimeknights.mantle.client.book;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;

import javax.annotation.Nullable;

public class StructureBlockAccess implements ViewableWorld {
    
    private final StructureInfo data;
    private final BlockState[][][] structure;
    
    public StructureBlockAccess(StructureInfo data) {
        this.data = data;
        this.structure = data.data;
    }
    
    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }
    
    @Override
    public int getLightLevel(BlockPos blockPos, int i) {
        return 15 << 20 | 15 << 4;
    }
    
    @Override
    public int getLightLevel(LightType type, BlockPos pos) {
        return 15 << 20 | 15 << 4;
    }
    
    @Override
    public BlockState getBlockState(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        
        if (y >= 0 && y < this.structure.length) {
            if (x >= 0 && x < this.structure[y].length) {
                if (z >= 0 && z < this.structure[y][x].length) {
                    int index = y * (this.data.structureLength * this.data.structureWidth) + x * this.data.structureWidth + z;
                    if (index <= this.data.getLimiter()) {
                        return this.structure[y][x][z] != null ? this.structure[y][x][z] : Blocks.AIR.getDefaultState();
                    }
                }
            }
        }
        return Blocks.AIR.getDefaultState();
    }
    
    @Override
    public boolean isAir(BlockPos pos) {
        return this.getBlockState(pos).getBlock() == Blocks.AIR;
    }
    
    @Override
    public Biome getBiome(BlockPos pos) {
        return null;
    }
    
    @Override
    public int getEmittedStrongRedstonePower(BlockPos pos, Direction direction) {
        return 0;
    }
    
    @Override
    public int getLightmapIndex(BlockPos pos, int amount) {
        return 0;
    }
    
    @Nullable
    @Override
    public Chunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
        return null;
    }
    
    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return false;
    }
    
    @Override
    public BlockPos getTopPosition(Type heightmapType, BlockPos pos) {
        return BlockPos.ORIGIN;
    }
    
    @Override
    public int getTop(Type heightmapType, int x, int z) {
        return 0;
    }
    
    @Override
    public int getAmbientDarkness() {
        return 0;
    }
    
    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }
    
    @Override
    public boolean intersectsEntities(Entity entityIn, VoxelShape shape) {
        return false;
    }
    
    @Override
    public boolean isClient() {
        return false;
    }
    
    @Override
    public int getSeaLevel() {
        return 0;
    }
    
    @Override
    public Dimension getDimension() {
        return null;
    }
    
    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }
}
