package slimeknights.mantle.fabric.mixin;

import net.minecraft.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemTags.class)
public interface MixinItemTags {
    @Accessor("latestVersion")
    static int getLatestVersion() {
        throw new AssertionError();
    }
}
