package slimeknights.mantle.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.util.Rect2i;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import slimeknights.mantle.fabric.mixin.ContainerScreenHooks;

// a sub-gui. Mostly the same as a separate ContainerScreen, but doesn't do the calls that affect the game as if this were the only gui
@Environment(EnvType.CLIENT)
public abstract class ModuleScreen extends AbstractContainerScreen {
    
    protected final MultiModuleScreen parent;
    
    // left or right of the parent
    protected final boolean right;
    // top or bottom of the parent
    protected final boolean bottom;
    
    public int yOffset = 0;
    public int xOffset = 0;
    
    public ModuleScreen(MultiModuleScreen parent, Container container, PlayerInventory playerInventory, Text title, boolean right, boolean bottom) {
        super(container, playerInventory, title);
        
        this.parent = parent;
        this.right = right;
        this.bottom = bottom;
    }
    
    public int guiRight() {
        return this.left + this.containerWidth;
    }
    
    public int guiBottom() {
        return this.top + this.containerHeight;
    }
    
    public Rect2i getArea() {
        return new Rect2i(this.left, this.top, this.containerWidth, this.containerHeight);
    }
    
    @Override
    public void init() {
        this.left = (this.width - this.containerWidth) / 2;
        this.top = (this.height - this.containerHeight) / 2;
    }
    
    public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
        if (this.right) {
            this.left = parentX + parentSizeX;
        } else {
            this.left = parentX - this.containerWidth;
        }
        
        if (this.bottom) {
            this.top = parentY + parentSizeY - this.containerHeight;
        } else {
            this.top = parentY;
        }
        
        this.left += this.xOffset;
        this.top += this.yOffset;
    }
    
    public boolean shouldDrawSlot(Slot slot) {
        return true;
    }
    
    public boolean isMouseInModule(int mouseX, int mouseY) {
        return mouseX >= this.left && mouseX < this.guiRight() && mouseY >= this.top && mouseY < this.guiBottom();
    }
    
    public boolean isMouseOverFullSlot(double mouseX, double mouseY) {
        for (Slot slot : this.container.slotList) {
            if (((ContainerScreenHooks) this.parent).mantle_isPointOverSlot(slot, mouseX, mouseY) && slot.hasStack()) {
                return true;
            }
        }
        return false;
    }
    
    /*
      public void updateDragged(boolean dragSplitting, Set draggedSlots) {
        this.dragSplitting = dragSplitting;
        this.dragSplittingSlots.clear();
        for(Object o : draggedSlots) {
          if(o instanceof SlotWrapper)
            this.dragSplittingSlots.add(((SlotWrapper) o).parent);
          else
            this.dragSplittingSlots.add(o);
        }
      }
    */
    public void handleDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawBackground(partialTicks, mouseX, mouseY);
    }
    
    public void handleDrawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.drawForeground(mouseX, mouseY);
    }
    
    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }
    
    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseClickMove(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick) {
        return false;
    }
    
    /**
     * Custom mouse click handling.
     *
     * @return True to prevent the main container handling the mouseclick
     */
    public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
        return false;
    }
    
}
