package ca.uwaterloo.stock_trends_analyzer.beans;

import ca.uwaterloo.stock_trends_analyzer.enums.Sentiment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trend
{
    private Sentiment sentiment;
    private Date startDate;
    private Date endDate;
    private Double slope;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Trend trend = (Trend) o;

        return new EqualsBuilder()
            .append(startDate, trend.startDate)
            .append(endDate, trend.endDate)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
            .append(startDate)
            .append(endDate)
            .toHashCode();
    }
}
