package fi.clim8.clim8server.data;

public enum EHadCRUTSummarySeries {
    HADCRUT_GLOBAL("global"),
    HADCRUT_NORTHERN_HEMISPHERE("northern"),
    HADCRUT_SOUTHERN_HEMISPHERE("southern");

    private final String summarySeries;

    EHadCRUTSummarySeries(String summarySeries) {
        this.summarySeries = summarySeries;
    }

    public String getSummarySeries() {
        return summarySeries;
    }
}
