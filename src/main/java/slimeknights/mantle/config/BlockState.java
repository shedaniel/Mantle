package slimeknights.mantle.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class BlockState {
    
    private static final BlockState MISSING = new BlockState(Blocks.AIR, "");
    
    public transient static final TypeSerializer<BlockState> SERIALIZER = new TypeSerializer<BlockState>() {
        @Override
        public BlockState deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) {
            String val = configurationNode.getString();
            String[] parts = val.split(":");
            
            Block block = Registry.BLOCK.get(new Identifier(parts[0], parts[1]));
            if (block == Blocks.AIR) {
                return MISSING;
            }
            String state = "";
            if (parts.length > 2) {
                state = parts[2];
            }
            
            return new BlockState(block, state);
        }
        
        @Override
        public void serialize(TypeToken<?> typeToken, BlockState blockMeta, ConfigurationNode configurationNode) {
            configurationNode.setValue(blockMeta.toString());
        }
    };
    
    public Block block;
    public String state;
    
    public BlockState() {
    
    }
    
    public BlockState(Block block, String state) {
        this.block = block;
        this.state = state;
    }
    
    public static BlockState of(net.minecraft.block.BlockState state) {
        return new BlockState(state.getBlock(), state.toString());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        
        BlockState blockState = (BlockState) o;
        
        if (!this.state.equalsIgnoreCase(blockState.state)) {
            return false;
        }
        return this.block != null ? this.block.equals(blockState.block) : blockState.block == null;
        
    }
    
    @Override
    public int hashCode() {
        int result = this.block != null ? this.block.hashCode() : 0;
        result = 31 * result + this.state.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        String val = Registry.BLOCK.getId(this.block).toString();
        if (!this.state.equals("")) {
            val += ":" + this.state;
        }
        return val;
    }
}
