package com.leeharkness.stockanalyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 *
 */
public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String baseDir = "/home/developerlyharkness/shared/stockdata";
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(baseDir + "/modeMap.dat"));
        Map<String, Double> modeMap = (Map)ois.readObject();
        ois.close();

        //System.out.println(medianMap);
        
        Set<String> potentials = modeMap.entrySet().stream()
            .filter(e -> e.getValue() > 5)

            .limit(1000)
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .map(e -> e.getKey())
            .collect(Collectors.toSet());

        String[] arr = new String[potentials.size()];
        Map<String, Stock> stocks = YahooFinance.get((String[])potentials.toArray(arr));

        Map<String, Double> symbolToSpread = new HashMap<>();
        for (Map.Entry<String, Stock> entry : stocks.entrySet()) {
            if (entry == null || entry.getValue() == null || 
                entry.getValue().getQuote() == null || 
                entry.getValue().getQuote().getPrice() == null) {
                    System.out.println("ERRROR");
                continue;
            }
            if (entry.getValue().getQuote().getPrice().doubleValue() < 100 && entry.getValue().getQuote().getPrice().doubleValue() < modeMap.get(entry.getKey()))
                symbolToSpread.put(entry.getKey(), modeMap.get(entry.getKey()) - entry.getValue().getQuote().getPrice().doubleValue());
        }
       
        System.out.println(symbolToSpread.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(20)
            .collect(Collectors.toList()));

        // Stock tesla = YahooFinance.get("AMZN", true);
        // System.out.println(tesla.getHistory());
    }
}
