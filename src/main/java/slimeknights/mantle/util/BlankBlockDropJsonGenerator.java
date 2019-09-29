package slimeknights.mantle.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlankBlockDropJsonGenerator implements IDataProvider {

  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
  private final DataGenerator generator;
  private final String modId;

  public BlankBlockDropJsonGenerator(DataGenerator generatorIn, String modId) {
    this.generator = generatorIn;
    this.modId = modId;
  }

  @Override
  public void act(DirectoryCache cache) throws IOException {
    JsonObject blockState = new JsonObject();
    int generatedEntries = 0;
    JsonObject blockstateCache = new JsonObject();

    Path cacheP = this.generator.getOutputFolder().resolve("cache/" + this.modId + "/loot_tables/blocks/blocks.json");

    if (Files.exists(cacheP)) {
      String jsonTxt = IOUtils.toString(cacheP.toUri(), "UTF-8");
      blockstateCache = new JsonParser().parse(jsonTxt).getAsJsonObject();
    }

    for (Block block : Registry.BLOCK) {
      blockState = new JsonObject();
      ResourceLocation resourcelocation = Registry.BLOCK.getKey(block);

      if (!resourcelocation.getNamespace().equals(this.modId)) {
        continue;
      }

      if (blockstateCache.has(resourcelocation.toString())) {
        continue;
      }
      blockState.addProperty("REPLACEME", "TRUE");

      blockstateCache.addProperty(resourcelocation.toString(), "UNUSED");
      generatedEntries++;

      Path path = this.generator.getOutputFolder().resolve("assets/" + this.modId + "/loot_tables/blocks/" + resourcelocation.getPath() + ".json");
      IDataProvider.save(GSON, cache, blockState, path);
    }

    if (generatedEntries != 0) {
      IDataProvider.save(GSON, cache, blockstateCache, cacheP);
    }

    cache.func_218456_c(cacheP);
  }

  /**
   * Gets a name for this provider, to use in logging.
   */
  @Override
  public String getName() {
    return "Blank Block Drops Generator";
  }
}