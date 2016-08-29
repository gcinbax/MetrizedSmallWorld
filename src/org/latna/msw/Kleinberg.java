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
    private volatile long edgesAmount = 0;
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

    public void generateLongRangeContacts() {
        elements.parallelStream().forEach(this::addLongRangeContacts);
    }

    private void addLongRangeContacts(MetricElement element) {
        //performance problem, solved by hack. We only read elements from "elements" and dont need amigious synchronization here
        List<MetricElement> metricElements = Collections.unmodifiableList(new ArrayList<>(elements));
        for (int i = 0; i < size; i++) {
            MetricElement randomElement2 = metricElements.get(new Random().nextInt(size));
            if (!element.equals(randomElement2)
                    && !element.getAllFriends().contains(randomElement2)) {
                double curr_prob_threshold = Math.pow(element.calcDistance(randomElement2), -prob);  //Kleinberg coefficient
                if (new Random().nextDouble() < curr_prob_threshold) {
                    element.addFriend(randomElement2);
                    randomElement2.addFriend(element);
                    edgesAmount++;
                }
            }
        }
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
