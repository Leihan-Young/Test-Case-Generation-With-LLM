package com.fasterxml.jackson.databind.ser.jdk;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Assert;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
public class DateSerializationTest
    extends BaseMapTest
{
    private static Date judate(int year, int month, int day, int hour, int minutes, int seconds, int millis, String tz) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day, hour, minutes, seconds);
        cal.set(Calendar.MILLISECOND, millis);
        cal.setTimeZone(TimeZone.getTimeZone(tz));

        return cal.getTime();
    }
    private void serialize(ObjectMapper mapper, Object date, String expected) throws IOException {
        Assert.assertEquals(quote(expected), mapper.writeValueAsString(date));
    }
    private void serialize(ObjectWriter w, Object date, String expected) throws IOException {
        Assert.assertEquals(quote(expected), w.writeValueAsString(date));
    }
    public void testDateISO8601_10k() throws IOException
    {
        ObjectWriter w = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serialize(w, judate(10204, 1, 1,  00, 00, 00, 0, "UTC"),   "+10204-01-01T00:00:00.000+0000");
        // and although specification lacks for beyond 5 digits (well, actually even 5...), let's do our best:
        serialize(w, judate(123456, 1, 1,  00, 00, 00, 0, "UTC"),   "+123456-01-01T00:00:00.000+0000");
    }
}
