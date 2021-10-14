package calc;

import parser.Item;

import java.util.Comparator;
import java.util.List;

public class Calculation {

    public static List<Item> calculation(List<Item> itemList) {
        for (Item item : itemList) {
            try {
                item.setBuySellRatio(item.getBuyOrders() * 1.0 / item.getSellOffers());
            } catch (Exception e) {
                item.setBuySellRatio(0.0);
            }
            try {
                if (item.getRecipeId() == 0) {
                    item.setCraftRatio(0.0);
                } else {
                    item.setCraftRatio(item.getFormatCraftingMargin() * 1f / item.getFormatBuyPrice());
                }
            } catch (Exception e) {
                item.setCraftRatio(0.0);
            }
        }
        itemList.sort(Comparator.comparing(Item::getBuySellRatio).reversed());
        return itemList;
    }

}
