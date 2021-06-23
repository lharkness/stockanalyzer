package com.leeharkness.stockanalyzer;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LoadApp {

    // TODO: Let's use findResource (or whatever it's called)
    public static final String DATA_DIRECTORY = "/path/to/data/files";

    private final StockDao stockDao;

    public LoadApp() throws SQLException {
        stockDao = new StockDao();
    }

    public static void main( String[] args ) throws IOException, SQLException {
        LoadApp loadApp = new LoadApp();
        loadApp.run();
    }

    private void run() throws IOException {
        System.out.println("Loading data");
        File dataDirectory = new File(DATA_DIRECTORY);
        if (!dataDirectory.isDirectory()) {
            System.out.println("Data Directory: " + DATA_DIRECTORY + " is not a directory");
            System.exit(-1);
        }
        // Assume this thing is full of only zip files, no need to recurse into subdirectories
        for (File file : Objects.requireNonNull(dataDirectory.listFiles(pathname -> pathname.getPath().endsWith(".zip")))) {
            ZipFile zipFile = new ZipFile(file);
            System.out.println("Processing: " + zipFile.getName());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                InputStream stream = zipFile.getInputStream(entry);
                InputStreamReader isr = new InputStreamReader((stream));
                BufferedReader br = new BufferedReader(isr);
                String line = br.readLine();
                while (line != null) {
                    processLine(line);
                    line = br.readLine();
                }
                br.close();
            }
            zipFile.close();
        }
    }

    private void processLine(String line) {
        String[] parts = line.split(",");
        String ticker = parts[0];
        String date = parts[1];
        String open = parts[2];
        String high = parts[3];
        String low = parts[4];
        String close = parts[5];
        String volume = parts[6];

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6));

        try {
            Metastock metastock = Metastock.builder()
                    .ticker(ticker)
                    .date(LocalDate.of(year, month, day))
                    .open(Double.parseDouble(open))
                    .high(Double.parseDouble(high))
                    .low(Double.parseDouble(low))
                    .close(Double.parseDouble(close))
                    .volume(Double.parseDouble(volume))
                    .build();

            stockDao.save(metastock);

        }
        catch (NumberFormatException | SQLException nfe) {
            System.out.println("ERROR: " + line);
            nfe.printStackTrace();
            System.exit(-1);
        }
    }
}
