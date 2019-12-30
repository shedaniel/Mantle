package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.MathHelper;
import slimeknights.mantle.client.book.action.StringActionProcessor;

import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ElementItem extends SizedBookElement {
    
    public static final int ITEM_SIZE_HARDCODED = 16;
    public static final int ITEM_SWITCH_TICKS = 90;
    
    public DefaultedList<ItemStack> itemCycle;
    public float scale;
    public String action;
    public List<String> tooltip;
    
    public int renderTick = 0;
    public int currentItem = 0;
    
    public ElementItem(int x, int y, float scale, Item item) {
        this(x, y, scale, new ItemStack(item));
    }
    
    public ElementItem(int x, int y, float scale, Block item) {
        this(x, y, scale, new ItemStack(item));
    }
    
    public ElementItem(int x, int y, float scale, ItemStack item) {
        this(x, y, scale, new ItemStack[]{item});
    }
    
    public ElementItem(int x, int y, float scale, Collection<ItemStack> itemCycle) {
        this(x, y, scale, itemCycle.toArray(new ItemStack[itemCycle.size()]));
    }
    
    public ElementItem(int x, int y, float scale, Collection<ItemStack> itemCycle, String action) {
        this(x, y, scale, itemCycle.toArray(new ItemStack[itemCycle.size()]), action);
    }
    
    public ElementItem(int x, int y, float scale, ItemStack... itemCycle) {
        this(x, y, scale, itemCycle, null);
    }
    
    public ElementItem(int x, int y, float scale, ItemStack[] itemCycle, String action) {
        super(x, y, MathHelper.floor(ITEM_SIZE_HARDCODED * scale), MathHelper.floor(ITEM_SIZE_HARDCODED * scale));
        
        DefaultedList<ItemStack> nonNullStacks = DefaultedList.ofSize(itemCycle.length, ItemStack.EMPTY);
        for (int i = 0; i < itemCycle.length; i++) {
            if (!itemCycle[i].isEmpty()) {
                nonNullStacks.set(i, itemCycle[i].copy());
            }
        }
        
        this.itemCycle = nonNullStacks;
        this.scale = scale;
        this.action = action;
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
        this.renderTick++;
        
        if (this.renderTick > ITEM_SWITCH_TICKS) {
            this.renderTick = 0;
            this.currentItem++;
            
            if (this.currentItem >= this.itemCycle.size()) {
                this.currentItem = 0;
            }
        }
        
        GuiLighting.enableForItems();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.x, this.y, 0);
        GlStateManager.scalef(this.scale, this.scale, 1.0F);
        
        if (this.currentItem < this.itemCycle.size()) {
            this.mc.getItemRenderer().renderGuiItemIcon(this.itemCycle.get(this.currentItem), 0, 0);
        }
        
        GlStateManager.popMatrix();
        GuiLighting.disable();
    }
    
    @Override
    public void drawOverlay(int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
        if (this.isHovered(mouseX, mouseY) && this.currentItem < this.itemCycle.size()) {
            if (this.tooltip != null) {
                this.drawHoveringText(this.tooltip, mouseX, mouseY);
            } else {
                this.renderToolTip(fontRenderer, this.itemCycle.get(this.currentItem), mouseX, mouseY);
            }
        }
    }
    
    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovered(mouseX, mouseY) && this.currentItem < this.itemCycle.size()) {
            if (this.action != null) {
                StringActionProcessor.process(this.action, this.parent);
            } else {
                this.parent.itemClicked(this.itemCycle.get(this.currentItem));
            }
        }
    }
}
