package org.latna.msw.evaluation;

import org.latna.msw.*;
import org.latna.msw.euclidian.GridEuclidianFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: i-mad_000
 * Date: 07.02.16
 * Time: 17:31
 */
public class KleinbergVsMSWModelTest {
    public static final int NUMBER_OF_THREADS = 12;
    private final int size;
    private final int dimension;
    private final int probCoeff;
    private final int latticeDistance;


    public KleinbergVsMSWModelTest(int size, int dimension, int probCoeff, int latticeDistance) {
        this.size = size;
        this.dimension = dimension;
        this.probCoeff = probCoeff;
        this.latticeDistance = latticeDistance;
    }

    public void runTest() {
        MetrizedSmallWorld metrizedSmallWorld = buildMsw();
        Kleinberg kleinberg = buildKleinberg(metrizedSmallWorld.getEdgesAmount());

        MetricStructureTestRunner testRunner = new MetricStructureTestRunner(dimension, 5, 10, 5);
        //MetricStructureWriter.writeStructure(kleinberg, "KleinbergGraph.dimen=" + dimension + ".size=" + size + ".txt");
        //MetricStructureWriter.writeStructure(metrizedSmallWorld, "MSWGraph.dimen=" + dimension + ".size=" + size + ".txt");
        testRunner.testStructureParallel(kleinberg, "KleinbergVsMSW_out/Kleinberg.dimen=" + dimension + ".size=" + size + ".txt");
        testRunner.testStructureParallel(metrizedSmallWorld, "KleinbergVsMSW_out/MSW.dimen=" + dimension + ".size=" + size + ".txt");
        MetricStructureWriter.writeDegreeDestribution(kleinberg, "KleinbergVsMSW_out/Kleinberg.dimen=" + dimension + ".size=" + size + "_degree_distrib.txt");
        MetricStructureWriter.writeDegreeDestribution(metrizedSmallWorld, "KleinbergVsMSW_out/MSW.dimen=" + dimension + ".size=" + size + "_degree_distrib.txt");
    }

    private MetrizedSmallWorld buildMsw() {
        MetrizedSmallWorld msw = new MetrizedSmallWorld();
        msw.setInitAttempts(10);
        msw.setNN(5);

        fillStructure(msw);

        //msw.addGridEdges(1);
        return msw;
    }

    private Kleinberg buildKleinberg(long edgesAmount) {
        Kleinberg kleinbergModel = new Kleinberg(edgesAmount);
        kleinbergModel.setSize(size);
        kleinbergModel.setProb(probCoeff);
        fillStructure(kleinbergModel);
        kleinbergModel.checkEdgesCorrectness();
        kleinbergModel.generateLongRangeContacts();

        return kleinbergModel;
    }

    private void fillStructure(AbstractMetricStructure structure) {
        GridEuclidianFactory factory = new GridEuclidianFactory(dimension, size, latticeDistance);
        factory.getElements().parallelStream().forEach(structure::add);
    }
}
