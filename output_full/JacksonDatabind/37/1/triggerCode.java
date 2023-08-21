package com.fasterxml.jackson.databind.objectid;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
public class Objecid1083Test extends BaseMapTest
{
      /*****************************************************
       */

      public void testSimple() throws Exception {
          final ObjectMapper mapper = new ObjectMapper();
          final String json = aposToQuotes("{'schemas': [{\n"
              + "  'name': 'FoodMart'\n"
              + "}]}\n");
          mapper.readValue(json, JsonRoot.class);
      }
}
