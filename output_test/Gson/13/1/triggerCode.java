/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.stream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import junit.framework.TestCase;
import static com.google.gson.stream.JsonToken.BEGIN_ARRAY;
import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;
import static com.google.gson.stream.JsonToken.BOOLEAN;
import static com.google.gson.stream.JsonToken.END_ARRAY;
import static com.google.gson.stream.JsonToken.END_OBJECT;
import static com.google.gson.stream.JsonToken.NAME;
import static com.google.gson.stream.JsonToken.NULL;
import static com.google.gson.stream.JsonToken.NUMBER;
import static com.google.gson.stream.JsonToken.STRING;
public final class JsonReaderTest extends TestCase {
  private void assertNotANumber(String s) throws IOException {
    JsonReader reader = new JsonReader(reader("[" + s + "]"));
    reader.setLenient(true);
    reader.beginArray();
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals(s, reader.nextString());
    reader.endArray();
  }
  private void testFailWithPosition(String message, String json) throws IOException {
    // Validate that it works reading the string normally.
    JsonReader reader1 = new JsonReader(reader(json));
    reader1.setLenient(true);
    reader1.beginArray();
    reader1.nextString();
    try {
      reader1.peek();
      fail();
    } catch (IOException expected) {
      assertEquals(message, expected.getMessage());
    }

    // Also validate that it works when skipping.
    JsonReader reader2 = new JsonReader(reader(json));
    reader2.setLenient(true);
    reader2.beginArray();
    reader2.skipValue();
    try {
      reader2.peek();
      fail();
    } catch (IOException expected) {
      assertEquals(message, expected.getMessage());
    }
  }
  private String repeat(char c, int count) {
    char[] array = new char[count];
    Arrays.fill(array, c);
    return new String(array);
  }
  private void assertDocument(String document, Object... expectations) throws IOException {
    JsonReader reader = new JsonReader(reader(document));
    reader.setLenient(true);
    for (Object expectation : expectations) {
      if (expectation == BEGIN_OBJECT) {
        reader.beginObject();
      } else if (expectation == BEGIN_ARRAY) {
        reader.beginArray();
      } else if (expectation == END_OBJECT) {
        reader.endObject();
      } else if (expectation == END_ARRAY) {
        reader.endArray();
      } else if (expectation == NAME) {
        assertEquals("name", reader.nextName());
      } else if (expectation == BOOLEAN) {
        assertEquals(false, reader.nextBoolean());
      } else if (expectation == STRING) {
        assertEquals("string", reader.nextString());
      } else if (expectation == NUMBER) {
        assertEquals(123, reader.nextInt());
      } else if (expectation == NULL) {
        reader.nextNull();
      } else if (expectation == IOException.class) {
        try {
          reader.peek();
          fail();
        } catch (IOException expected) {
        }
      } else {
        throw new AssertionError();
      }
    }
  }
  /**
   * Returns a reader that returns one character at a time.
   */
  private Reader reader(final String s) {
    /* if (true) */ return new StringReader(s);
    /* return new Reader() {
      int position = 0;
      @Override public int read(char[] buffer, int offset, int count) throws IOException {
        if (position == s.length()) {
          return -1;
        } else if (count > 0) {
          buffer[offset] = s.charAt(position++);
          return 1;
        } else {
          throw new IllegalArgumentException();
        }
      }
      @Override public void close() throws IOException {
      }
    }; */
  }
  /**
   * Issue 1053, negative zero.
   * @throws Exception
   */
  public void testNegativeZero() throws Exception {
	  	JsonReader reader = new JsonReader(reader("[-0]"));
	    reader.setLenient(false);
	    reader.beginArray();
	    assertEquals(NUMBER, reader.peek());
	    assertEquals("-0", reader.nextString());
  }
}
