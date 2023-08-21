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
    public void testAxisAttribute() {
        // attribute::
        assertXPathValue(context, "vendor/location/@id", "100");

        // attribute:: produces the correct pointer
        assertXPathPointer(
            context,
            "vendor/location/@id",
            "/vendor[1]/location[1]/@id");

        // iterate over attributes
        assertXPathValueIterator(
            context,
            "vendor/location/@id",
            list("100", "101"));

        // Using different prefixes for the same namespace
        assertXPathValue(
            context,
            "vendor/product/price:amount/@price:discount",
            "10%");
        
        // namespace uri for an attribute
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount/@price:discount)",
            "priceNS");

        // local name of an attribute
        assertXPathValue(
            context,
            "local-name(vendor/product/price:amount/@price:discount)",
            "discount");

        // name for an attribute
        assertXPathValue(
            context,
            "name(vendor/product/price:amount/@price:discount)",
            "price:discount");

        // attribute:: with the default namespace
        assertXPathValue(
            context,
            "vendor/product/price:amount/@discount",
            "20%");

        // namespace uri of an attribute with the default namespace
        assertXPathValue(
            context,
            "namespace-uri(vendor/product/price:amount/@discount)",
            "");

        // local name of an attribute with the default namespace
        assertXPathValue(
            context,
            "local-name(vendor/product/price:amount/@discount)",
            "discount");

        // name of an attribute with the default namespace
        assertXPathValue(
            context,
            "name(vendor/product/price:amount/@discount)",
            "discount");

        // attribute:: with a namespace and wildcard
        assertXPathValueIterator(
            context,
            "vendor/product/price:amount/@price:*",
            list("10%"));

        // attribute:: with a wildcard
        assertXPathValueIterator(
            context,
            "vendor/location[1]/@*",
            set("100", "", "local"));

        // attribute:: with default namespace and wildcard
        assertXPathValueIterator(
                context,
                "vendor/product/price:amount/@*",
                //use a set because DOM returns attrs sorted by name, JDOM by occurrence order:
                set("10%", "20%"));

        // attribute:: select non-ns'd attributes only
        assertXPathValueIterator(
            context,
            "vendor/product/price:amount/@*[namespace-uri() = '']",
            list("20%"));

        // Empty attribute
        assertXPathValue(context, "vendor/location/@manager", "");

        // Missing attribute
        assertXPathValueLenient(context, "vendor/location/@missing", null);

        // Missing attribute with namespace
        assertXPathValueLenient(context, "vendor/location/@miss:missing", null);

        // Using attribute in a predicate
        assertXPathValue(
            context,
            "vendor/location[@id='101']//street",
            "Tangerine Drive");
        
        assertXPathValueIterator(
            context,
            "/vendor/location[1]/@*[name()!= 'manager']", list("100",
            "local"));
    }
}
