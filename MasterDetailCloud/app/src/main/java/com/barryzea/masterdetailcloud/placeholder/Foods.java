package com.barryzea.masterdetailcloud.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Foods {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<Food> ITEMS = new ArrayList<Food>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, Food> ITEM_MAP = new HashMap<String, Food>();

    private static final int COUNT = 0;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    public static void addItem(Food item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    public static Food getItem(String name){

        for(Food f : ITEMS){
            if(f.getName().equals(name)){
                return f;

            }
        }
        return null;
    }
    public static void updateItem(Food item){

        ITEMS.set(ITEMS.indexOf(item),item);
        ITEM_MAP.put(item.id, item);
    }
    public static void deleteItem(Food item){
        ITEMS.remove(item);
        ITEM_MAP.remove(item);
    }
    private static Food createPlaceholderItem(int position) {
        return new Food(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class Food {
        public  String id;
        public String name;
        public String price;

        public Food(String id, String name, String price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
        public Food( String name, String price) {

            this.name = name;
            this.price = price;
        }
        public Food(){}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Food food = (Food) o;
            return Objects.equals(id, food.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}