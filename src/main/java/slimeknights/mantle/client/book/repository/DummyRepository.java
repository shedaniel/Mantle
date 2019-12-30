package slimeknights.mantle.client.book.repository;

import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.book.data.SectionData;

import java.util.List;

public class DummyRepository extends BookRepository {
    
    @Override
    public List<SectionData> getSections() {
        return null;
    }
    
    @Override
    public Identifier getResourceLocation(String path, boolean safe) {
        return null;
    }
    
    @Override
    public Resource getResource(Identifier loc) {
        return null;
    }
    
    @Override
    public boolean resourceExists(Identifier location) {
        return false;
    }
    
    @Override
    public String resourceToString(Resource resource, boolean skipCommments) {
        return null;
    }
}
