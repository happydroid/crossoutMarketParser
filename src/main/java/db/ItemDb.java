package db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import parser.Price;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ItemDb {

    @JsonProperty("id")
    public Integer id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("ratio")
    private Double ratio;

    @JsonProperty("sellValues")
    public List<Price> sellValues;

    @JsonProperty("timeRecord")
    public LocalDateTime timeRecord;
}
