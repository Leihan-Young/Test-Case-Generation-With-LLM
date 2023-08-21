package com.fasterxml.jackson.databind.deser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
public class TestDateDeserialization
    extends BaseMapTest
{
    /**********************************************************
     */

    private String dateToString(java.util.Date value)
    {
        /* Then from String. This is bit tricky, since JDK does not really
         * suggest a 'standard' format. So let's try using something...
         */
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return df.format(value);
    }
    private static Calendar gmtCalendar(long time)
    {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTimeInMillis(time);
        return c;
    }
    public void testDateUtilISO8601NoTimezoneNonDefault() throws Exception
    {
        // In first case, no timezone -> SHOULD use configured timezone
        ObjectReader r = MAPPER.readerFor(Date.class);
        TimeZone tz = TimeZone.getTimeZone("GMT-2");
        Date date1 = r.with(tz)
                .readValue(quote("1970-01-01T00:00:00.000"));
        // Second case, should use specified timezone, not configured
        Date date2 = r.with(TimeZone.getTimeZone("GMT+5"))
                .readValue(quote("1970-01-01T00:00:00.000-02:00"));
        assertEquals(date1, date2);

        // also verify actual value, in GMT
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        c.setTime(date1);
        assertEquals(1970, c.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, c.get(Calendar.MONTH));
        assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
        assertEquals(2, c.get(Calendar.HOUR_OF_DAY));
    }
}
