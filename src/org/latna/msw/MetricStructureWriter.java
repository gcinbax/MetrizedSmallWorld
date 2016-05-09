package org.latna.msw;

import org.latna.msw.euclidian.Euclidean;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by i-mad_000 on 19.03.2016.
 */
public class MetricStructureWriter {
    public static void writeStructure(AbstractMetricStructure graph, String output) {
        FileWriter fileWriter = null;
        List<MetricElement> visited = new ArrayList<>();
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
                                && !visited.contains(element2)) {
                            fileWriter.write(euclElement1.toString() + " " + euclElement2.toString() + "\n");
                        }
                    }
                }
                visited.add(element1);
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

    public static void writeDegreeDestribution(AbstractMetricStructure metricStructure, String output) {
        Map<Integer, Integer> degrees = new TreeMap<Integer, Integer>();
        for (MetricElement me : metricStructure.getElements()) {
            int degree = me.getAllFriends().size();
            if (degrees.containsKey(degree)) {
                degrees.put(degree, degrees.get(degree) + 1);
            } else
                degrees.put(degree, 1);
        }

        FileWriter outp;
        try {
            outp = new FileWriter(output);
            for (Map.Entry elem : degrees.entrySet()) {
                outp.write(elem.getValue().toString() + " " + elem.getKey().toString() + "\n");
            }
            outp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
