package slimeknights.mantle.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.common.ServerProxy;

public class ClientProxy extends ServerProxy {
    
    private BookLoader bookLoader;
    private ExtraHeartRenderHandler extraHeartRenderHandler;
    
    @Override
    public void preInit() {
        this.bookLoader = new BookLoader();
    }
    
    @Override
    public void init() {
        this.bookLoader.apply(MinecraftClient.getInstance().getResourceManager());
    }
    
    @Override
    public void postInit() {
        extraHeartRenderHandler = new ExtraHeartRenderHandler();
    }
    
    @Override
    public void onRenderExtraHeart() {
        extraHeartRenderHandler.renderHealthbar();
    }
    
    @Override
    public void registerReloadableListener() {
        ((ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager()).registerListener(this.bookLoader);
    }
}
