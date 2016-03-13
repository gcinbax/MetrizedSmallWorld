package org.latna.msw;

import sun.reflect.generics.tree.ArrayTypeSignature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * One of the simplest implementation of the Metrized Small World Data Structure.
 * Based on Kleinberg model.
 *
 * @author Alexander Bukata gcinbax@gmail.com
 */
public class Kleinberg extends AbstractMetricStructure {
    private long edgesAmount = 0;
    private final long edgesAmountConstraint;
    private int size = 0; //sqrt(size)*sqrt(size) grid
    private int latticeDistance = 1; // distance between elements of grid
    private int prob = 500; // coefficient of sparseness of grid. With prob = 0 we will get graph of Watts and Strogatz

    public Kleinberg() {
        super();
        edgesAmountConstraint = Long.MAX_VALUE;
    }

    public Kleinberg(long edgesAmountConstraint) {
        super();
        this.edgesAmountConstraint = edgesAmountConstraint;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLatticeDistance() {
        return latticeDistance;
    }

    public void setLatticeDistance(int latticeDistance) {
        this.latticeDistance = latticeDistance;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public long getEdgesAmount() {
        return edgesAmount;
    }

    @Override
    public void add(MetricElement newElement) {
        try {
            MetricElement enterPoint = this.getProvider().getRandomEnterPoint();

            //check if newElement is the first element, if true then return
            if ((enterPoint == null)) {
                elements.add(newElement);
                incSize();
                return;
            }

            newElement.removeAllFriends();
            synchronized (elements) {
                for (MetricElement element : elements) {
                    if (element.calcDistance(newElement) <= latticeDistance) { // or just equals
                        element.addFriend(newElement);
                        newElement.addFriend(element);
                        edgesAmount++;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        elements.add(newElement);
    }

    public void replaceEdges() {
        checkEdgesCorrectness();
        do {
            ArrayList<MetricElement> shuffledElements = new ArrayList<MetricElement>(elements);
            for (MetricElement element_1 : elements) {
                Collections.shuffle(shuffledElements);
                for (MetricElement element_2 : shuffledElements) {
                    double curr_prob_threshold = Math.pow(element_1.calcDistance(element_2), -prob);  //Kleinberg coefficient
                    if (!element_1.equals(element_2)
                            && !element_1.getAllFriends().contains(element_2)
                            && new Random().nextDouble() < curr_prob_threshold
                            && edgesAmount < edgesAmountConstraint) {
                        element_1.addFriend(element_2);
                        element_2.addFriend(element_1);
                        edgesAmount++;
                        //break;
                    }
                }
            }
            System.out.println(edgesAmount);
        } while (edgesAmount < edgesAmountConstraint || edgesAmountConstraint == Long.MAX_VALUE);
    }

    public void checkEdgesCorrectness() {
        for (MetricElement element1 : elements) {
            for (MetricElement element2 : elements) {
                if (element1.calcDistance(element2) <= latticeDistance
                        && !element1.getAllFriends().contains(element2)
                        && !element1.equals(element2)) {
                    element1.addFriend(element2);
                    element2.addFriend(element1);
                    edgesAmount++;
                }
            }
        }
    }

    @Override
    public SearchResult nnSearch(MetricElement query, int attempts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SearchResult knnSearch(MetricElement query, int k, int attempts) {
        return AlgorithmLib.kSearchElementsWithAttempts(query, getProvider(), k, attempts);
    }
}
