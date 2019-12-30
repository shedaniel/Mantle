package slimeknights.mantle.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class DistExecutor {
    public static <T> T runForDist(Supplier<Callable<T>> client, Supplier<Callable<T>> server) {
        try {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                return client.get().call();
            return server.get().call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T callWhenOn(EnvType type, Supplier<Callable<T>> toRun) {
        if (type == FabricLoader.getInstance().getEnvironmentType()) {
            try {
                return toRun.get().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    
    public static void runWhenOn(EnvType type, Supplier<Runnable> toRun) {
        if (type == FabricLoader.getInstance().getEnvironmentType()) {
            toRun.get().run();
        }
    }
}
