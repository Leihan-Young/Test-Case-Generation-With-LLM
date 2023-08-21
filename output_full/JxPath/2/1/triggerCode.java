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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import junit.textui.TestRunner;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Function;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.PackageFunctions;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.TestBean;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.NodePointer;
/**
 * Test extension functions.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public class ExtensionFunctionTest extends JXPathTestCase {
    private Functions functions;
    private JXPathContext context;
    private TestBean testBean;
    public void testNodeSetReturn() {
        assertXPathValueIterator(
            context,
            "test:nodeSet()/name",
            list("Name 1", "Name 2"));

        assertXPathValueIterator(
            context,
            "test:nodeSet()",
            list(testBean.getBeans()[0], testBean.getBeans()[1]));

        assertXPathPointerIterator(
            context,
            "test:nodeSet()/name",
            list("/beans[1]/name", "/beans[2]/name"));
            
        assertXPathValueAndPointer(
            context,
            "test:nodeSet()/name",
            "Name 1",
            "/beans[1]/name");        

        assertXPathValueAndPointer(
            context,
            "test:nodeSet()/@name",
            "Name 1",
            "/beans[1]/@name");
    }
}
