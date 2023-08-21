/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
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
package org.apache.commons.collections4.keyvalue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Unit tests for {@link org.apache.commons.collections4.keyvalue.MultiKey}.
 *
 * @version $Id$
 */
public class MultiKeyTest extends TestCase {
        private Object readResolve() {
            hashCode=2; // simulate different hashCode after deserialization in another process
            return this;
        }
    public void testEqualsAfterSerializationOfDerivedClass() throws IOException, ClassNotFoundException
    {
        final DerivedMultiKey<?> mk = new DerivedMultiKey<String>("A", "B");

        // serialize
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(mk);
        out.close();

        // deserialize
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream in = new ObjectInputStream(bais);
        final DerivedMultiKey<?> mk2 = (DerivedMultiKey<?>)in.readObject();
        in.close();

        assertEquals(mk.hashCode(), mk2.hashCode());
    }
}
