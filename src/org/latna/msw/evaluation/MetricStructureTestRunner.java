package org.latna.msw.evaluation;

import org.latna.msw.*;
import org.latna.msw.euclidian.EuclidianFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: i-mad_000
 * Date: 11.02.16
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class MetricStructureTestRunner {
    public static final int NUMBER_OF_THREADS = 12;
    public static final int TEST_SEQ_SIZE = 30;
    private final EuclidianFactory testQueryFactory;
    private final ArrayList<MetricElement> testQueries;
    private final int minAttempts;
    private final int maxAttempts;
    private final int kClosest;

    public MetricStructureTestRunner(int dimensionality, int minAttempts, int maxAttempts, int kClosest) {
        testQueryFactory = new EuclidianFactory(dimensionality, TEST_SEQ_SIZE);
        testQueries = new ArrayList<MetricElement>();
        this.minAttempts = minAttempts;
        this.maxAttempts = maxAttempts;
        this.kClosest = kClosest;

    }

    public void testStructure(AbstractMetricStructure metricStructure, String outputName) {
        FileWriter fw = null;
        try {
            new File("KleinbergVsMSW_out").mkdir();
            File file = new File(outputName);
            System.out.println(file.getAbsolutePath());
            fw = new FileWriter(file, true);

            Map<MetricElement, TreeSet<EvaluatedElement>> rightResultMap = new HashMap<MetricElement, TreeSet<EvaluatedElement>>();

            System.out.println("The second stage");
            for (MetricElement newQuery : testQueryFactory.getElements()) {
                testQueries.add(newQuery);
                rightResultMap.put(newQuery, TestLib.getKCorrectElements(metricStructure.getElements(), newQuery, kClosest));
            }

            System.out.println("The third stage");
            System.out.println("Elements Array Size: " + metricStructure.getElements().size());

            Random random = new Random();

            for (int a = minAttempts; a <= maxAttempts; a++) {
                ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
                List<Future<TestResult>> searchResultList = new ArrayList<Future<TestResult>>();

                int good = 0;
                long scanned = 0;
                for (int i = 0; i < TEST_SEQ_SIZE; i++) {
                    MetricElement testQ = testQueries.get(random.nextInt(testQueries.size()));

                    Callable testSearcher = new DimensionalityTest.MyCallable(metricStructure, testQ, rightResultMap.get(testQ), a, kClosest);

                    Future<TestResult> submit = executor.submit(testSearcher);
                    searchResultList.add(submit);
                }

                for (Future<TestResult> future : searchResultList) {
                    try {
                        TestResult tr = future.get();
                        good += tr.getRightResutls();
                        scanned += tr.getScannedNumber();

                    } catch (InterruptedException e) {
                        throw new Error(e);
                    } catch (ExecutionException e) {
                        throw new Error(e);
                    }
                }
                executor.shutdown();

                double recall = ((double) good) / ((double) TEST_SEQ_SIZE * kClosest);
                double scannedPercent = ((double) (scanned)) / ((double) metricStructure.getElements().size() * (double) TEST_SEQ_SIZE);
                System.out.print("K = " + kClosest + " Attepts = " + a + "\trecall = " + recall + "\tScanedPercent = " + scannedPercent + "\tAvg Scanned\t" + ((double) scanned / (double) TEST_SEQ_SIZE) + "\n");
                fw.append("K = " + kClosest + " Attepts = " + a + "\trecall = " + recall + "\tScanedPercent = " + scannedPercent + "\tAvg Scanned\t" + ((double) scanned / (double) TEST_SEQ_SIZE) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
