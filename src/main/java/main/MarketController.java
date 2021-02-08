package main;

import calc.CraftService;
import org.springframework.web.bind.annotation.RequestMapping;

//@RestController
//@RequestMapping("market")
public class MarketController {

    private CraftService craftService = new CraftService();

    @RequestMapping("/ping")
    public void ping() {
        craftService.readSomeData();
        craftService.print();
    }

}
