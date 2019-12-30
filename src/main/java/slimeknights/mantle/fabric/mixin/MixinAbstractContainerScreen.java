package slimeknights.mantle.fabric.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.mantle.client.screen.ModuleScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.WrapperSlot;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawSlot(Slot slotIn, CallbackInfo info) {
        if ((Object) this instanceof MultiModuleScreen) {
            MultiModuleScreen screen = (MultiModuleScreen) (Object) this;
            ModuleScreen module = screen.getModuleForSlot(slotIn.id);
            
            if (module != null) {
                Slot slot = slotIn;
                // unwrap for the call to the module
                if (slotIn instanceof WrapperSlot) {
                    slot = ((WrapperSlot) slotIn).parent;
                }
                if (!module.shouldDrawSlot(slot)) {
                    return;
                }
            }
            
            // update slot positions
            if (slotIn instanceof WrapperSlot) {
                slotIn.xPosition = ((WrapperSlot) slotIn).parent.xPosition;
                slotIn.yPosition = ((WrapperSlot) slotIn).parent.yPosition;
            }
        }
    }
    
    @Inject(method = "isPointOverSlot", at = @At("HEAD"), cancellable = true)
    private void drawSlot(Slot slotIn, double pointX, double pointY, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof MultiModuleScreen) {
            MultiModuleScreen screen = (MultiModuleScreen) (Object) this;
            ModuleScreen module = screen.getModuleForSlot(slotIn.id);
            
            // mouse inside the module of the slot?
            if (module != null) {
                Slot slot = slotIn;
                // unwrap for the call to the module
                if (slotIn instanceof WrapperSlot) {
                    slot = ((WrapperSlot) slotIn).parent;
                }
                if (!module.shouldDrawSlot(slot)) {
                    info.setReturnValue(false);
                }
            }
        }
    }
}
