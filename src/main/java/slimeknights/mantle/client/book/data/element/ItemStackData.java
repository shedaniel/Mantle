package slimeknights.mantle.client.book.data.element;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.StringTag;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.repository.BookRepository;
import slimeknights.mantle.fabric.mixin.MixinItemTags;

import java.util.stream.Collectors;

public class ItemStackData implements IDataElement {
    
    public String itemList = null;
    public String tag = null;
    public transient String action;
    private transient DefaultedList<ItemStack> items;
    
    public String id = "";
    public byte amount = 1;
    public JsonObject nbt;
    
    private transient boolean customData;
    private transient boolean isTag;
    private transient int tagGeneration;
    
    public DefaultedList<ItemStack> getItems() {
        if (isTag && tagGeneration != MixinItemTags.getLatestVersion()) {
            loadTag();
        }
        
        if (items != null) {
            return items;
        }
        
        return DefaultedList.ofSize(1, getItem());
    }
    
    private ItemStack getItem() {
        Item item;
        boolean isMissingItem = false;
        try {
            item = Registry.ITEM.get(new Identifier(id));//ItemArgument.getItem(null, id).getItem();
        } catch (Exception e) {
            item = Item.fromBlock(Blocks.BARRIER);
            isMissingItem = true;
        }
        
        if (item == Items.AIR) {
            item = Item.fromBlock(Blocks.BARRIER);
            isMissingItem = true;
        }
        
        ItemStack itemStack = new ItemStack(item, amount);
        
        if (nbt != null) {
            try {
                itemStack.setTag(StringNbtReader.parse(filterJsonQuotes(nbt.toString())));
            } catch (CommandSyntaxException ignored) {
            }
        }
        
        if (isMissingItem) {
            CompoundTag display = itemStack.getOrCreateSubTag("display");
            display.putString("Name", "\u00A7rUnknown Item");
            ListTag lore = new ListTag();
            lore.add(new StringTag("\u00A7r\u00A7eItem Name:"));
            lore.add(new StringTag("\u00A7r\u00A7e" + id));
            display.put("Lore", lore);
        }
        
        return itemStack;
    }
    
    public static ItemStackData getItemStackData(ItemStack stack) {
        return getItemStackData(stack, false);
    }
    
    public static ItemStackData getItemStackData(ItemStack stack, boolean ignoreNbt) {
        ItemStackData data = new ItemStackData();
        data.id = Registry.ITEM.getId(stack.getItem()).toString();
        data.amount = (byte) stack.getCount();
        if (!ignoreNbt && stack.getTag() != null) {
            data.nbt = BookLoader.GSON.toJsonTree(stack.getTag(), CompoundTag.class).getAsJsonObject();
        }
        
        return data;
    }
    
    public static ItemStackData getItemStackData(DefaultedList<ItemStack> items) {
        ItemStackData data = new ItemStackData();
        data.items = items;
        data.customData = true;
        
        data.id = "->itemList";
        
        return data;
    }
    
    public static String filterJsonQuotes(String s) {
        return s.replaceAll("\"(\\w+)\"\\s*:", "$1: ");
    }
    
    private void loadTag() {
        isTag = true;
        tagGeneration = MixinItemTags.getLatestVersion();
        
        Tag<Item> values = ItemTags.getContainer().get(new Identifier(tag));
        if (values != null) {
            items = values.values().stream().map(ItemStack::new).collect(Collectors.toCollection(DefaultedList::of));
        } else {
            items = DefaultedList.of();
        }
    }
    
    @Override
    public void load(BookRepository source) {
        if (customData) {
            return;
        }
        
        if (!ChatUtil.isEmpty(tag) && Identifier.isValid(tag)) {
            loadTag();
            id = "->itemList";
            return;
        }
        
        Identifier location = source.getResourceLocation(itemList);
        
        if (location != null) {
            id = "->itemList";
            
            if (source.resourceExists(location)) {
                try {
                    ItemsList itemsList = BookLoader.GSON.fromJson(source.resourceToString(source.getResource(location)), ItemsList.class);
                    items = DefaultedList.ofSize(itemsList.items.length, ItemStack.EMPTY);
                    
                    for (int i = 0; i < itemsList.items.length; i++) {
                        items.set(i, itemsList.items[i].getItem());
                    }
                    
                    this.action = itemsList.action;
                } catch (Exception ignored) {
                }
            }
        }
    }
    
    private static class ItemsList {
        
        public ItemStackData[] items = new ItemStackData[0];
        public String action;
    }
    
    public static class ItemLink {
        
        public ItemStackData item = new ItemStackData();
        public boolean damageSensitive = false;
        public String action = "";
    }
}
