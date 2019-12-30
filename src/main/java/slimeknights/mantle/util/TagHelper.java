package slimeknights.mantle.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class TagHelper {
    
    public static int TAG_TYPE_STRING = (new StringTag()).getType();
    public static int TAG_TYPE_COMPOUND = (new CompoundTag()).getType();
    
    private TagHelper() {
    }
    
    /* Generic Tag Operations */
    public static CompoundTag getTagSafe(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return new CompoundTag();
        }
        
        return stack.getTag();
    }
    
    public static CompoundTag getTagSafe(CompoundTag tag, String key) {
        if (tag == null || !tag.containsKey(key)) {
            return new CompoundTag();
        }
        
        return tag.getCompound(key);
    }
    
    public static ListTag getTagListSafe(CompoundTag tag, String key, int type) {
        if (tag == null || !tag.containsKey(key)) {
            return new ListTag();
        }
        
        return tag.getList(key, type);
    }
    
}
