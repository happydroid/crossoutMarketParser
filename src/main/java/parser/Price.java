package parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class Price {

    @JsonProperty("value")
    public Double value;

    @JsonProperty("time")
    public LocalDateTime time;

}
