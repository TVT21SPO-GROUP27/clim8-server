package fi.clim8.clim8server.data;

public class HadCURTData {
    int year;
    int month;
    EHadCURTSummarySeries summarySeries;
    double[] data = {0.0,0.0,0.0};

    public HadCURTData(int year, int month, EHadCURTSummarySeries summarySeries) {
        this.year = year;
        this.month = month;
        this.summarySeries = summarySeries;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public EHadCURTSummarySeries getSummarySeries() {
        return summarySeries;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double degC, double lowConfidenceLimit, double upperConfidenceLimit) {
        data[0] = degC;
        data[1] = lowConfidenceLimit;
        data[2] = upperConfidenceLimit;
    }
}
