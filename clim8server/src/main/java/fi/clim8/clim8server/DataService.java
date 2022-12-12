package fi.clim8.clim8server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import fi.clim8.clim8server.data.AbstractData;
import fi.clim8.clim8server.data.EHadCRUTSummarySeries;
import fi.clim8.clim8server.data.HadCRUTData;
import fi.clim8.clim8server.data.IceCoreData;
import fi.clim8.clim8server.data.MaunaLoaData;
import fi.clim8.clim8server.data.VostokData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;



public class DataService {

    private static final DataService instance = new DataService();

    public static DataService getInstance() {
        return instance;
    }

    public void init(boolean nofetch) throws MalformedURLException {
        Logger.getGlobal().info("Refreshing database, please wait...");
        DatabaseService.getInstance().init();
        if(!nofetch) {
            DatabaseService.getInstance().refreshDataFromHadCRUT(getHadCRUTasBigData());
            DatabaseService.getInstance().refreshDataFromMoberg2005(fetchMoberg2005(new URL("https://www.ncei.noaa.gov/pub/data/paleo/contributions_by_author/moberg2005/nhtemp-moberg2005.txt")));
            DatabaseService.getInstance().refreshDataFromMaunaLoa(fetchMaunaLoaAnnual(new URL("https://gml.noaa.gov/webdata/ccgg/trends/co2/co2_annmean_mlo.txt")));
            DatabaseService.getInstance().refreshDataFromMaunaLoa(fetchMaunaLoaMonthly(new URL("https://gml.noaa.gov/webdata/ccgg/trends/co2/co2_mm_mlo.txt")));
            DatabaseService.getInstance().refreshDataFromIceCore(fetchIceCore());
            DatabaseService.getInstance().refreshDataFromVostokCore(fetchVostokCore(new URL("https://cdiac.ess-dive.lbl.gov/ftp/trends/co2/vostok.icecore.co2")));
        }
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
                        hd.setData(Double.parseDouble(array[2]));
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

    public List<AbstractData> fetchMoberg2005(URL url) {
        List<AbstractData> mobergDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(93);

            lines.forEach(line -> {
                String[] data = line.split(" ");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                AbstractData adata = new AbstractData(Integer.parseInt(list.get(0)));
                adata.setData(Double.parseDouble(list.get(1)));
                mobergDataList.add(adata);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return mobergDataList;
    }

    public List<MaunaLoaData> fetchMaunaLoaAnnual(URL url) {
        List<MaunaLoaData> maunaLoaDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(57);

            lines.forEach(line -> {
                String[] data = line.split(" ");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                MaunaLoaData tempData = new MaunaLoaData(Integer.parseInt(list.get(0)));
                tempData.setData(Double.parseDouble(list.get(1)));
                maunaLoaDataList.add(tempData);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return maunaLoaDataList;
    }

    public List<MaunaLoaData> fetchMaunaLoaMonthly(URL url) {
        List<MaunaLoaData> maunaLoaDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(54);

            lines.forEach(line -> {
                String[] data = line.split(" ");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                MaunaLoaData tempData = new MaunaLoaData(Integer.parseInt(list.get(0)),(Integer.parseInt(list.get(1))));
                tempData.setData(Double.parseDouble(list.get(4)));
                maunaLoaDataList.add(tempData);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return maunaLoaDataList;
    }

    public List<IceCoreData> fetchIceCore() {
        List<IceCoreData> iceCoreDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("clim8server\\src\\main\\java\\fi\\clim8\\clim8server\\data\\IceCoreData.txt"))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(4);
            
            lines.forEach(line -> {
                String[] data = line.split(" ");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                IceCoreData tempData = new IceCoreData(Integer.parseInt(list.get(5)));
                tempData.setSeries((list.get(0)));
                tempData.setData(Double.parseDouble(list.get(6)));
                iceCoreDataList.add(tempData);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return iceCoreDataList;
    }

    public List<VostokData> fetchVostokCore(URL url) {
        List<VostokData> vostokDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(21);

            lines.forEach(line -> {
                String[] data = line.split(" ");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                VostokData vostokdata = new VostokData(Double.parseDouble(list.get(0)));
                vostokdata.setYear(Integer.parseInt(list.get(3)));
                vostokdata.setData(Double.parseDouble(list.get(4)));
                vostokDataList.add(vostokdata);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return vostokDataList;
    }

}
