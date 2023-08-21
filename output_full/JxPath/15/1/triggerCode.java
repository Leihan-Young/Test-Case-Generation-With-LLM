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
    public void testUnion() {
        assertXPathValue(context, "/vendor[1]/contact[1] | /vendor[1]/contact[4]", "John");
        assertXPathValue(context, "/vendor[1]/contact[4] | /vendor[1]/contact[1]", "John");
    }
}
