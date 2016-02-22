package org.latna.msw.euclidian;

import cern.jet.random.Normal;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;
import org.latna.msw.MetricElement;
import org.latna.msw.MetricElementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GridEuclidianFactory implements MetricElementFactory {
    private int dimension;
    private int n; //number of elements
    private List<MetricElement> allElements;
    private static Random random = new Random();

    public List<MetricElement> getElements() {
        return allElements;
    }

    public void setParameterString(String param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*

    * Create factory and generate list with n uniformaly distributed elements
     * @param dimension
     * @param n - number of elements
     * @param seed - random seed
     */
    public GridEuclidianFactory(int dimension, int n, int latticeDistance) {

        this.dimension = dimension;
        this.n = n;
        //Random random = new Random();

        allElements = new ArrayList<MetricElement>();

        long[] coords = new long[dimension];
        Arrays.fill(coords, 0);
        long i = 0;
        while (i < n) {
            double[] x = new double[dimension];
            for (int k = 0; k < dimension; k++) {
                x[k] = coords[k];
                if (k == dimension - 1) {
                    incCoordsRestr(coords, latticeDistance, dimension, (int) Math.pow(n, (float) 1 / (float) dimension));
                }
            }
            allElements.add(new Euclidean(x));
            i++;
        }
    }

    /**
     * @param coord           reflect a node's coords
     * @param latticeDistance distance between nodes in grid
     * @param dimension       a number of coords of one node
     * @param n               a number of nodes in one row
     */
    private void incCoordsRestr(long[] coord, double latticeDistance, int dimension, int n) {
        coord[dimension - 1] += latticeDistance;
        int i = dimension - 1;
        while (coord[i] >= n * latticeDistance && i > 0) {
            coord[i] = 0;
            coord[i - 1] += latticeDistance;
            i--;
        }
    }

    public static MetricElement getRandomElement(int dimension) {
        double x[] = new double[dimension];
        for (int j = 0; j < dimension; j++) {
            x[j] = random.nextDouble();
        }
        return new Euclidean(x);
    }

    public GridEuclidianFactory(int dimension, int n, double standardDeviation) {
        this.dimension = dimension;
        this.n = n;


        RandomEngine engine = new DRand();
        Normal normal = new Normal(0, standardDeviation, engine);

        allElements = new ArrayList(n);

        for (int i = 0; i < n; i++) {
            double x[] = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                x[j] = normal.nextDouble();
            }

            allElements.add(new Euclidean(x));
        }

    }


    public int getDimension() {
        return dimension;
    }
}
