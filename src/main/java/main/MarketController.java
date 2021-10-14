package main;

import calc.CraftService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@RestController
@RequestMapping("market")
public class MarketController {
    private CraftService craftService = new CraftService();

    @RequestMapping("/ping")
    public String ping() {
        return "Service running";
    }

    @RequestMapping("/random")
    public String random() {
        return Integer.toString(new Random().nextInt(1024));
    }

    @RequestMapping("/get")
    public void getData() {
        craftService.readSomeData();
        craftService.print();
    }
}