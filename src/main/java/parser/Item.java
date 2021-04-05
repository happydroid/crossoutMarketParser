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
    public Double formatSellPrice;

    @JsonProperty("formatBuyPrice")
    public Double formatBuyPrice;

    @JsonProperty("rarityName")
    public String rarityName;

    @JsonProperty("categoryName")
    public String categoryName;

    @JsonProperty("formatCraftingMargin")
    public Double formatCraftingMargin;

    @JsonProperty("formatCraftingBuySum")
    public Double formatCraftingBuySum;

    @JsonProperty("recipeId")
    public Integer recipeId;

    public Double buySellRatio;

    public Double speedBuySellRatio;

    public Double craftRatio;

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

    public Double getFormatSellPrice() {
        return formatSellPrice;
    }

    public Double getFormatBuyPrice() {
        return formatBuyPrice;
    }

    public String getRarityName() {
        return rarityName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Double getFormatCraftingMargin() {
        return formatCraftingMargin;
    }

    public Double getFormatCraftingBuySum() {
        return formatCraftingBuySum;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public Double getBuySellRatio() {
        return buySellRatio;
    }

    public Double getSpeedBuySellRatio() {
        return speedBuySellRatio;
    }

    public Double getCraftRatio() {
        return craftRatio;
    }
}
