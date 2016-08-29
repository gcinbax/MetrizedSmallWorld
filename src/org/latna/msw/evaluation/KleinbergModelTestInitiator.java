package org.latna.msw.evaluation;

/**
 * @author Alexander Bukata
 */
public class KleinbergModelTestInitiator {
    public static void main(String[] args) {
//        KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(5000, 2, 2, 1);
//        test.runTest();
        int k = 1;
        for (int i = 1; i <= 6; i++) {
            k *= 2;
            KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(k * 10000, 2, 2, 1);
            test.runTest();
            test = new KleinbergVsMSWModelTest((k + k / 2) * 10000, 2, 2, 1);
            test.runTest();
        }
//        int k = 1;
//        for (int i = 1; i <= 9; i++) {
//            k *= 2;
//            KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(k * 10000, 15, 15, 1);
//            test.runTest();
//            test = new KleinbergVsMSWModelTest((k + k / 2) * 10000, 15, 15, 1);
//            test.runTest();
//        }
//        for (int i = 1; i <= 50; i++) {
//            if (i > 10) {
//                i += 4;
//            }
//            KleinbergVsMSWModelTest test = new KleinbergVsMSWModelTest(60000, i, i, 1);
//            test.runTest();
//        }
    }
}
