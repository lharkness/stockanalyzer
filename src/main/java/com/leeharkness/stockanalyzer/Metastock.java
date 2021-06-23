package com.leeharkness.stockanalyzer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Metastock implements Serializable {
    @EqualsAndHashCode.Include
    private String ticker;

    private LocalDate date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private double openInterest;
}
