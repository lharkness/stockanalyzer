package com.leeharkness.stockanalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StockDao {

    private final Connection connection;

    public StockDao() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost/stocks", "postgres", "password");
    }

    public void save(Metastock metastock) throws SQLException {
        String insertSql = new StringBuilder().append("INSERT INTO PROD.STOCK VALUES('")
                .append(metastock.getTicker()).append("', '")
                .append(metastock.getDate()).append("',")
                .append(metastock.getOpen()).append(",")
                .append(metastock.getHigh()).append(",")
                .append(metastock.getLow()).append(",")
                .append(metastock.getClose()).append(",")
                .append(metastock.getVolume()).append(")")
                .toString();

        connection.createStatement().execute(insertSql);
    }
}
