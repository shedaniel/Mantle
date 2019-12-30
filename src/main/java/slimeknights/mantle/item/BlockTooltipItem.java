package slimeknights.mantle.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockTooltipItem extends BlockItem {
    public BlockTooltipItem(Block blockIn, Item.Settings builder) {
        super(blockIn, builder);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        TooltipItem.addOptionalTooltip(stack, tooltip);
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
    }
}
