package parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {

    @JsonProperty("value")
    public Double value;

    @JsonProperty("time")
    public LocalDateTime time;


    public Price() {
    }

    public Price(Double value, LocalDateTime time) {
        this.value = value;
        this.time = time;
    }
}
