package slimeknights.mantle.util;

import com.google.common.collect.Lists;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;

// localization utils
public abstract class LocUtils {
    
    private LocUtils() {
    }
    
    /**
     * Removes all whitespaces from the string and makes it lowercase.
     */
    public static String makeLocString(String unclean) {
        return unclean.toLowerCase(Locale.US).replaceAll(" ", "");
    }
    
    public static String translateRecursive(String key, Object... params) {
        return I18n.translate(I18n.translate(key, params));
    }
    
    public static List<Text> getTooltips(String text) {
        List<Text> list = Lists.newLinkedList();
        if (!I18n.translate(text).equals(text)) {
            String translate = I18n.translate(text);
            if (!I18n.translate(translate).equals(translate)) {
                String[] strings = new TranslatableText(translate).asFormattedString().split("\n");
                
                for (String string : strings) {
                    list.add(new LiteralText(string).formatted(Formatting.GRAY));
                }
            } else {
                String[] strings = new TranslatableText(text).asFormattedString().split("\n");
                
                for (String string : strings) {
                    list.add(new LiteralText(string).formatted(Formatting.GRAY));
                }
            }
        }
        
        return list;
    }
    
    public static String convertNewlines(String line) {
        if (line == null) {
            return null;
        }
        int j;
        while ((j = line.indexOf("\\n")) >= 0) {
            line = line.substring(0, j) + '\n' + line.substring(j + 2);
        }
        
        return line;
    }
}
