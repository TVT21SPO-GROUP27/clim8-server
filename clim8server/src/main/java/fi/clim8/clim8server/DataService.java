package fi.clim8.clim8server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import fi.clim8.clim8server.data.*;
import fi.clim8.clim8server.data.enums.EHadCRUTSummarySeries;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            DatabaseService.getInstance().refreshDataFromVostokCore(fetchVostok(new URL("https://cdiac.ess-dive.lbl.gov/ftp/trends/co2/vostok.icecore.co2")));
            DatabaseService.getInstance().refreshDataFromACoreRevised(fetchACore(new URL("https://www.ncei.noaa.gov/pub/data/paleo/icecore/antarctica/antarctica2015co2composite.txt")));

            Logger.getGlobal().info("Preparing for V8 fetching!");
            // V8
            DatabaseService.getInstance().refreshDataFromNationalCarbonEmissions(fetchNationalCarbonEmissions(Paths.get("../../data/National_Carbon_Emissions_2021v1.0.xlsx")));
        }
        Logger.getGlobal().info("Database refreshed, have fun!");
    }

    private List<HadCRUTData> getHadCRUTasBigData() throws MalformedURLException {
        Logger.getGlobal().info("Running HadCRUT fetch process!");
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
        Logger.getGlobal().info("Running Moberg2005 fetch process!");
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
        Logger.getGlobal().info("Running ManuaLoaAnnual fetch process!");
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
        Logger.getGlobal().info("Running ManuaLoaMonthly fetch process!");
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
        Logger.getGlobal().info("Running IceCore fetch process!");
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
        public List<VostokData> fetchVostok(URL url) {
        Logger.getGlobal().info("Running Vostok fetch process!");
        List<VostokData> vostokDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(21);
            
            lines.forEach(line -> {
                String[] data = line.split("\\t");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                VostokData tempData = new VostokData(Integer.parseInt(list.get(2)));
                tempData.setData(Double.parseDouble(list.get(3)));
                vostokDataList.add(tempData);
            });
        } catch (IOException e) {
            Logger.getGlobal().info(e.getMessage());
        }
        return vostokDataList;
    }
    

    public List<ACoreRevised> fetchACore(URL url) {
        Logger.getGlobal().info("Running ACore fetch process!");
        List<ACoreRevised> acoreDataList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            //Skip unnecessary lines
            Stream<String> lines = reader.lines().skip(138);

            lines.forEach(line -> {
                String[] data = line.split("\\t");
                List<String> list = Arrays.stream(data).filter(string -> !string.isEmpty()).toList();
                if(!list.isEmpty()) {
                    // BP to CE
                    double bp = Double.parseDouble(list.get(0));
                    int ce = (int) Math.round(-bp + 1950);
                    final ACoreRevised acoredata = new ACoreRevised(ce);
                    acoredata.setData(Double.parseDouble(list.get(2)));
                    acoreDataList.add(acoredata);
                }
            });
        } catch (IOException e) {
            Logger.getGlobal().info("Error: " + e.getMessage());
        }
        return acoreDataList;
    }

    public List<NationalCarbonData> fetchNationalCarbonEmissions(Path path) {
        Logger.getGlobal().info("Running NationalCarbonEmissions fetch process!");
        List<NationalCarbonData> list = new ArrayList<>();
        try(Workbook wb = WorkbookFactory.create(path.toFile())) {
            Sheet sheet = wb.getSheetAt(1);

            AtomicInteger current = new AtomicInteger(2);
            sheet.getRow(11).cellIterator().forEachRemaining(cell -> {
                for(int i = 12; i < 74; i++) {
                    final Cell dataCell = sheet.getRow(i).getCell(current.get(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    // Skip data cells that doesn't contain anything.
                    if(dataCell == null) continue;
                    final NationalCarbonData data = new NationalCarbonData(1947+i, cell.getStringCellValue());
                    data.setData(dataCell.getNumericCellValue());
                    list.add(data);
                }
                current.getAndIncrement();
            });
        } catch (Exception ignore) {}
        return list;
    }
}
