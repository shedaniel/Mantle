package slimeknights.mantle.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTab extends ItemGroup {
    
    private ItemStack icon;
    
    // a vanilla icon in case the other one isn't present
    public CreativeTab(String label, ItemStack backupIcon) {
        super(ItemGroup.GROUPS.length - 1, label);
        
        this.icon = backupIcon;
    }
    
    public static CreativeTab create(String label, ItemStack backupIcon) {
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        return new CreativeTab(label, backupIcon);
    }
    
    public void setDisplayIcon(ItemStack displayIcon) {
        if (!displayIcon.isEmpty()) {
            this.icon = displayIcon;
        }
    }
    
    @Nonnull
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack getIcon() {
        return this.icon;
    }
    
    @Nonnull
    @Environment(EnvType.CLIENT)
    @Override
    public ItemStack createIcon() {
        return this.icon;
    }
}
