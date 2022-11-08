package fi.clim8.clim8server.data;

public class HadCRUTData {
    int year;
    int month;
    EHadCRUTSummarySeries summarySeries;
    double data = 0.0;

    public HadCRUTData(int year, int month, EHadCRUTSummarySeries summarySeries) {
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

    public EHadCRUTSummarySeries getSummarySeries() {
        return summarySeries;
    }

    public double getData() {
        return data;
    }

    public void setData(double degC) {
        data = degC;
    }
}
