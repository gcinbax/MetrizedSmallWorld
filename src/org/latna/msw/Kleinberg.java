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
    private int size = 0; //sqrt(size)*sqrt(size) grid
    private int latticeDistance = 1; // distance between elements of grid
    private int prob = 500; // coefficient of sparseness of grid. With prob = 0 we will get graph of Watts and Strogatz

    public Kleinberg() {
        super();
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
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        elements.add(newElement);
    }

    public void replaceEdges() {
        ArrayList<MetricElement> shuffledElements = new ArrayList<MetricElement>(elements);
        for (MetricElement element_1 : elements) {
            for (MetricElement friend_of_element_1 : new HashSet<MetricElement>(element_1.getAllFriends())) {  // take each edge and try to move
                Collections.shuffle(shuffledElements);
                for (MetricElement element_2 : shuffledElements) {
                    double curr_prob_threshold = Math.pow(element_1.calcDistance(element_2), -prob);  //Kleinberg coefficient
                    if (!element_1.equals(element_2)
                            && !element_1.getAllFriends().contains(element_2)
                            && new Random().nextDouble() < curr_prob_threshold) {
                        element_1.removeFriend(friend_of_element_1);
                        friend_of_element_1.removeFriend(element_1);
                        element_1.addFriend(element_2);
                        element_2.addFriend(element_1);
                        break;
                    }
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
