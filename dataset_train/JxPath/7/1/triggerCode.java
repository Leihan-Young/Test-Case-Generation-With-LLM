/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jxpath.ri.compiler;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Variables;
/**
 * Test basic functionality of JXPath - infoset types,
 * operations.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class CoreOperationTest extends JXPathTestCase {
    private JXPathContext context;
    public void testNodeSetOperations() {
        assertXPathValue(context, "$array > 0", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array >= 0", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array = 0", Boolean.FALSE, Boolean.class);
        assertXPathValue(context, "$array = 0.25", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array = 0.5", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array = 0.50000", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array = 0.75", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array < 1", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array <= 1", Boolean.TRUE, Boolean.class);
        assertXPathValue(context, "$array = 1", Boolean.FALSE, Boolean.class);
        assertXPathValue(context, "$array > 1", Boolean.FALSE, Boolean.class);
        assertXPathValue(context, "$array < 0", Boolean.FALSE, Boolean.class);
    }
}
