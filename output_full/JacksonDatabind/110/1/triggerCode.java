package com.fasterxml.jackson.databind.deser.jdk;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
public class UtilCollectionsTypesTest extends BaseMapTest
{
   private final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
   public void testUnmodifiableListFromLinkedList() throws Exception {
       final List<String> input = new LinkedList<>();
       input.add("first");
       input.add("second");

       // Can't use simple "_verifyCollection" as type may change; instead use
       // bit more flexible check:
       Collection<?> act = _writeReadCollection(Collections.unmodifiableList(input));
       assertEquals(input, act);

       // and this check may be bit fragile (may need to revisit), but is good enough for now:
       assertEquals(Collections.unmodifiableList(new ArrayList<>(input)).getClass(), act.getClass());
   }
}
