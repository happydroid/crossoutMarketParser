package parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Item {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("sellOffers")
    private Integer sellOffers;

    @JsonProperty("buyOrders")
    private Integer buyOrders;

    @JsonProperty("formatSellPrice")
    private Double formatSellPrice;

    @JsonProperty("formatBuyPrice")
    private Double formatBuyPrice;

    @JsonProperty("rarityName")
    private String rarityName;

    @JsonProperty("categoryName")
    private String categoryName;

    @JsonProperty("formatCraftingMargin")
    private Double formatCraftingMargin;

    @JsonProperty("formatCraftingBuySum")
    private Double formatCraftingBuySum;

    @JsonProperty("recipeId")
    private Integer recipeId;

    private Double buySellRatio;

    private Double speedBuySellRatio;

    private Double craftRatio;

}
