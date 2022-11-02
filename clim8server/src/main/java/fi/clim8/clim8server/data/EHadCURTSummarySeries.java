package fi.clim8.clim8server.data;

public enum EHadCURTSummarySeries {
    HADCURT_GLOBAL("global"),
    HADCURT_NORTHERN_HEMISPHERE("northern"),
    HADCURT_SOUTHERN_HEMISPHERE("southern");

    private final String summarySeries;

    EHadCURTSummarySeries(String summarySeries) {
        this.summarySeries = summarySeries;
    }

    public String getSummarySeries() {
        return summarySeries;
    }
}
