package slimeknights.mantle.inventory;

import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import slimeknights.mantle.tileentity.InventoryTileEntity;

public class SimpleChestContainer extends BaseContainer<InventoryTileEntity> {
    
    public SimpleChestContainer(ContainerType<?> containerType, int windowId, InventoryTileEntity tile, int rows, int columns, PlayerInventory playerInventory) {
        super(containerType, windowId, tile);
        
        int index = 0;
        
        // chest inventory
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                // safety
                if (index > tile.getInvSize()) {
                    break;
                }
                
                this.addSlot(this.createSlot(tile, index, 8 + j * 18, 18 + i * 18));
                index++;
            }
        }
        
        // player inventory
        this.addPlayerInventory(playerInventory, 17, 86);
    }
    
    protected Slot createSlot(Inventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y);
    }
}
