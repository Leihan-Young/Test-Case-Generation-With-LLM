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
import java.io.IOException;
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
      assertFormatted("Jan 1, 1970 12:00:00 AM", new DefaultDateTypeAdapter(Date.class));
      assertFormatted("1/1/70", new DefaultDateTypeAdapter(Date.class, DateFormat.SHORT));
      assertFormatted("Jan 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.MEDIUM));
      assertFormatted("January 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.LONG));
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
    assertEquals(toLiteral(formatted), adapter.toJson(new Date(0)));
  }
  private void assertParsed(String date, DefaultDateTypeAdapter adapter) throws IOException {
    assertEquals(date, new Date(0), adapter.fromJson(toLiteral(date)));
    assertEquals("ISO 8601", new Date(0), adapter.fromJson(toLiteral("1970-01-01T00:00:00Z")));
  }
  private static String toLiteral(String s) {
    return '"' + s + '"';
  }
  public void testUnexpectedToken() throws Exception {
    try {
      DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class);
      adapter.fromJson("{}");
      fail("Unexpected token should fail.");
    } catch (IllegalStateException expected) { }
  }
}
