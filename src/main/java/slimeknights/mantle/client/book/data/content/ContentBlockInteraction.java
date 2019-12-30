package slimeknights.mantle.client.book.data.content;

import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ElementImage;
import slimeknights.mantle.client.screen.book.element.ElementItem;
import slimeknights.mantle.client.screen.book.element.ElementText;

import java.util.ArrayList;

import static slimeknights.mantle.client.screen.book.Textures.TEX_MISC;

public class ContentBlockInteraction extends PageContent {
    
    public static final transient int TEX_SIZE = 512;
    public static final transient ImageData IMG_SMITHING = new ImageData(TEX_MISC, 0, 0, 88, 55, TEX_SIZE, TEX_SIZE);
    
    public static final transient int INPUT_X = 6;
    public static final transient int INPUT_Y = 18;
    public static final transient int BLOCK_X = 40;
    public static final transient int BLOCK_Y = 26;
    
    public static final transient float ITEM_SCALE = 2.0F;
    public static final transient float BLOCK_SCALE = 5.0F;
    
    public String title = "Block Interaction";
    public ItemStackData input;
    public ItemStackData block;
    public TextData[] description;
    
    @Override
    public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
        int x = BookScreen.PAGE_WIDTH / 2 - IMG_SMITHING.width / 2 - 10;
        int y = TITLE_HEIGHT;
        
        if (this.title == null || this.title.isEmpty()) {
            y = 0;
        } else {
            this.addTitle(list, this.title);
        }
        
        list.add(new ElementImage(x, y, IMG_SMITHING.width, IMG_SMITHING.height, IMG_SMITHING, book.appearance.slotColor));
        
        if (this.input != null && !this.input.getItems().isEmpty()) {
            list.add(new ElementItem(x + INPUT_X, y + INPUT_Y, ITEM_SCALE, this.input.getItems(), this.input.action));
        }
        
        if (this.block != null && !this.block.getItems().isEmpty()) {
            list.add(new ElementItem(x + BLOCK_X, y + BLOCK_Y, BLOCK_SCALE, this.block.getItems(), this.block.action));
        }
        
        if (this.description != null && this.description.length > 0) {
            list.add(new ElementText(0, IMG_SMITHING.height + y + 50, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - IMG_SMITHING.height - y - 50, this.description));
        }
    }
}
