package slimeknights.mantle.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// Item with automatic tooltip support
public class TooltipItem extends Item {
    
    public TooltipItem(Settings properties) {
        super(properties);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        addOptionalTooltip(stack, tooltip);
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
    }
    
    public static void addOptionalTooltip(ItemStack stack, List<Text> tooltip) {
        String translationKey = stack.getTranslationKey() + ".tooltip";
        
        if (!I18n.translate(translationKey).equals(translationKey)) {
            String translate = I18n.translate(translationKey);
            if (!I18n.translate(translate).equals(translate)) {
                String[] strings = new TranslatableText(translate).asFormattedString().split("\n");
                
                for (String string : strings) {
                    tooltip.add(new LiteralText(string).formatted(Formatting.GRAY));
                }
            } else {
                String[] strings = new TranslatableText(translationKey).asFormattedString().split("\n");
                
                for (String string : strings) {
                    tooltip.add(new LiteralText(string).formatted(Formatting.GRAY));
                }
            }
        }
    }
}
