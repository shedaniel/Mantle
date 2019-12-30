package slimeknights.mantle.client.book.data.content;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ElementImage;
import slimeknights.mantle.client.screen.book.element.ElementText;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ContentImageText extends PageContent {
    
    public String title = null;
    public ImageData image;
    public TextData[] text;
    
    @Override
    public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
        int y = TITLE_HEIGHT;
        
        if (this.title == null || this.title.isEmpty()) {
            y = 0;
        } else {
            this.addTitle(list, this.title);
        }
        
        if (this.image != null && this.image.location != null) {
            list.add(new ElementImage(0, y, BookScreen.PAGE_WIDTH, 100, this.image));
        } else {
            list.add(new ElementImage(0, y, 32, 32, ImageData.MISSING));
        }
        
        if (this.text != null && this.text.length > 0) {
            list.add(new ElementText(0, y + 105, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT - 105 - y, this.text));
        }
    }
}
