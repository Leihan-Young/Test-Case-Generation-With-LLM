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
package org.apache.commons.jxpath.ri.model;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.IdentityManager;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.xml.DocumentContainer;
/**
 * Abstract superclass for pure XPath 1.0.  Subclasses
 * apply the same XPaths to contexts using different models:
 * DOM, JDOM etc.
 *
 * @author Dmitri Plotnikov
 * @version $Revision$ $Date$
 */
public abstract class XMLModelTestCase extends JXPathTestCase {
    public void testNamespaceMapping() {
        context.registerNamespace("rate", "priceNS");
        context.registerNamespace("goods", "productNS");

        assertEquals("Context node namespace resolution",  "priceNS",  context.getNamespaceURI("price"));        
        
        assertEquals("Registered namespace resolution",  "priceNS",  context.getNamespaceURI("rate"));

        // child:: with a namespace and wildcard
        assertXPathValue(context, 
                "count(vendor/product/rate:*)", 
                new Double(2));

        assertXPathValue(context,
                "vendor[1]/product[1]/rate:amount[1]/@rate:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/rate:amount[1]/@price:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/price:amount[1]/@rate:discount", "10%");
        assertXPathValue(context,
                "vendor[1]/product[1]/price:amount[1]/@price:discount", "10%");

        // Preference for externally registered namespace prefix
        assertXPathValueAndPointer(context,
                "//product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");
        
        // Same, but with a child context        
        JXPathContext childCtx = 
            JXPathContext.newContext(context, context.getContextBean());
        assertXPathValueAndPointer(childCtx,
                "//product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");
        
        // Same, but with a relative context        
        JXPathContext relativeCtx = 
            context.getRelativeContext(context.getPointer("/vendor"));
        assertXPathValueAndPointer(relativeCtx,
                "product/product:name",
                "Box of oranges",
                "/vendor[1]/product[1]/goods:name[1]");
    }
}
