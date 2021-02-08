package main;

import calc.CraftService;
import calc.ExtremumService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    ExtremumService extremumService() {
        return new ExtremumService();
    }

    @Bean
    CraftService craftService() {
        return new CraftService();
    }

}
