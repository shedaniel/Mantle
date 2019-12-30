package slimeknights.mantle.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Widget {
    
    public int xPos;
    public int yPos;
    public int height;
    public int width;
    
    public abstract void draw();
    
    /**
     * Where the part will be drawn. Upper left corner.
     */
    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}

