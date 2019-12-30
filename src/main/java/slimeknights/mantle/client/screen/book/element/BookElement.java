package slimeknights.mantle.client.screen.book.element;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import slimeknights.mantle.client.screen.book.BookScreen;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public abstract class BookElement extends DrawableHelper {
    
    public BookScreen parent;
    
    protected MinecraftClient mc = MinecraftClient.getInstance();
    protected TextureManager renderEngine = this.mc.getTextureManager();
    
    public int x, y;
    
    public BookElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void draw(int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer);
    
    public void drawOverlay(int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
    }
    
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    
    }
    
    public void mouseClickMove(double mouseX, double mouseY, int clickedMouseButton) {
    
    }
    
    public void mouseReleased(double mouseX, double mouseY, int clickedMouseButton) {
    
    }
    
    public void mouseDragged(int clickX, int clickY, int mx, int my, int lastX, int lastY, int button) {
    
    }
    
    public void renderToolTip(TextRenderer fontRenderer, ItemStack stack, int x, int y) {
        if (stack != null) {
            List<Text> list = stack.getTooltip(this.mc.player, this.mc.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            List<String> list1 = list.stream().map(Text::asFormattedString).collect(Collectors.toList());
            
            drawHoveringText(list1, x, y);
            GuiLighting.disable();
        }
    }
    
    public void drawHoveringText(List<String> textLines, int x, int y) {
        parent.renderTooltip(textLines, x, y);
        GuiLighting.disable();
    }
}
