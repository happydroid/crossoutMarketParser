package db;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "local";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        final String uriString = "mongodb://$[username]:$[password]@$[hostlist]/$[database]?authSource=$[authSource]";
        return MongoClients.create(uriString);
    }
}