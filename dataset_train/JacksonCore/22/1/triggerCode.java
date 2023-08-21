package com.fasterxml.jackson.core.filter;
import java.util.*;
import com.fasterxml.jackson.core.*;
public class BasicParserFilteringTest extends BaseTest
{
    public void testSingleMatchFilteringWithPath() throws Exception
    {
        String jsonString = aposToQuotes("{'a':123,'array':[1,2],'ob':{'value0':2,'value':3,'value2':4},'b':true}");
        JsonParser p0 = JSON_F.createParser(jsonString);
        FilteringParserDelegate p = new FilteringParserDelegate(p0,
                new NameMatchFilter("a"),
                true, // includePath
                false // multipleMatches
        );
        String result = readAndWrite(JSON_F, p);
        assertEquals(aposToQuotes("{'a':123}"), result);
        assertEquals(1, p.getMatchCount());
    }
}
