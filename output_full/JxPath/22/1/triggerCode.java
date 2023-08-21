package org.apache.commons.jxpath.ri.model;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathTestCase;
import org.apache.commons.jxpath.xml.DocumentContainer;
public class JXPath154Test extends JXPathTestCase {
    public void testInnerEmptyNamespaceDOM() {
        doTest("b:foo/test", DocumentContainer.MODEL_DOM, "/b:foo[1]/test[1]");
    }
}
