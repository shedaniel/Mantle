package slimeknights.mantle.client.screen.book;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.book.data.BookmarkData;

@Environment(EnvType.CLIENT)
public class BookmarkButton extends ButtonWidget {
    
    private static final Identifier TEX_BOOK = new Identifier("mantle:textures/gui/book.png");
    
    public static final int WIDTH = 31;
    public static final int HEIGHT = 9;
    public static final int TEX_X = 0, TEX_Y = 400;
    public static final int ADD_W = 5, ADD_H = 5, ADD_X = 32, ADD_Y = 402;
    
    public static final float TEXT_SCALE = 0.5F;
    
    public int type = 0;
    
    public final BookmarkData data;
    
    public BookmarkButton(BookmarkData data, PressAction iPressable) {
        super(-500, -500, WIDTH, HEIGHT, "", iPressable);
        
        this.data = data;
    }
    
    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        int tex_y = TEX_Y + HEIGHT * this.type;
        
        minecraft.getTextureManager().bindTexture(TEX_BOOK);
        
        float r = ((this.data.color >> 16) & 0xff) / 255.F;
        float g = ((this.data.color >> 8) & 0xff) / 255.F;
        float b = (this.data.color & 0xff) / 255.F;
        
        GlStateManager.color3f(r, g, b);
        blit(this.x, this.y, this.width, this.height, TEX_X, tex_y, WIDTH, HEIGHT, 512, 512);
        
        if (this.data.text != null && !this.data.text.isEmpty()) {
            TextDataRenderer.drawScaledString(minecraft.textRenderer, this.data.text, this.x + 1, this.y + this.height / 2 - minecraft.textRenderer.fontHeight * TEXT_SCALE / 2 + 1, 0xFFFFFFFF, true, TEXT_SCALE);
        }
        
        GlStateManager.color3f(1F, 1F, 1F);
        
        if (this.data.page.equals("ADD")) {
            blit(this.x + this.width / 2 - ADD_W / 2, this.y + this.height / 2 - ADD_H / 2, ADD_X, ADD_Y, ADD_W, ADD_H, 512, 512);
        }
    }
}
