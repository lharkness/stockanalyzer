package com.leeharkness.stockanalyzer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MetastockRow {

    public String symbol;
    public LocalDate localDate;
    public double open;
    public double high;
    public double low;
    public double close;
    public double volume;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    

    public MetastockRow() {};
    public MetastockRow(String row) {
        String[] parts = row.split(",");
        this.symbol = parts[0];
        this.localDate = LocalDate.parse(parts[1], formatter);
        this.open = Double.parseDouble(parts[2]);
        this.high = Double.parseDouble(parts[3]);
        this.low = Double.parseDouble(parts[4]);
        this.close = Double.parseDouble(parts[5]);
        this.volume = Double.parseDouble(parts[6]);
    }
}