package calc;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import parser.*;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class CraftService {
    public static final String URI_ITEMS = "https://crossoutdb.com/api/v1/items";

    private List<Item> itemList = new ArrayList<>();
    private Map<Integer, ArrayDeque<Item>> itemIdToItemMap = new HashMap<>();

    public void readSomeData() {
        itemList = Calculation.calculation(fastParseAllItems());
        fillMapWithTime(itemList);
        for (Integer integer : itemIdToItemMap.keySet()) {
            Item last = itemIdToItemMap.get(integer).getLast();
            Item first = itemIdToItemMap.get(integer).getFirst();
            if (last == first) {
                last.setSpeedBuySellRatio(0.0);
                continue;
            }
            last.setSpeedBuySellRatio(last.getBuySellRatio() - first.getBuySellRatio());
        }
    }

    private void fillMapWithTime(List<Item> itemList) {
        for (Item item : itemList) {
            if (!itemIdToItemMap.containsKey(item.getId())) {
                itemIdToItemMap.put(item.getId(), new ArrayDeque<>());
            }
            itemIdToItemMap.get(item.getId()).add(item);
            if (itemIdToItemMap.get(item.getId()).size() > 10) {
                itemIdToItemMap.get(item.getId()).pollFirst();
            }
        }
    }

    public void print() {
        System.out.println();
        System.out.println("Static buy/sell buySellRatio:");
        for (Item item : itemList) {
            if (item.getBuySellRatio() < 2) {
                break;
            }
            System.out.println(item.getName() + " " + item.getBuySellRatio() + " " + item.getRarityName() + " " + item.getCategoryName());
        }
        System.out.println();
        System.out.println();

        System.out.println("Static good craft cost");
        itemList.sort(Comparator.comparing(Item::getCraftRatio).reversed());
        for (Item item : itemList) {
            if (item.getCraftRatio() < 0.2) {
                continue;
            }
            System.out.println(item.getName() + " " + item.getCraftRatio() + " " + item.getRarityName() + " " + item.getCategoryName());
        }

        System.out.println();
        System.out.println();
        System.out.println("Speed good craft cost:");
        itemList.sort(Comparator.comparing(Item::getSpeedBuySellRatio).reversed());
        for (Item item : itemList) {
            if (item.getBuySellRatio() < 1 || item.getSpeedBuySellRatio() < 0.1) {
                continue;
            }
            System.out.println(item.getName() + " " + item.getBuySellRatio() + " " + item.getSpeedBuySellRatio() + " "
                    + item.getRarityName() + " " + item.getCategoryName());
        }
        System.out.println();
        System.out.println();
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void makeIteration() {
        try {
            System.out.println("Start job");
            readSomeData();
            print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, String> getAllIdAndNames() {
        List<Item> itemList = fastParseAllItems();
        return itemList.stream().collect(Collectors.toMap(Item::getId, Item::getName));
    }

    public List<Item> fastParseAllItems() {
        SkipSslVerificationHttpRequestFactory httpRequestFactory = new SkipSslVerificationHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        String result = restTemplate.getForObject(URI_ITEMS, String.class);

        List<Item> itemList = Arrays.asList(ItemParser.parseItem(result));
        return itemList.stream()
                .filter(a -> a.getBuyOrders() != 0)
                .filter(a -> RarityEnum.Rare.toString().equals(a.getRarityName())
                        || RarityEnum.Epic.toString().equals(a.getRarityName())
                        || RarityEnum.Special.toString().equals(a.getRarityName())
                        || RarityEnum.Legendary.toString().equals(a.getRarityName())
                )
                .filter(a -> CategoryEnum.Weapons.toString().equals(a.getRarityName())
                        || CategoryEnum.Cabins.toString().equals(a.getRarityName())
                        || CategoryEnum.Hardware.toString().equals(a.getRarityName())
                        || CategoryEnum.Movement.toString().equals(a.getRarityName())
                ).collect(Collectors.toList());
    }
}
