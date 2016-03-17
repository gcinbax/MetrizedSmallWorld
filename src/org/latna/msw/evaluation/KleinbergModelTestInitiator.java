package org.latna.msw.evaluation;

/**
 * @author Alexander Bukata
 */
public class KleinbergModelTestInitiator {
    public static void main(String[] args) {
        int k = 1;
        for (int i = 1; i <= 11; i++) {
            k *= 2;
            KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(k * 10000, 2, 2, 1);
            test.runTest();
        }
        for (int i = 3; i <= 5; i++) {
            KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(5000000, i, i, 1);
            test.runTest();
        }
    }
}
