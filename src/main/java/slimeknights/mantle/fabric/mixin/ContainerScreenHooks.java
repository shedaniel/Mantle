package slimeknights.mantle.fabric.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerScreen.class)
public interface ContainerScreenHooks {
    
    @Accessor("left")
    int mantle_getContainerLeft();
    
    @Accessor("top")
    int mantle_getContainerTop();
    
    @Accessor("containerWidth")
    int mantle_getContainerWidth();
    
    @Accessor("containerHeight")
    int mantle_getContainerHeight();
    
    @Invoker("isPointOverSlot")
    boolean mantle_isPointOverSlot(Slot slot, double pointX, double pointY);
    
}