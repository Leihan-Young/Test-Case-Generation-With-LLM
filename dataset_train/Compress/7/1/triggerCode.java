/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.apache.commons.compress.archivers.tar;
import junit.framework.TestCase;
public class TarUtilsTest extends TestCase {
    private void fillBuff(byte []buffer, String input) throws Exception{
        for(int i=0; i<buffer.length;i++){
            buffer[i]=0;
        }
        System.arraycopy(input.getBytes("UTF-8"),0,buffer,0,Math.min(buffer.length,input.length()));        
    }
    private void checkRoundTripOctal(final long value) {
        byte [] buffer = new byte[12];
        long parseValue;
        TarUtils.formatLongOctalBytes(value, buffer, 0, buffer.length);
        parseValue = TarUtils.parseOctal(buffer,0, buffer.length);
        assertEquals(value,parseValue);
    }
    private void checkName(String string) {
        byte buff[] = new byte[100];
        int len = TarUtils.formatNameBytes(string, buff, 0, buff.length);
        assertEquals(string, TarUtils.parseName(buff, 0, len));
    }
    public void testRoundTripNames(){
        checkName("");
        checkName("The quick brown fox\n");
        checkName("\177");
        // checkName("\0"); // does not work, because NUL is ignored
        // COMPRESS-114
        checkName("0302-0601-3北盕06盬220盳B盠ALALA北北北北北CAN北DC北04060302盡OE.model");
    }
}
