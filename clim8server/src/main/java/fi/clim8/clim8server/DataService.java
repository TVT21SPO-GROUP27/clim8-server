package fi.clim8.clim8server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import fi.clim8.clim8server.data.EHadCURTSummarySeries;
import fi.clim8.clim8server.data.HadCURTData;

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
        DatabaseService.getInstance().refreshDataFromHadCURT(getHadCURTasBigData());

        Logger.getGlobal().info("Database refreshed, have fun!");
    }

    private List<HadCURTData> getHadCURTasBigData() throws MalformedURLException {
        List<HadCURTData> hadCURTDataList = new ArrayList<>();
        String url = "https://www.metoffice.gov.uk/hadobs/hadcrut5/data/current/analysis/diagnostics/HadCRUT.5.0.1.0.analysis.summary_series.";

        hadCURTDataList.addAll(readCSV(new URL(url + "global.monthly.csv"), EHadCURTSummarySeries.HADCURT_GLOBAL));
        hadCURTDataList.addAll(readCSV(new URL(url + "global.annual.csv"), EHadCURTSummarySeries.HADCURT_GLOBAL));

        hadCURTDataList.addAll(readCSV(new URL(url + "northern_hemisphere.monthly.csv"), EHadCURTSummarySeries.HADCURT_NORTHERN_HEMISPHERE));
        hadCURTDataList.addAll(readCSV(new URL(url + "northern_hemisphere.annual.csv"), EHadCURTSummarySeries.HADCURT_NORTHERN_HEMISPHERE));

        hadCURTDataList.addAll(readCSV(new URL(url + "southern_hemisphere.monthly.csv"), EHadCURTSummarySeries.HADCURT_SOUTHERN_HEMISPHERE));
        hadCURTDataList.addAll(readCSV(new URL(url + "southern_hemisphere.annual.csv"), EHadCURTSummarySeries.HADCURT_SOUTHERN_HEMISPHERE));

        return hadCURTDataList;
    }

    public List<HadCURTData> readCSV(URL url, EHadCURTSummarySeries summarySeries) {
        List<HadCURTData> hadCURTDataList = new ArrayList<>();
        try(Reader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            try(CSVReader csvReader = new CSVReader(reader)) {
                AtomicInteger i = new AtomicInteger(1);
                csvReader.readAll().forEach(array -> {
                    if(i.get() != 1) {
                        String[] parse = array[0].split("-");
                        int year = Integer.parseInt(parse[0]);
                        int month = 0;
                        if(parse.length != 1) month = Integer.parseInt(parse[1]);
                        final HadCURTData hd = new HadCURTData(year, month, summarySeries);
                        hd.setData(Double.parseDouble(array[1]), Double.parseDouble(array[2]), Double.parseDouble(array[3]));
                        hadCURTDataList.add(hd);
                    }
                    i.addAndGet(1);
                });
            }
        } catch (IOException | CsvException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return hadCURTDataList;
    }
}
