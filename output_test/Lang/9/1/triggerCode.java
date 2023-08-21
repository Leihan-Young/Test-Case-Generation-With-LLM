/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional inparserion regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import junit.framework.Assert;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
/**
 * Unit tests {@link org.apache.commons.lang3.time.FastDateParser}.
 *
 * @since 3.2
 */
public class FastDateParserTest {
    private static final String SHORT_FORMAT_NOERA = "y/M/d/h/a/E/Z";
    private static final String LONG_FORMAT_NOERA = "yyyy/MMMM/dddd/hhhh/aaaa/EEEE/ZZZZ";
    private static final String SHORT_FORMAT = "G/" + SHORT_FORMAT_NOERA;
    private static final String LONG_FORMAT = "GGGG/" + LONG_FORMAT_NOERA;
    private static final String yMdHmsSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
    private static final String DMY_DOT = "dd.MM.yyyy";
    private static final String YMD_SLASH = "yyyy/MM/dd";
    private static final String MDY_DASH = "MM-DD-yyyy";
    private static final String MDY_SLASH = "MM/DD/yyyy";
    private static final TimeZone REYKJAVIK = TimeZone.getTimeZone("Atlantic/Reykjavik");
    private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final Locale SWEDEN = new Locale("sv", "SE");
    private DateParser getDateInstance(int dateStyle, Locale locale) {
        return getInstance(FormatCache.getPatternForStyle(Integer.valueOf(dateStyle), null, locale), TimeZone.getDefault(), Locale.getDefault());
    }
    private DateParser getInstance(String format, Locale locale) {
        return getInstance(format, TimeZone.getDefault(), locale);
    }
    private DateParser getInstance(String format, TimeZone timeZone) {
        return getInstance(format, timeZone, Locale.getDefault());
    }
    private void testLocales(String format, boolean eraBC) throws Exception {

        Calendar cal= Calendar.getInstance(GMT);
        cal.clear();
        cal.set(2003, 1, 10);
        if (eraBC) {
            cal.set(Calendar.ERA, GregorianCalendar.BC);
        }
        for(Locale locale : Locale.getAvailableLocales()) {
            // ja_JP_JP cannot handle dates before 1868 properly
            if (eraBC && locale.equals(FastDateParser.JAPANESE_IMPERIAL)) {
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
            DateParser fdf = getInstance(format, locale);

            try {
                checkParse(locale, cal, sdf, fdf);
            } catch(ParseException ex) {
                Assert.fail("Locale "+locale+ " failed with "+format+" era "+(eraBC?"BC":"AD")+"\n" + trimMessage(ex.toString()));
            }
        }
    }
    private String trimMessage(String msg) {
        if (msg.length() < 100) {
            return msg;
        }
        int gmt = msg.indexOf("(GMT");
        if (gmt > 0) {
            return msg.substring(0, gmt+4)+"...)";
        }
        return msg.substring(0, 100)+"...";
    }
    private void checkParse(Locale locale, Calendar cal, SimpleDateFormat sdf, DateParser fdf) throws ParseException {
        String formattedDate= sdf.format(cal.getTime());
        Date expectedTime = sdf.parse(formattedDate);
        Date actualTime = fdf.parse(formattedDate);
        assertEquals(locale.toString()+" "+formattedDate
                +"\n",expectedTime, actualTime);
    }
    private void testSdfAndFdp(String format, String date, boolean shouldFail)
            throws Exception {
        Date dfdp = null;
        Date dsdf = null;
        Throwable f = null;
        Throwable s = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            sdf.setTimeZone(NEW_YORK);
            dsdf = sdf.parse(date);
            if (shouldFail) {
                Assert.fail("Expected SDF failure, but got " + dsdf + " for ["+format+","+date+"]");
            }
        } catch (Exception e) {
            s = e;
            if (!shouldFail) {
                throw e;
            }
//            System.out.println("sdf:"+format+"/"+date+"=>"+e);
        }

        try {
            DateParser fdp = getInstance(format, NEW_YORK, Locale.US);
            dfdp = fdp.parse(date);
            if (shouldFail) {
                Assert.fail("Expected FDF failure, but got " + dfdp + " for ["+format+","+date+"] using "+((FastDateParser)fdp).getParsePattern());
            }
        } catch (Exception e) {
            f = e;
            if (!shouldFail) {
                throw e;
            }
//            System.out.println("fdf:"+format+"/"+date+"=>"+e);
        }
        // SDF and FDF should produce equivalent results
        assertTrue("Should both or neither throw Exceptions", (f==null)==(s==null));
        assertEquals("Parsed dates should be equal", dsdf, dfdp);
    }
    @Test
    public void testLANG_832() throws Exception {
        testSdfAndFdp("'d'd" ,"d3", false); // OK
        testSdfAndFdp("'d'd'","d3", true); // should fail (unterminated quote)
    }
}
