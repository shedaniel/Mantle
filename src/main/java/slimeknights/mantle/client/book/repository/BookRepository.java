package slimeknights.mantle.client.book.repository;

import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.book.data.SectionData;

import java.util.List;

public abstract class BookRepository {
    
    public static final BookRepository DUMMY = new DummyRepository();
    
    public abstract List<SectionData> getSections();
    
    public Identifier getResourceLocation(String path) {
        return this.getResourceLocation(path, false);
    }
    
    public abstract Identifier getResourceLocation(String path, boolean safe);
    
    public abstract Resource getResource(Identifier loc);
    
    public boolean resourceExists(String location) {
        return this.resourceExists(new Identifier(location));
    }
    
    public abstract boolean resourceExists(Identifier location);
    
    public String resourceToString(Resource resource) {
        return this.resourceToString(resource, true);
    }
    
    public abstract String resourceToString(Resource resource, boolean skipCommments);
}
