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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableScheduling
@Import({MongoConfig.class})
public class ExtremumService {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static final Random random = new Random();

    private volatile static AtomicInteger currentItems = new AtomicInteger();
    private static int maxItems = 0;
    private static Map<Integer, String> itemIdToName;
    private CraftService craftService = new CraftService();
    private List<ItemDb> allItems = new CopyOnWriteArrayList<>();

    @Autowired
    private ItemLoader itemLoader;

    //@Scheduled(fixedRate = 1000000000)
    public void startParseSite() {
        itemIdToName = craftService.getIdAndNames();
        maxItems = itemIdToName.size();

        ExecutorService exec = Executors.newFixedThreadPool(2);
        try {
            for (Integer itemId : itemIdToName.keySet()) {
                //todo parse only items, that hasnt in db
                exec.submit(() -> {
                    ItemDb itemDb = parseFromSite(itemId);
                    itemLoader.write(itemDb);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
        }
    }

    /*private List<ItemDb> loadDataFromDb() {
        LocalDateTime now = LocalDateTime.now();
        List<ItemDb> itemDbs = new ArrayList<>();
        for (ItemDb itemDb : itemLoader.getAll().find()) {
            if (itemDb.getTimeRecord().plusDays(2).isAfter(now)) {
                itemDbs.add(itemDb);
            }
        }
        return itemDbs;
    }*/

    @Scheduled(fixedRate = 1000000000)
    public void loadDataFromDb() {
        Map<Double, String> ratioToName = new HashMap<>();

        for (ItemDb itemDb : itemLoader.getAll().find()) {
            Median median = new Median();
            List<Double> doubles = itemDb.getSellValues().stream().map(Price::getValue).collect(Collectors.toList());

            double valueMax = median.evaluate(doubles.stream().mapToDouble(i -> i).toArray(), 90);
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

        System.exit(0);
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
        itemDb.setName(itemIdToName.get(itemId));
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
