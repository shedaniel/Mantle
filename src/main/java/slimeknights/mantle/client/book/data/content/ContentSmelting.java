package slimeknights.mantle.client.book.data.content;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static slimeknights.mantle.client.screen.book.Textures.TEX_SMELTING;

public class ContentSmelting extends PageContent {
    public static final transient int TEX_SIZE = 128;
    public static final transient ImageData IMG_SMELTING = new ImageData(TEX_SMELTING, 0, 0, 110, 114, TEX_SIZE, TEX_SIZE);
    
    public static final transient int INPUT_X = 5;
    public static final transient int INPUT_Y = 5;
    public static final transient int RESULT_X = 74;
    public static final transient int RESULT_Y = 41;
    public static final transient int FUEL_X = 5;
    public static final transient int FUEL_Y = 77;
    
    public static final transient float ITEM_SCALE = 2.0F;
    
    public String title = "Smelting";
    public ItemStackData input;
    public ItemStackData result;
    public ItemStackData fuel;
    public int cookTime = 200;
    public TextData[] description;
    public String recipe;
    
    @Override
    public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
        int x = BookScreen.PAGE_WIDTH / 2 - IMG_SMELTING.width / 2;
        int y = TITLE_HEIGHT;
        
        TextData tdTitle = new TextData(this.title);
        tdTitle.underlined = true;
        list.add(new ElementText(0, 0, BookScreen.PAGE_WIDTH, 9, tdTitle));
        list.add(new ElementImage(x, y, IMG_SMELTING.width, IMG_SMELTING.height, IMG_SMELTING, book.appearance.slotColor));
        list.add(new ElementTooltip(ImmutableList.of(I18n.translate("mantle:tooltip.cooktime", cookTime / 20)), x + 7, y + 42, 60, 28));
        
        if (this.input != null && !this.input.getItems().isEmpty()) {
            list.add(new ElementItem(x + INPUT_X, y + INPUT_Y, ITEM_SCALE, this.input.getItems(), this.input.action));
        }
        
        if (this.result != null && !this.result.getItems().isEmpty()) {
            list.add(new ElementItem(x + RESULT_X, y + RESULT_Y, ITEM_SCALE, this.result.getItems(), this.result.action));
        }
        
        list.add(new ElementItem(x + FUEL_X, y + FUEL_Y, ITEM_SCALE, this.getFuelsList()));
        
        if (this.description != null && this.description.length > 0) {
            list.add(new ElementText(0, IMG_SMELTING.height + y + 5, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - y - 5, this.description));
        }
    }
    
    public DefaultedList<ItemStack> getFuelsList() {
        //TODO ask JEI for fuel list if it is present
        
        if (fuel != null) {
            return fuel.getItems();
        }
        
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().stream().map(ItemStack::new).collect(Collectors.toCollection(DefaultedList::of));
    }
    
    @Override
    public void load() {
        super.load();
        
        if (!StringUtils.isEmpty(recipe) && Identifier.isValid(recipe)) {
            Recipe<?> recipe = MinecraftClient.getInstance().world.getRecipeManager().get(new Identifier(this.recipe)).orElse(null);
            
            if (recipe instanceof AbstractCookingRecipe) {
                input = ItemStackData.getItemStackData(DefaultedList.copyOf(ItemStack.EMPTY, recipe.getPreviewInputs().get(0).getStackArray()));
                cookTime = ((AbstractCookingRecipe) recipe).getCookTime();
                result = ItemStackData.getItemStackData(recipe.getOutput());
            }
        }
    }
}
