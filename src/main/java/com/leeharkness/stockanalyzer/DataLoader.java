package com.leeharkness.stockanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.DoubleConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataLoader {

    private static Map<String, Double> symbolToStandardDeviation = new HashMap<>();
    private static Map<String, Double> symbolToMedian = new HashMap<>();
    private static Map<String, List<Double>> symbolToClosing = new HashMap<>();
    private static Map<String, Double> symbolToMode = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String baseDir = "/home/developerlyharkness/shared/stockdata";
        File dir = new File(baseDir);
        String[] files = dir.list();
        for (String file : files) {
            if (!file.endsWith("zip")) {
                continue;
            }
            try (ZipFile zf = new ZipFile(baseDir + "/" + file)) {

                Enumeration<? extends ZipEntry> entries = zf.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = entries.nextElement();
                    BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        MetastockRow row = new MetastockRow(line);

                        if (symbolToClosing.get(row.symbol) == null) {
                            List<Double> newList = new ArrayList<>();
                            symbolToClosing.put(row.symbol, newList);
                        }
                        symbolToClosing.get(row.symbol).add(row.close);

                    }
                    br.close();
                }
            }
        }
        for (Map.Entry<String, List<Double>> e : symbolToClosing.entrySet()) {
            symbolToStandardDeviation.put(e.getKey(), getStandardDeviation(e.getValue()));
            symbolToMedian.put(e.getKey(), getMedianOf(e.getValue()));
            symbolToMode.put(e.getKey(), getModifiedModeOf(e.getKey(), e.getValue()));
        }

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(baseDir + "/standardDeviationMap.dat"));
        oos.writeObject(symbolToStandardDeviation);
        oos.close();
        oos = new ObjectOutputStream(new FileOutputStream(baseDir + "/medianMap.dat"));
        oos.writeObject(symbolToMedian);
        oos.close();
        oos = new ObjectOutputStream(new FileOutputStream(baseDir + "/modeMap.dat"));
        oos.writeObject(symbolToMode);
        oos.close();
    }

    private static double getStandardDeviation(List<Double> list) {
        // Calculate the standard deviation
        StatCollector statCollection = list.stream().collect(StatCollector::new, StatCollector::accept,
                StatCollector::combine);
        double sum = statCollection.getSum();
        double sumOfSquares = statCollection.getSumOfSquares();
        double intermediate = sumOfSquares - sum * sum / list.size();

        return Math.sqrt(intermediate / (list.size() - 1));
    }

    private static double getMedianOf(List<Double> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        if (list.size() == 2) {
            return (list.get(0) + list.get(1))/2;
        }
        Collections.sort(list);
        if (list.size() % 2 != 0) {
            int middle = (list.size() / 2) + 1;
            return list.get(middle);
        }
        double lower = list.get(list.size() / 2);
        double upper = list.get((list.size() / 2) + 1);

        return (lower + upper) / 2;
    }

    private static double getModifiedModeOf(String name, List<Double> list) {
        // Get the base mode

        Map<Double, Integer> modes = new HashMap<>();
        for (double d : list) {
            if (!modes.containsKey(d)) {
                modes.put(d, 0);
            }
            modes.put(d, modes.get(d) + 1);
        }

        double baseMode = modes.entrySet().stream().sorted(Map.Entry.<Double, Integer>comparingByValue().reversed())
                .findFirst().get().getKey();

        // throw out everything more than 3 standard deviations from this mode and
        // record the new mode
        double sd = symbolToStandardDeviation.get(name);
        List<Double> values = new ArrayList<>();
        for (double d : list) {
            if (Math.abs(d - baseMode) <= 3 * sd) {
                values.add(d);
            }
        }

        modes = new HashMap<>();
        for (double d : values) {
            if (!modes.containsKey(d)) {
                modes.put(d, 0);
            }
            modes.put(d, modes.get(d) + 1);
        }

        Optional<Entry<Double, Integer>> optionalMode = modes.entrySet()
                .stream()
            .sorted(Map.Entry.<Double, Integer>comparingByValue().reversed())
            .findFirst();

        if (optionalMode.isPresent()) {
            return optionalMode.get().getKey();
        }
        
        return 0.0;
    }

    private static final class StatCollector implements DoubleConsumer {

        private double sum;
        private double sumOfSquares;
    
        @Override
        public void accept(double t) {
            sum += t;
            sumOfSquares += t * t;
        }
    
        public void combine(StatCollector other) {
            sum += other.sum;
            sumOfSquares += other.sumOfSquares;
        }
    
        double getSum() {
            return sum;
        }
    
        double getSumOfSquares() {
            return sumOfSquares;
        }
    }
}