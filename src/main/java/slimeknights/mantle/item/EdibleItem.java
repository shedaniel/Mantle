package slimeknights.mantle.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class EdibleItem extends Item {
    
    private boolean displayEffectsTooltip; // set to false to not display effects of food in tooltip
    
    public EdibleItem(FoodComponent foodIn, ItemGroup itemGroup) {
        this(foodIn, itemGroup, true);
    }
    
    public EdibleItem(FoodComponent foodIn, ItemGroup itemGroup, boolean displayEffectsTooltip) {
        super(new Settings().food(foodIn).group(itemGroup));
        this.displayEffectsTooltip = displayEffectsTooltip;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TooltipItem.addOptionalTooltip(stack, tooltip);
        
        if (this.displayEffectsTooltip) {
            for (Pair<StatusEffectInstance, Float> pair : stack.getItem().getFoodComponent().getStatusEffects()) {
                if (pair.getLeft() != null) {
                    tooltip.add(new LiteralText(I18n.translate(pair.getLeft().getTranslationKey()).trim()).formatted(Formatting.GRAY));
                }
            }
        }
        
        super.appendTooltip(stack, world, tooltip, context);
    }
}
