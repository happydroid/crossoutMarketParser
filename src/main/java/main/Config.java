package main;

import calc.CraftService;
import calc.ExtremumService;
import db.MongoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MongoConfig.class})
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
