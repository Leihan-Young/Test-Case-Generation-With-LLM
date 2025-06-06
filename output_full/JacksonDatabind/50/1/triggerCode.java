package com.fasterxml.jackson.databind.objectid;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
public class ObjectWithCreator1261Test
    extends BaseMapTest
{
   private Answer createInitialAnswer() {
      Answer answer = new Answer();
      String child1Name = "child1";
      String child2Name = "child2";
      String parent1Name = "parent1";
      String parent2Name = "parent2";
      Parent parent1 = new Parent(parent1Name, false);
      answer.parents.put(parent1Name, parent1);
      Child child1 = new Child(child1Name);
      child1.parent = parent1;
      child1.parentAsList = Collections.singletonList(parent1);
      Child child2 = new Child(child2Name);
      Parent parent2 = new Parent(parent2Name, false);
      child2.parent = parent2;
      child2.parentAsList = Collections.singletonList(parent2);
      parent1.children.put(child1Name, child1);
      parent1.children.put(child2Name, child2);
      answer.parents.put(parent2Name, parent2);
      return answer;
   }
    public void testObjectIds1261() throws Exception
    {
         ObjectMapper mapper = new ObjectMapper();
         mapper.enable(SerializationFeature.INDENT_OUTPUT);
         mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

         Answer initialAnswer = createInitialAnswer();
         String initialAnswerString = mapper.writeValueAsString(initialAnswer);
// System.out.println("Initial answer:\n"+initialAnswerString);
         JsonNode tree = mapper.readTree(initialAnswerString);
         Answer deserializedAnswer = mapper.readValue(initialAnswerString,
               Answer.class);
         String reserializedAnswerString = mapper
               .writeValueAsString(deserializedAnswer);
         JsonNode newTree = mapper.readTree(reserializedAnswerString);
         if (!tree.equals(newTree)) {
                  fail("Original and recovered Json are different. Recovered = \n" + reserializedAnswerString + "\n");
         }
   }
}
