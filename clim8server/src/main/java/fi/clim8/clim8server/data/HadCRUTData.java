package fi.clim8.clim8server.data;

public class HadCRUTData extends AbstractData {

    int month;
    EHadCRUTSummarySeries summarySeries;

    public HadCRUTData(int year, int month, EHadCRUTSummarySeries summarySeries) {
        super(year);
        this.month = month;
        this.summarySeries = summarySeries;
    }


    public int getMonth() {
        return month;
    }

    public EHadCRUTSummarySeries getSummarySeries() {
        return summarySeries;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Month: " + month + ", SummarySeries: " + summarySeries.toString() + ", Data: " + data;
    }
}
