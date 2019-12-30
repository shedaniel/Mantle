package slimeknights.mantle.client.book;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;

public class BookHelper {
    
    public static String getSavedPage(ItemStack item) {
        if (!item.isEmpty() && item.hasTag()) {
            CompoundTag mantleBook = item.getTag().getCompound("mantle").getCompound("book");
            
            if (mantleBook.containsKey("page", Arrays.asList(Tag.TYPES).indexOf("STRING"))) {
                return mantleBook.getString("page");
            }
        }
        
        return "";
    }
    
    public static void writeSavedPage(ItemStack item, String page) {
        CompoundTag compound = item.getTag();
        
        if (compound == null) {
            compound = new CompoundTag();
        }
        
        CompoundTag mantle = compound.getCompound("mantle");
        CompoundTag book = mantle.getCompound("book");
        
        book.putString("page", page);
        
        mantle.put("book", book);
        compound.put("mantle", mantle);
        item.setTag(compound);
    }
    
}
