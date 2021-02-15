package calc;

import db.ItemDb;
import db.ItemLoader;
import db.MongoConfig;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import parser.Price;
import parser.SkipSslVerificationHttpRequestFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableScheduling
@Import({MongoConfig.class})
public class ExtremumService {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final Random random = new Random();

    private CraftService craftService = new CraftService();
    private List<ItemDb> allItems = new CopyOnWriteArrayList<>();
    private Map<Integer, String> idToNameAllItems;

    @Autowired
    private ItemLoader itemLoader;

    @Scheduled(fixedRate = 1000000000)
    public void complexLoadMain() {
        //load and calc on actual data
        List<ItemDb> itemsFromDb = loadActualItemsFromDb();
        if (itemsFromDb.isEmpty()) {
            System.err.println("No actual data");
        } else {
            calcRatioAndPrint(itemsFromDb);
        }

        //refresh deprecated items
        idToNameAllItems = craftService.getAllIdAndNames();
        System.err.println("Actual items: " + itemsFromDb.size());
        System.err.println("All items: " + idToNameAllItems.size());

        Set<Integer> actualIdSet = itemsFromDb.stream().map(ItemDb::getId).collect(Collectors.toSet());
        List<Integer> itToParse = idToNameAllItems.keySet().stream().filter(idItem -> !actualIdSet.contains(idItem)).collect(Collectors.toList());

        startParseSite(itToParse);

        //all data
        calcRatioAndPrint(loadActualItemsFromDb());
        System.exit(0);
    }

    private void startParseSite(List<Integer> idList) {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        List<Runnable> tasks = new ArrayList<>();
        for (Integer itemId : idList) {
            tasks.add(() -> {
                ItemDb itemDb = parseFromSite(itemId);
                itemLoader.write(itemDb);
            });
        }

        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, exec))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        exec.shutdown();
    }

    private List<ItemDb> loadActualItemsFromDb() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemDb> itemDbs = new ArrayList<>();
        for (ItemDb itemDb : itemLoader.getAll().find()) {
            if (itemDb.getTimeRecord().plusDays(2).isAfter(now)) {
                itemDbs.add(itemDb);
            }
        }
        return itemDbs;
    }

    private void calcRatioAndPrint(List<ItemDb> itemDbs) {
        Map<Double, String> ratioToName = new HashMap<>();
        for (ItemDb itemDb : itemDbs) {
            Median median = new Median();
            List<Double> doubles = itemDb.getSellValues().stream().map(Price::getValue).collect(Collectors.toList());

            double valueMax = median.evaluate(doubles.stream().mapToDouble(i -> i).toArray(), 95);
            double valueNow = (doubles.get(0)
                    + doubles.get(1)
                    + doubles.get(2)
                    + doubles.get(3)
                    + doubles.get(4)) / 5.0;
            if (valueMax < valueNow) {
                ratioToName.put(valueNow / valueMax, itemDb.getName());
            }
        }
        Stream<Map.Entry<Double, String>> sorted = ratioToName.entrySet().stream().sorted(Map.Entry.comparingByKey());
        sorted.forEach(pair -> System.out.println(pair.getKey() + " " + pair.getValue()));
    }

    private ItemDb parseFromSite(Integer itemId) {
        try {
            Thread.sleep((long) (random.nextFloat() * 200) + 200);
        } catch (Exception e) {

        }
        final String uri = "https://crossoutdb.com/api/v1/market/sellprice/" + itemId;
        SkipSslVerificationHttpRequestFactory httpRequestFactory = new SkipSslVerificationHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);

        ArrayList[] objects = null;
        try {
            ResponseEntity<ArrayList[]> responseEntity = restTemplate.getForEntity(uri, ArrayList[].class);
            objects = responseEntity.getBody();
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                System.err.println("Try load data again for id = " + itemId);
                return parseFromSite(itemId);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("Try load data again for id = " + itemId);
            return parseFromSite(itemId);
        }

        ItemDb itemDb = new ItemDb();
        itemDb.setId(itemId);
        itemDb.setName(idToNameAllItems.get(itemId));
        itemDb.setSellValues(
                Arrays.stream(objects)
                        .map(list -> {
                            String time = (String) list.get(0);
                            Integer value = (Integer) list.get(1);
                            return new Price(value / 100.0, LocalDateTime.parse(time, dtf));
                        }).collect(Collectors.toList())
        );
        itemDb.setTimeRecord(LocalDateTime.now());
        return itemDb;
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ExtremumService.class);
    }
}
