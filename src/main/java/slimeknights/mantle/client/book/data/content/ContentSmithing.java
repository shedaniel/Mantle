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

public class ContentSmithing extends PageContent {
    
    public static final transient int TEX_SIZE = 512;
    public static final transient ImageData IMG_SMITHING = new ImageData(TEX_MISC, 88, 0, 210, 42, TEX_SIZE, TEX_SIZE);
    
    public static final transient int INPUT_X = 5;
    public static final transient int INPUT_Y = 5;
    public static final transient int MODIFIER_X = 89;
    public static final transient int MODIFIER_Y = 5;
    public static final transient int RESULT_X = 173;
    public static final transient int RESULT_Y = 5;
    
    public static final transient float ITEM_SCALE = 2.0F;
    
    public String title = "Smithing";
    public ItemStackData input;
    public ItemStackData modifier;
    public ItemStackData result;
    public TextData[] description;
    
    @Override
    public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
        int x = BookScreen.PAGE_WIDTH / 2 - IMG_SMITHING.width / 2;
        int y = TITLE_HEIGHT;
        
        if (this.title == null || this.title.isEmpty()) {
            y = 0;
        } else {
            this.addTitle(list, this.title);
        }
        
        list.add(new ElementImage(x, y, IMG_SMITHING.width, IMG_SMITHING.height, IMG_SMITHING, book.appearance.slotColor));
        
        if (this.input != null && !this.input.id.equals("")) {
            list.add(new ElementItem(x + INPUT_X, y + INPUT_Y, ITEM_SCALE, this.input.getItems(), this.input.action));
        }
        
        if (this.modifier != null && !this.modifier.id.equals("")) {
            list.add(new ElementItem(x + MODIFIER_X, y + MODIFIER_Y, ITEM_SCALE, this.modifier.getItems(), this.modifier.action));
        }
        
        if (this.result != null && !this.result.id.equals("")) {
            list.add(new ElementItem(x + RESULT_X, y + RESULT_Y, ITEM_SCALE, this.result.getItems(), this.result.action));
        }
        
        if (this.description != null && this.description.length > 0) {
            list.add(new ElementText(0, IMG_SMITHING.height + y + 5, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - IMG_SMITHING.height - y - 5, this.description));
        }
    }
}
