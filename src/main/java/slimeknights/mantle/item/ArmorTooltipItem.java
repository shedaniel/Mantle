package slimeknights.mantle.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorTooltipItem extends ArmorItem {
    
    public ArmorTooltipItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Item.Settings builder) {
        super(armorMaterial, equipmentSlot, builder);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        TooltipItem.addOptionalTooltip(stack, tooltip);
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
    }
}
