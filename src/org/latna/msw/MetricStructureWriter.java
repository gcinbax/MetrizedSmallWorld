package org.latna.msw;

import org.latna.msw.euclidian.Euclidean;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by i-mad_000 on 19.03.2016.
 */
public class MetricStructureWriter {
    public static void writeStructure(AbstractMetricStructure graph, String output) {
        FileWriter fileWriter = null;
        Map<MetricElement, Integer> metricElementIntegerMap = buildMapNumbers(graph.elements);
        try {
            fileWriter = new FileWriter(output);
            List<MetricElement> elements = graph.elements;
            for (MetricElement element1 : elements) {
                for (MetricElement element2 : elements) {
                    if (element1 instanceof Euclidean && element2 instanceof Euclidean) {
                        Euclidean euclElement1 = (Euclidean) element1;
                        Euclidean euclElement2 = (Euclidean) element2;
                        if (element1.getAllFriends().contains(element2)
                                && element2.getAllFriends().contains(element1)
                                && euclElement1.x[0] >= euclElement2.x[0]) {
                            /*Integer firstElemNumber = metricElementIntegerMap.get(element1);
                            Integer secondElemNumber = metricElementIntegerMap.get(element2);
                            fileWriter.write(firstElemNumber + " " + secondElemNumber + "\n");*/
                            fileWriter.write(euclElement1.toString() + " " + euclElement2.toString() + "\n");
                        }
                    }
                }
            }
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<MetricElement, Integer> buildMapNumbers(Collection<MetricElement> elements) {
        int i = 1;
        HashMap<MetricElement, Integer> map = new HashMap<>();
        for (MetricElement element : elements) {
            map.put(element, i++);
        }
        return map;
    }
}