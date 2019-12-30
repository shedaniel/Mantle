package slimeknights.mantle.client.book.data;

import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.resource.Resource;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.content.ContentError;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.client.screen.book.BookScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class SectionData implements IDataItem {
    
    public String name = null;
    public ImageData icon = new ImageData();
    public Set<String> requirements = Sets.newHashSet();
    public boolean hideWhenLocked = false;
    public String data = "";
    
    public transient int unnamedPageCounter = 0;
    public transient BookData parent;
    public transient BookRepository source;
    public transient ArrayList<PageData> pages = new ArrayList<>();
    
    public SectionData() {
        this(false);
    }
    
    public SectionData(boolean custom) {
        if (custom) {
            this.data = "no-load";
        }
    }
    
    public String translate(String string) {
        return this.parent.translate(string);
    }
    
    @Override
    public void load() {
        if (this.name == null) {
            this.name = "section" + this.parent.unnamedSectionCounter++;
        }
        
        this.name = this.name.toLowerCase();
        
        if (!this.data.equals("no-load")) {
            Resource pagesInfo = this.source.getResource(this.source.getResourceLocation(this.data));
            if (pagesInfo != null) {
                String data = this.source.resourceToString(pagesInfo);
                if (!data.isEmpty()) {
                    try {
                        this.pages = this.getPages(data);
                    } catch (Exception e) {
                        this.pages = new ArrayList<>();
                        PageData pdError = new PageData(true);
                        pdError.name = "errorrenous";
                        pdError.content = new ContentError("Failed to load section " + this.name + ".", e);
                        this.pages.add(pdError);
                        
                        e.printStackTrace();
                    }
                }
            }
        }
        
        for (PageData page : this.pages) {
            page.parent = this;
            page.source = this.source;
            page.load();
        }
        
        this.icon.load(source);
    }
    
    /**
     * Gets a list of pages from the given data
     *
     * @param data JSON data
     * @return ArrayList of pages for the book
     */
    protected ArrayList<PageData> getPages(String data) {
        return new ArrayList<>(Arrays.asList(BookLoader.GSON.fromJson(data, PageData[].class)));
    }
    
    public void update(@Nullable BookScreen.AdvancementCache advancementCache) {
    }
    
    public String getTitle() {
        String title = this.parent.strings.get(this.name);
        return title == null ? this.name : title;
    }
    
    public int getPageCount() {
        return this.pages.size();
    }
    
    public boolean isUnlocked(BookScreen.AdvancementCache advancementCache) {
        if (advancementCache == null || this.requirements == null || this.requirements.size() == 0) {
            return true;
        }
        
        for (String achievement : this.requirements) {
            if (!requirementSatisfied(achievement, advancementCache)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static boolean requirementSatisfied(String requirement, BookScreen.AdvancementCache advancementCache) {
        if (advancementCache == null) {
            return true;
        }
        
        AdvancementProgress progress = advancementCache.getProgress(requirement);
        
        return progress != null && progress.isDone();
        
    }
}
