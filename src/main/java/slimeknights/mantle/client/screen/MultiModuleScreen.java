package slimeknights.mantle.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.util.Rect2i;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import slimeknights.mantle.fabric.mixin.ContainerScreenHooks;
import slimeknights.mantle.inventory.MultiModuleContainer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MultiModuleScreen<T extends MultiModuleContainer> extends AbstractContainerScreen<T> {
    
    protected List<ModuleScreen> modules = Lists.newArrayList();
    
    public int cornerX;
    public int cornerY;
    public int realWidth;
    public int realHeight;
    
    public MultiModuleScreen(T container, PlayerInventory playerInventory, Text title) {
        super(container, playerInventory, title);
        
        this.realWidth = -1;
        this.realHeight = -1;
    }
    
    protected void addModule(ModuleScreen module) {
        this.modules.add(module);
    }
    
    public List<Rect2i> getModuleAreas() {
        List<Rect2i> areas = new ArrayList<Rect2i>(this.modules.size());
        for (ModuleScreen module : this.modules) {
            areas.add(module.getArea());
        }
        return areas;
    }
    
    @Override
    public void init() {
        if (this.realWidth > -1) {
            // has to be reset before calling initGui so the position is getting retained
            this.containerWidth = this.realWidth;
            this.containerHeight = this.realHeight;
        }
        super.init();
        
        this.cornerX = this.left;
        this.cornerY = this.top;
        this.realWidth = this.containerWidth;
        this.realHeight = this.containerHeight;
        
        for (ModuleScreen module : this.modules) {
            this.updateSubmodule(module);
        }
        
        //this.guiLeft = this.guiTop = 0;
        //this.xSize = width;
        //this.ySize = height;
    }
    
    @Override
    protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
        for (ModuleScreen module : this.modules) {
            module.handleDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        }
    }
    
    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        this.drawContainerName();
        this.drawPlayerInventoryName();
        
        for (ModuleScreen module : this.modules) {
            // set correct state for the module
            GlStateManager.pushMatrix();
            GlStateManager.translatef(-this.left, -this.top, 0.0F);
            GlStateManager.translatef(((ContainerScreenHooks) module).mantle_getContainerLeft(), ((ContainerScreenHooks) module).mantle_getContainerTop(), 0.0F);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            module.handleDrawGuiContainerForegroundLayer(mouseX, mouseY);
            GlStateManager.popMatrix();
        }
    }
    
    protected void drawBackground(Identifier background) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(background);
        this.blit(this.cornerX, this.cornerY, 0, 0, this.realWidth, this.realHeight);
    }
    
    protected void drawContainerName() {
        String localizedName = this.getTitle().asFormattedString();//multiContainer.getInventoryDisplayName();
        if (localizedName != null) {
            this.font.draw(localizedName, 8, 6, 0x404040);
        }
    }
    
    protected void drawPlayerInventoryName() {
        String localizedName = MinecraftClient.getInstance().player.inventory.getDisplayName().asString();
        this.font.draw(localizedName, 8, this.containerHeight - 96 + 2, 0x404040);
    }
    
    @Override
    public void init(MinecraftClient mc, int width, int height) {
        super.init(mc, width, height);
        
        for (ModuleScreen module : this.modules) {
            module.init(mc, width, height);
            this.updateSubmodule(module);
        }
    }
    
    @Override
    public void resize(@Nonnull MinecraftClient mc, int width, int height) {
        super.resize(mc, width, height);
        
        for (ModuleScreen module : this.modules) {
            module.resize(mc, width, height);
            this.updateSubmodule(module);
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        int oldX = this.left;
        int oldY = this.top;
        int oldW = this.containerWidth;
        int oldH = this.containerHeight;
        
        this.left = this.cornerX;
        this.top = this.cornerY;
        this.containerWidth = this.realWidth;
        this.containerHeight = this.realHeight;
        super.render(mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.left = oldX;
        this.top = oldY;
        this.containerWidth = oldW;
        this.containerHeight = oldH;
    }
    
    // needed to get the correct slot on clicking
    @Override
    protected boolean isPointWithinBounds(int left, int top, int right, int bottom, double pointX, double pointY) {
        pointX -= this.cornerX;
        pointY -= this.cornerY;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }
    
    protected void updateSubmodule(ModuleScreen module) {
        module.updatePosition(this.cornerX, this.cornerY, this.realWidth, this.realHeight);
        
        if (((ContainerScreenHooks) module).mantle_getContainerLeft() < this.left) {
            this.containerWidth += this.left - ((ContainerScreenHooks) module).mantle_getContainerLeft();
            this.left = ((ContainerScreenHooks) module).mantle_getContainerLeft();
        }
        if (((ContainerScreenHooks) module).mantle_getContainerTop() < this.top) {
            this.containerHeight += this.top - ((ContainerScreenHooks) module).mantle_getContainerTop();
            this.top = ((ContainerScreenHooks) module).mantle_getContainerTop();
        }
        if (module.guiRight() > this.left + this.containerWidth) {
            this.containerWidth = module.guiRight() - this.left;
        }
        if (module.guiBottom() > this.top + this.containerHeight) {
            this.containerHeight = module.guiBottom() - this.top;
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        ModuleScreen module = this.getModuleForPoint(mouseX, mouseY);
        if (module != null) {
            if (module.handleMouseClicked(mouseX, mouseY, mouseButton)) {
                return false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double unkowwn) {
        ModuleScreen module = this.getModuleForPoint(mouseX, mouseY);
        if (module != null) {
            if (module.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
                return false;
            }
        }
        
        return super.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, unkowwn);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        ModuleScreen module = this.getModuleForPoint(mouseX, mouseY);
        if (module != null) {
            if (module.handleMouseReleased(mouseX, mouseY, state)) {
                return false;
            }
        }
        
        return super.mouseReleased(mouseX, mouseY, state);
    }
    
    protected ModuleScreen getModuleForPoint(double x, double y) {
        for (ModuleScreen module : this.modules) {
            if (this.isPointWithinBounds(((ContainerScreenHooks) module).mantle_getContainerLeft(), ((ContainerScreenHooks) module).mantle_getContainerTop(), module.guiRight(), module.guiBottom(), x + this.cornerX, y + this.cornerY)) {
                return module;
            }
        }
        
        return null;
    }
    
    public ModuleScreen getModuleForSlot(int slotNumber) {
        return this.getModuleForContainer(this.getContainer().getSlotContainer(slotNumber));
    }
    
    protected ModuleScreen getModuleForContainer(Container container) {
        for (ModuleScreen module : this.modules) {
            if (module.getContainer() == container) {
                return module;
            }
        }
        
        return null;
    }
    
    @Nonnull
    @Override
    public T getContainer() {
        return this.container;
    }
    
}
