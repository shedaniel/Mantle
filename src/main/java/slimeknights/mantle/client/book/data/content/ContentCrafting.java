package slimeknights.mantle.client.book.data.content;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.BookLoadException;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ElementImage;
import slimeknights.mantle.client.screen.book.element.ElementItem;
import slimeknights.mantle.client.screen.book.element.ElementText;

import java.util.ArrayList;

import static slimeknights.mantle.client.screen.book.Textures.TEX_CRAFTING;

public class ContentCrafting extends PageContent {
    
    public static final transient int TEX_SIZE = 256;
    public static final transient ImageData IMG_CRAFTING_LARGE = new ImageData(TEX_CRAFTING, 0, 0, 183, 114, TEX_SIZE, TEX_SIZE);
    public static final transient ImageData IMG_CRAFTING_SMALL = new ImageData(TEX_CRAFTING, 0, 114, 155, 78, TEX_SIZE, TEX_SIZE);
    
    public static final transient int X_RESULT_SMALL = 118;
    public static final transient int Y_RESULT_SMALL = 23;
    public static final transient int X_RESULT_LARGE = 146;
    public static final transient int Y_RESULT_LARGE = 41;
    
    public static final transient float ITEM_SCALE = 2.0F;
    public static final transient int SLOT_MARGIN = 5;
    public static final transient int SLOT_PADDING = 4;
    
    public String title = "Crafting";
    public String grid_size = "large";
    public ItemStackData[][] grid;
    public ItemStackData result;
    public TextData[] description;
    public String recipe;
    
    @Override
    public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
        int x = 0;
        int y = 16;
        int height = 100;
        int resultX = 100;
        int resultY = 50;
        
        TextData tdTitle = new TextData(this.title);
        tdTitle.underlined = true;
        list.add(new ElementText(0, 0, BookScreen.PAGE_WIDTH, 9, tdTitle));
        
        if (this.grid_size.equalsIgnoreCase("small")) {
            x = BookScreen.PAGE_WIDTH / 2 - IMG_CRAFTING_SMALL.width / 2;
            height = y + IMG_CRAFTING_SMALL.height;
            list.add(new ElementImage(x, y, IMG_CRAFTING_SMALL.width, IMG_CRAFTING_SMALL.height, IMG_CRAFTING_SMALL, book.appearance.slotColor));
            resultX = x + X_RESULT_SMALL;
            resultY = y + Y_RESULT_SMALL;
        } else if (this.grid_size.equalsIgnoreCase("large")) {
            x = BookScreen.PAGE_WIDTH / 2 - IMG_CRAFTING_LARGE.width / 2;
            height = y + IMG_CRAFTING_LARGE.height;
            list.add(new ElementImage(x, y, IMG_CRAFTING_LARGE.width, IMG_CRAFTING_LARGE.height, IMG_CRAFTING_LARGE, book.appearance.slotColor));
            resultX = x + X_RESULT_LARGE;
            resultY = y + Y_RESULT_LARGE;
        }
        
        if (this.grid != null) {
            for (int i = 0; i < this.grid.length; i++) {
                for (int j = 0; j < this.grid[i].length; j++) {
                    if (this.grid[i][j] == null || this.grid[i][j].getItems().isEmpty()) {
                        continue;
                    }
                    list.add(new ElementItem(x + SLOT_MARGIN + (SLOT_PADDING + Math.round(ElementItem.ITEM_SIZE_HARDCODED * ITEM_SCALE)) * j, y + SLOT_MARGIN + (SLOT_PADDING + Math.round(ElementItem.ITEM_SIZE_HARDCODED * ITEM_SCALE)) * i, ITEM_SCALE, this.grid[i][j].getItems(), this.grid[i][j].action));
                }
            }
        }
        
        if (this.result != null) {
            list.add(new ElementItem(resultX, resultY, ITEM_SCALE, this.result.getItems(), this.result.action));
        }
        
        if (this.description != null && this.description.length > 0) {
            list.add(new ElementText(0, height + 5, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - height - 5, this.description));
        }
    }
    
    @Override
    public void load() {
        super.load();
        
        if (!StringUtils.isEmpty(recipe) && Identifier.isValid(recipe)) {
            int w = 0, h = 0;
            switch (grid_size.toLowerCase()) {
                case "large":
                    w = h = 3;
                    break;
                case "small":
                    w = h = 2;
                    break;
            }
            
            Recipe<?> recipe = MinecraftClient.getInstance().world.getRecipeManager().get(new Identifier(this.recipe)).orElse(null);
            if (recipe instanceof CraftingRecipe) {
                if (!recipe.fits(w, h)) {
                    throw new BookLoadException("Recipe " + this.recipe + " cannot fit in a " + w + "x" + h + " crafting grid");
                }
                
                result = ItemStackData.getItemStackData(recipe.getOutput());
                
                DefaultedList<Ingredient> ingredients = recipe.getPreviewInputs();
                
                if (recipe instanceof ShapedRecipe) {
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    
                    grid = new ItemStackData[shaped.getHeight()][shaped.getWidth()];
                    
                    for (int y = 0; y < grid.length; y++) {
                        for (int x = 0; x < grid[y].length; x++) {
                            grid[y][x] = ItemStackData.getItemStackData(DefaultedList.copyOf(ItemStack.EMPTY, ingredients.get(x + y * grid[y].length).getStackArray()));
                        }
                    }
                    
                    return;
                }
                
                grid = new ItemStackData[h][w];
                for (int i = 0; i < ingredients.size(); i++) {
                    grid[i / h][i % w] = ItemStackData.getItemStackData(DefaultedList.copyOf(ItemStack.EMPTY, ingredients.get(i).getStackArray()));
                }
            }
        }
    }
}
