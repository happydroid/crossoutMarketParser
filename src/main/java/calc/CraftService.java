package calc;

import main.RarityEnum;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import parser.Item;
import parser.ItemParser;
import parser.SkipSslVerificationHttpRequestFactory;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class CraftService {

    private List<Item> itemList = new ArrayList<>();
    private Map<Integer, ArrayDeque<Item>> integerQueueMap = new HashMap<>();

    public void readSomeData() {
        itemList = Calculation.calculation(itemList);
        fillMapWithTime(itemList);
        for (Integer integer : integerQueueMap.keySet()) {
            Item last = integerQueueMap.get(integer).getLast();
            Item first = integerQueueMap.get(integer).getFirst();
            if (last == first) {
                last.speedBuySellRatio = 0f;
                continue;
            }
            last.speedBuySellRatio = last.buySellRatio - first.buySellRatio;
        }
    }

    private void fillMapWithTime(List<Item> itemList) {
        for (Item item : itemList) {
            if (!integerQueueMap.containsKey(item.id)) {
                integerQueueMap.put(item.id, new ArrayDeque<>());
            }
            integerQueueMap.get(item.id).add(item);
            if (integerQueueMap.get(item.id).size() > 10) {
                integerQueueMap.get(item.id).pollFirst();
            }
        }
    }

    public void print() {
        System.out.println();
        System.out.println("Static buy/sell buySellRatio:");
        for (Item item : itemList) {
            if (item.buySellRatio < 2) {
                break;
            }
            System.out.println(item.name + " " + item.buySellRatio + " " + item.rarityName + " " + item.categoryName);
        }
        System.out.println();
        System.out.println();

        System.out.println("Static good craft cost");
        itemList.sort(Comparator.comparing(Item::getCraftRatio).reversed());
        for (Item item : itemList) {
            if (item.craftRatio < 0.2) {
                continue;
            }
            System.out.println(item.name + " " + item.craftRatio + " " + item.rarityName + " " + item.categoryName);
        }

        System.out.println();
        System.out.println();
        System.out.println("Speed good craft cost:");
        itemList.sort(Comparator.comparing(Item::getSpeedBuySellRatio).reversed());
        for (Item item : itemList) {
            if (item.buySellRatio < 1 || item.speedBuySellRatio < 0.1) {
                continue;
            }
            System.out.println(item.name + " " + item.buySellRatio + " " + item.speedBuySellRatio + " " + item.rarityName
                    + " " + item.categoryName);
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

    public Map<Integer, String> getIdAndNames() {
        List<Item> itemList = parseFromSite();
        return itemList.stream().collect(Collectors.toMap(Item::getId, Item::getName));
    }

    private List<Item> parseFromSite() {
        final String uri = "https://crossoutdb.com/api/v1/items";

        SkipSslVerificationHttpRequestFactory httpRequestFactory = new SkipSslVerificationHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        String result = restTemplate.getForObject(uri, String.class);

        List<Item> itemList = Arrays.asList(ItemParser.parseItem(result));
        return itemList.stream()
                .filter(a -> a.buyOrders != 0)
                .filter(a -> RarityEnum.Rare.toString().equals(a.rarityName)
                        || RarityEnum.Epic.toString().equals(a.rarityName))
                .filter(a -> CategoryEnum.Weapons.toString().equals(a.categoryName)
                        || CategoryEnum.Cabins.toString().equals(a.categoryName)
                        || CategoryEnum.Hardware.toString().equals(a.categoryName)
                        || CategoryEnum.Movement.toString().equals(a.categoryName)
                ).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(CraftService.class);
    }
}
