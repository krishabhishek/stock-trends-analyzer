package ca.uwaterloo.stock_trends_analyzer.serializers;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date>
{
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException
    {
        DateTime dateTime = new DateTime(value.toInstant());
        gen.writeObject(Constants.DATETIME_FORMATTER.print(dateTime));
    }
}
