package org.latna.msw.evaluation;

import org.latna.msw.*;
import org.latna.msw.euclidian.GridEuclidianFactory;

import java.util.*;
import java.util.concurrent.*;

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
        Kleinberg kleinberg = buildKleinberg();
        MetrizedSmallWorld metrizedSmallWorld = buildMsw();

        MetricStructureTestRunner testRunner = new MetricStructureTestRunner(dimension, 5, 10, 5);
        testRunner.testStructure(kleinberg, "D:/Kleinberg.txt");
        testRunner.testStructure(metrizedSmallWorld, "D:/MSW.txt");
    }

    private MetrizedSmallWorld buildMsw() {
        MetrizedSmallWorld msw = new MetrizedSmallWorld();
        msw.setInitAttempts(10);
        msw.setNN(5);
        fillStructure(msw);
        return msw;
    }

    private Kleinberg buildKleinberg() {
        Kleinberg kleinbergModel = new Kleinberg();
        kleinbergModel.setProb(probCoeff);
        fillStructure(kleinbergModel);
        kleinbergModel.replaceEdges();
        return kleinbergModel;
    }

    private void fillStructure(AbstractMetricStructure structure) {
        GridEuclidianFactory factory = new GridEuclidianFactory(dimension, size, latticeDistance);
        ExecutorService executorAdder = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        for (MetricElement me : factory.getElements()) {
            executorAdder.submit(new Adding(structure, me));
        }

        shutdownAndAwaitTermination(executorAdder);
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        // System.out.print("shutdownAndAwaitTermination");
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            while (!pool.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                // System.out.println("Waiting for executor termination");
            }
            //pool.shutdown();
            pool.shutdownNow();
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static class Adding extends Thread {
        private AbstractMetricStructure db;
        private MetricElement newElement;

        public Adding(AbstractMetricStructure db, MetricElement newElement) {
            this.db = db;
            this.newElement = newElement;
        }

        public void run() {
            /*
              synchronized (db){
                System.out.println(">"+elementNumber+"<");
              }
              */
            db.add(newElement);
            /*  synchronized (db){
                System.out.println("["+ elementNumber + "]");
              }
              */
        }

    }
}
