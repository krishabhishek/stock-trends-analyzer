package ca.uwaterloo.stock_trends_analyzer.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Comparator;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPrice
{
    Long timestamp;
    Double closingPrice;
    private static Comparator<StockPrice> pricePointComparator;

    public static Comparator<StockPrice> getPricePointComparator()
    {
        return new Comparator()
        {
            @Override
            public int compare(Object o1, Object o2)
            {
                StockPrice p1 = (StockPrice) o1;
                StockPrice p2 = (StockPrice) o2;

                return p1.getTimestamp().compareTo(p2.getTimestamp());
            }
        };
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockPrice that = (StockPrice) o;

        return timestamp.equals(that.timestamp);

    }

    @Override
    public int hashCode()
    {
        return timestamp.hashCode();
    }
}
