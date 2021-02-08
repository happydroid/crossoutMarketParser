package calc;

import main.Config;
import main.SkipSslVerificationHttpRequestFactory;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import parser.Price;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ExtremumService {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private static Random random = new Random();

    private volatile static AtomicInteger currentItems = new AtomicInteger();
    private static int maxItems = 0;
    private static Map<Integer, String> itemIdToName;

    private CraftService craftService = new CraftService();

    public void readAndCalcData() {
        Map<String, Double> ratioToName = new ConcurrentHashMap<>();
        itemIdToName = craftService.getIdAndNames();
        maxItems = itemIdToName.size();

        ExecutorService exec = Executors.newFixedThreadPool(2);
        try {
            for (Integer itemId : itemIdToName.keySet()) {
                exec.submit(new Runnable() {
                    @Override
                    public void run() {
                        Optional<Double> optionalDouble = makeWork(itemId);
                        optionalDouble.ifPresent(aDouble -> {
                            System.out.println(itemIdToName.get(itemId) + " " + aDouble);
                            ratioToName.put(itemIdToName.get(itemId), aDouble);

                        });
                    }
                });
            }
        } finally {
            exec.shutdown();
        }

        Stream<Map.Entry<String, Double>> sorted = ratioToName.entrySet().stream().sorted(Map.Entry.comparingByValue());
        sorted.forEach(name -> System.out.println(name + " " + ratioToName.get(name)));
    }

    private Optional<Double> makeWork(Integer itemId) {
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
                return makeWork(itemId);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("Try load data again for id = " + itemId);
            return makeWork(itemId);
        }

        List<Double> doubles = new ArrayList<>();
        List<Price> prices = Arrays.stream(objects)
                .map(list -> {
                    String time = (String) list.get(0);
                    Integer value = (Integer) list.get(1);
                    doubles.add(value / 100.0);
                    return new Price(value / 100.0, LocalDateTime.parse(time, dtf));
                }).collect(Collectors.toList());

        Median median = new Median();
        double valueMax = median.evaluate(doubles.stream().mapToDouble(i -> i).toArray(), 95);

        double valueNow = (doubles.get(doubles.size() - 1));

        /*double valueNow = (doubles.get(doubles.size() - 1)
                + doubles.get(doubles.size() - 2)
                + doubles.get(doubles.size() - 3)
                + doubles.get(doubles.size() - 4)
                + doubles.get(doubles.size() - 5)) / 5.0;*/

        System.out.println(itemIdToName.get(itemId) + " valueNow " + valueNow + " valueMax " + valueMax);
        System.out.println(currentItems.incrementAndGet() / maxItems * 1.0 + "%");

        if (valueMax < valueNow) {
            return Optional.of(valueNow / valueMax);
        }

        return Optional.empty();
    }

    public static void main(String[] args) {
        //new AnnotationConfigApplicationContext(ExtremumService.class);
        new AnnotationConfigApplicationContext(Config.class);
        new ExtremumService().readAndCalcData();
    }
}
