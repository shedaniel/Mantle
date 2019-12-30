package slimeknights.mantle.client.book.data.element;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.book.repository.BookRepository;

public class DataLocation implements IDataElement {
    
    public String file;
    public transient Identifier location;
    
    @Override
    public void load(BookRepository source) {
        location = "$BLOCK_ATLAS".equals(file) ? SpriteAtlasTexture.BLOCK_ATLAS_TEX : source.getResourceLocation(file, true);
    }
}
