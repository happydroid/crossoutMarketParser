package parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    @JsonProperty("id")
    public Integer id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("sellOffers")
    public Integer sellOffers;

    @JsonProperty("buyOrders")
    public Integer buyOrders;

    @JsonProperty("formatSellPrice")
    public Float formatSellPrice;

    @JsonProperty("formatBuyPrice")
    public Float formatBuyPrice;

    @JsonProperty("rarityName")
    public String rarityName;

    @JsonProperty("categoryName")
    public String categoryName;

    @JsonProperty("formatCraftingMargin")
    public Float formatCraftingMargin;

    @JsonProperty("formatCraftingBuySum")
    public Float formatCraftingBuySum;

    @JsonProperty("recipeId")
    public Integer recipeId;

    public Float buySellRatio;

    public Float speedBuySellRatio;

    public Float craftRatio;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getSellOffers() {
        return sellOffers;
    }

    public Integer getBuyOrders() {
        return buyOrders;
    }

    public Float getFormatSellPrice() {
        return formatSellPrice;
    }

    public Float getFormatBuyPrice() {
        return formatBuyPrice;
    }

    public String getRarityName() {
        return rarityName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Float getFormatCraftingMargin() {
        return formatCraftingMargin;
    }

    public Float getFormatCraftingBuySum() {
        return formatCraftingBuySum;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public Float getBuySellRatio() {
        return buySellRatio;
    }

    public Float getSpeedBuySellRatio() {
        return speedBuySellRatio;
    }

    public Float getCraftRatio() {
        return craftRatio;
    }
}
