package fi.clim8.clim8server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.tomcat.jni.Time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DataService {

    private static final DataService instance = new DataService();

    public static DataService getInstance() {
        return instance;
    }

    public void init() throws MalformedURLException {
        Logger.getGlobal().info("Refreshing database, please wait...");
        DatabaseService.getInstance().init();
        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.global.monthly.csv")), "global");
        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.global.annual.csv")), "global");

        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.northern_hemisphere.monthly.csv")), "northern");
        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.northern_hemisphere.annual.csv")), "northern");

        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.southern_hemisphere.monthly.csv")), "southern");
        DatabaseService.getInstance().refreshDataFromHadCURT(readCSV(new URL("https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.southern_hemisphere.annual.csv")), "southern");

        Logger.getGlobal().info("Database refreshed, have fun!");
    }

    public Map<Integer, HashMap<Integer, Double[]>> readCSV(URL url) {
        HashMap<Integer, HashMap<Integer, Double[]>> data = new HashMap<>();
        try(Reader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            try(CSVReader csvReader = new CSVReader(reader)) {
                AtomicInteger i = new AtomicInteger(1);
                csvReader.readAll().forEach(array -> {
                    if(i.get() != 1) {
                        String[] parse = array[0].split("-");
                        int year = Integer.parseInt(parse[0]);
                        int month = 0;
                        if(parse.length != 1) month = Integer.parseInt(parse[1]);
                        HashMap<Integer, Double[]> hashMap = data.getOrDefault(year, new HashMap<>());
                        hashMap.put(month, new Double[]{Double.parseDouble(array[1]), Double.parseDouble(array[2]), Double.parseDouble(array[3])});
                        data.putIfAbsent(year, hashMap);
                    }
                    i.addAndGet(1);
                });
            }
        } catch (IOException | CsvException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return data;
    }
}
