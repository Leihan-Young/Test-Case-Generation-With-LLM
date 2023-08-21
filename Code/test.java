public void test04()  throws Throwable  {
    Arc2D.Double arc2D_Double0 = new Arc2D.Double();
    // Undeclared exception!
    try { 
    ShapeUtilities.drawRotatedShape((Graphics2D) null, arc2D_Double0, 1.0, 1956.217F, 0.0F);
    fail("Expecting exception: NullPointerException");
    } catch(NullPointerException e) {
        //
        // no message in exception (getMessage() returned null)
        //
        verifyException("org.jfree.chart.util.ShapeUtilities", e);
    }
}