package ca.uwaterloo.stock_trends_analyzer.beans;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.enums.Sentiment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trend
{
    private Sentiment sentiment;
    private DateTime startDate;
    private DateTime endDate;
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

    public String formatToTSV()
    {
        return
            sentiment + "\t" +
            Constants.DATETIME_FORMATTER.print(startDate) + "\t" +
            Constants.DATETIME_FORMATTER.print(endDate) + "\t" +
            slope;
    }

    public static String getTSVHeader()
    {
        return
            "SENTIMENT" + "\t" +
            "START_DATE" + "\t" +
            "END_DATE" + "\t" +
            "SLOPE";
    }
}
