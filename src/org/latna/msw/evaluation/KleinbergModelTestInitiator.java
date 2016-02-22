package org.latna.msw.evaluation;

/**
 * @author Alexander Bukata
 */
public class KleinbergModelTestInitiator {
    public static void main(String[] args) {

        KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(100, 2, 0, 1);
        test.runTest();
    }
}
