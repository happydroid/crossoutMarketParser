package parser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemParser {

    private static ObjectMapper mapper = new ObjectMapper();

    public static Item[] parseItem(String json) {
        try {
            return mapper.readValue(json, Item[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Item[0];
        }
    }

}
