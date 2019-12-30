package slimeknights.mantle.util;

import com.google.common.collect.Lists;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Rect2i;
import net.minecraft.util.Identifier;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.screen.MultiModuleScreen;

import java.util.List;

public class ReiPlugin implements REIPluginV0 {
    
    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(Mantle.modId, "internal");
    }
    
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("3.1");
    }
    
    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        displayHelper.getBaseBoundsHandler().registerExclusionZones(MultiModuleScreen.class, aBoolean -> {
            List<Rectangle> rectangles = Lists.newArrayList();
            for (Object obj : ((MultiModuleScreen) MinecraftClient.getInstance().currentScreen).getModuleAreas()) {
                Rect2i area = (Rect2i) obj;
                rectangles.add(new Rectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
            }
            return rectangles;
        });
    }
    
}
