package db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.math3.util.Pair;
import parser.Price;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public List<Price> getSellValues() {
        return sellValues;
    }

    public void setSellValues(List<Price> sellValues) {
        this.sellValues = sellValues;
    }

    public LocalDateTime getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(LocalDateTime timeRecord) {
        this.timeRecord = timeRecord;
    }
}
