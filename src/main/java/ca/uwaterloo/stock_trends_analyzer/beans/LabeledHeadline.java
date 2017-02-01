package ca.uwaterloo.stock_trends_analyzer.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabeledHeadline
{
    private String label;
    private String orgName;
    private String headline;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LabeledHeadline that = (LabeledHeadline) o;

        return new EqualsBuilder()
            .append(orgName, that.orgName)
            .append(headline, that.headline)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
            .append(orgName)
            .append(headline)
            .toHashCode();
    }
}
