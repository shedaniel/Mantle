package slimeknights.mantle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.client.ClientProxy;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.common.ServerProxy;
import slimeknights.mantle.fabric.DistExecutor;

/**
 * Mantle
 * <p>
 * Central mod object for Mantle
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
public class Mantle implements ModInitializer {
    
    public static final String modId = "mantle";
    public static final Logger logger = LogManager.getLogger("Mantle");
    
    /* Instance of this mod, used for grabbing prototype fields */
    public static Mantle instance;
    
    /* Proxies for sides, used for graphics processing */
    public static ServerProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    
    public Mantle() {
        instance = this;
    }
    
    @Override
    public void onInitialize() {
        proxy.preInit();
        ServerSidePacketRegistry.INSTANCE.register(new Identifier("mantle:books"), (packetContext, packetByteBuf) -> {
            String pageName = packetByteBuf.readString();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer() != null && pageName != null) {
                    PlayerEntity player = packetContext.getPlayer();
                    
                    ItemStack is = player.getStackInHand(Hand.MAIN_HAND);
                    
                    if (!is.isEmpty()) {
                        BookHelper.writeSavedPage(is, pageName);
                    }
                }
            });
        });
        proxy.init();
        proxy.postInit();
    }
}
