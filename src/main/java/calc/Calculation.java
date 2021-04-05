package calc;

import parser.Item;

import java.util.Comparator;
import java.util.List;

public class Calculation {

    public static List<Item> calculation(List<Item> itemList) {
        for (Item item : itemList) {
            try {
                item.buySellRatio = item.buyOrders * 1.0 / item.sellOffers;
            } catch (Exception e) {
                item.buySellRatio = 0.0;
            }
            try {
                if (item.recipeId == 0) {
                    item.craftRatio = 0.0;
                } else {
                    item.craftRatio = item.formatCraftingMargin * 1f / item.formatBuyPrice;
                }
            } catch (Exception e) {
                item.craftRatio = 0.0;
            }
        }
        itemList.sort(Comparator.comparing(Item::getBuySellRatio).reversed());
        return itemList;
    }

}
