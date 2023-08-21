/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
/**
 * A simple unit test for the {@link DefaultDateTypeAdapter} class.
 *
 * @author Joel Leitch
 */
public class DefaultDateTypeAdapterTest extends TestCase {
  private void assertFormattingAlwaysEmitsUsLocale(Locale locale) {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(locale);
    try {
      assertFormatted("Jan 1, 1970 12:00:00 AM", new DefaultDateTypeAdapter());
      assertFormatted("1/1/70", new DefaultDateTypeAdapter(DateFormat.SHORT));
      assertFormatted("Jan 1, 1970", new DefaultDateTypeAdapter(DateFormat.MEDIUM));
      assertFormatted("January 1, 1970", new DefaultDateTypeAdapter(DateFormat.LONG));
      assertFormatted("1/1/70 12:00 AM",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
      assertFormatted("Jan 1, 1970 12:00:00 AM",
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
      assertFormatted("January 1, 1970 12:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
      assertFormatted("Thursday, January 1, 1970 12:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }
  private void assertFormatted(String formatted, DefaultDateTypeAdapter adapter) {
    assertEquals(formatted, adapter.serialize(new Date(0), Date.class, null).getAsString());
  }
  private void assertParsed(String date, DefaultDateTypeAdapter  adapter) {
    assertEquals(date, new Date(0), adapter.deserialize(new JsonPrimitive(date), Date.class, null));
    assertEquals("ISO 8601", new Date(0), adapter.deserialize(
        new JsonPrimitive("1970-01-01T00:00:00Z"), Date.class, null));
  }
  public void testDateDeserializationISO8601() throws Exception {
  	DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter();
    assertParsed("1970-01-01T00:00:00.000Z", adapter);
    assertParsed("1970-01-01T00:00Z", adapter);
    assertParsed("1970-01-01T00:00:00+00:00", adapter);
    assertParsed("1970-01-01T01:00:00+01:00", adapter);
    assertParsed("1970-01-01T01:00:00+01", adapter);
  }
}
