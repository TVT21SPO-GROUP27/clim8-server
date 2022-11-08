package fi.clim8.clim8server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import fi.clim8.clim8server.data.EHadCRUTSummarySeries;
import fi.clim8.clim8server.data.HadCRUTData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        DatabaseService.getInstance().refreshDataFromHadCRUT(getHadCRUTasBigData());

        Logger.getGlobal().info("Database refreshed, have fun!");
    }

    private List<HadCRUTData> getHadCRUTasBigData() throws MalformedURLException {
        List<HadCRUTData> hadCRUTDataList = new ArrayList<>();
        String url = "https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.";

        hadCRUTDataList.addAll(readCSV(new URL(url + "global.monthly.csv"), EHadCRUTSummarySeries.HADCRUT_GLOBAL));
        hadCRUTDataList.addAll(readCSV(new URL(url + "global.annual.csv"), EHadCRUTSummarySeries.HADCRUT_GLOBAL));

        hadCRUTDataList.addAll(readCSV(new URL(url + "northern_hemisphere.monthly.csv"), EHadCRUTSummarySeries.HADCRUT_NORTHERN_HEMISPHERE));
        hadCRUTDataList.addAll(readCSV(new URL(url + "northern_hemisphere.annual.csv"), EHadCRUTSummarySeries.HADCRUT_NORTHERN_HEMISPHERE));

        hadCRUTDataList.addAll(readCSV(new URL(url + "southern_hemisphere.monthly.csv"), EHadCRUTSummarySeries.HADCRUT_SOUTHERN_HEMISPHERE));
        hadCRUTDataList.addAll(readCSV(new URL(url + "southern_hemisphere.annual.csv"), EHadCRUTSummarySeries.HADCRUT_SOUTHERN_HEMISPHERE));

        return hadCRUTDataList;
    }

    /**
     * Fetches HadCRUT data.
     *
     * @param url           where the data is stored
     * @param summarySeries which hemisphere this data is from
     * @return              very spicy list full of HadCRUTData objects
     */
    public List<HadCRUTData> readCSV(URL url, EHadCRUTSummarySeries summarySeries) {
        List<HadCRUTData> hadCRUTDataList = new ArrayList<>();
        try(Reader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            try(CSVReader csvReader = new CSVReader(reader)) {
                AtomicInteger i = new AtomicInteger(1);
                csvReader.readAll().forEach(array -> {
                    if(i.get() != 1) {
                        String[] parse = array[0].split("-");
                        int year = Integer.parseInt(parse[0]);
                        int month = 0;
                        if(parse.length != 1) month = Integer.parseInt(parse[1]);
                        final HadCRUTData hd = new HadCRUTData(year, month, summarySeries);
                        hd.setData(Double.parseDouble(array[1]));
                        hadCRUTDataList.add(hd);
                    }
                    i.addAndGet(1);
                });
            }
        } catch (IOException | CsvException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return hadCRUTDataList;
    }
}
