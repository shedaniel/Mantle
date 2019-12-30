package slimeknights.mantle.common;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.item.BlockTooltipItem;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface IRegisterUtil {
    /* General methods */
    
    /**
     * Gets the mod ID to use for registrations
     *
     * @return String mod ID
     */
    String getModId();
    
    /**
     * Gets a resource location for this mod
     *
     * @param name Resource name
     * @return Resource location in this mod
     */
    default Identifier getResource(String name) {
        return new Identifier(this.getModId(), name);
    }
    
    /**
     * Registers a thing to a Forge registry
     *
     * @param registry Registry to use
     * @param thing    Thing to register
     * @param name     Name to register under
     * @param <I>      Instance type
     * @param <T>      Registry type
     * @return Thing that was registered
     */
    default <I extends T, T> I register(Registry<T> registry, I thing, String name) {
        return this.register(registry, thing, this.getResource(name));
    }
    
    /**
     * Registers a thing to a Forge registry
     *
     * @param registry Registry to use
     * @param thing    Thing to register
     * @param location Registration location
     * @param <I>      Instance type
     * @param <T>      Registry type
     * @return Thing that was registered
     */
    default <I extends T, T> I register(Registry<T> registry, I thing, Identifier location) {
        return Registry.register(registry, location, thing);
    }
    
    
    /* Special case methods */
    
    /**
     * Registers a generic item block for a block
     *
     * @param registry Item registry
     * @param block    Block to get item block
     * @return Registered item block
     */
    default BlockItem registerBlockItem(Registry<Item> registry, Block block) {
        BlockItem itemBlock = new BlockTooltipItem(block, new Item.Settings());
        return this.register(registry, itemBlock, Registry.BLOCK.getId(block));
    }
    
    /**
     * Registers a generic item block for a block
     *
     * @param registry Item registry
     * @param block    Block to get item block
     * @param group    Group for the item block
     * @return Registered item block
     */
    default BlockItem registerBlockItem(Registry<Item> registry, Block block, ItemGroup group) {
        BlockItem itemBlock = new BlockTooltipItem(block, new Item.Settings().group(group));
        return this.register(registry, itemBlock, Registry.BLOCK.getId(block));
    }
    
    /**
     * Registers an item block, using the block as the registry name
     *
     * @param registry  Item registry
     * @param itemBlock Item block instance to register
     * @return Registered item block
     */
    default <T extends BlockItem> T registerBlockItem(Registry<Item> registry, T itemBlock) {
        return this.register(registry, itemBlock, Registry.BLOCK.getId(itemBlock.getBlock()));
    }
    
    /**
     * Registers a tile entity class to the game
     *
     * @param registry    Tile entity registry
     * @param constructor Tile entity constructor instance
     * @param name        Registry name to use
     */
    default <T extends BlockEntity> BlockEntityType<T> registerTE(Registry<BlockEntityType<?>> registry, Supplier<T> constructor, String name, Block... validBlocks) {
        BlockEntityType<T> type = BlockEntityType.Builder.create(constructor, validBlocks).build(null);
        return this.register(registry, type, name);
    }
    
    /* List methods */
    
    /**
     * Registers a list of blocks from a list of variants. Commonly used with Enum.values()
     *
     * @param registry    Block registry instance
     * @param constructor Block constructor
     * @param props       Block properties for registration
     * @param variants    Array of variants
     * @param baseName    Base name for registration
     * @param <B>         Block type
     * @return List of blocks registered
     */
    default <B extends Block> List<B> registerBlocks(Registry<Block> registry, Function<Block.Settings, B> constructor, StringIdentifiable[] variants, Block.Settings props, String baseName) {
        return Arrays.stream(variants).map((s) -> {
            return this.register(registry, constructor.apply(props), baseName + "_" + s.asString());
        }).collect(Collectors.toList());
    }
    
    /**
     * Registers a generic item block for each block in a list
     *
     * @param registry Item registry
     * @param blocks   Blocks needing item blocks
     * @return Registered item blocks
     */
    default List<BlockItem> registerBlockItems(Registry<Item> registry, List<? extends Block> blocks) {
        return blocks.stream().map((block) -> {
            return this.registerBlockItem(registry, block);
        }).collect(Collectors.toList());
    }
    
    /**
     * Registers a generic item block for each block in a list
     *
     * @param registry Item registry
     * @param blocks   Blocks needing item blocks
     * @param group    Group for the item blocks
     * @return Registered item blocks
     */
    default List<BlockItem> registerBlockItems(Registry<Item> registry, List<? extends Block> blocks, ItemGroup group) {
        return blocks.stream().map((block) -> {
            return this.registerBlockItem(registry, block, group);
        }).collect(Collectors.toList());
    }
    
    /**
     * Registers a specific item block for each block in a list
     *
     * @param registry    Item registry
     * @param blocks      Blocks to create item blocks for
     * @param constructor ItemBlock constructor
     * @return Registered item block
     */
    default <T extends BlockItem> List<T> registerBlockItems(Registry<Item> registry, List<? extends Block> blocks, Function<Block, T> constructor) {
        return blocks.stream().map((block) -> {
            return this.registerBlockItem(registry, constructor.apply(block));
        }).collect(Collectors.toList());
    }
}
