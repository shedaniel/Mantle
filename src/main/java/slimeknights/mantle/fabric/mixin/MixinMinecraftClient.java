package slimeknights.mantle.fabric.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.mantle.Mantle;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        Mantle.proxy.registerReloadableListener();
    }
}
