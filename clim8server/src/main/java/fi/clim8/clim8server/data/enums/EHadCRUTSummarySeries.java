package fi.clim8.clim8server.data.enums;

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

    public static EHadCRUTSummarySeries find(String s) {
        return switch (s) {
            case "global" -> EHadCRUTSummarySeries.HADCRUT_GLOBAL;
            case "northern" -> EHadCRUTSummarySeries.HADCRUT_NORTHERN_HEMISPHERE;
            case "southern" -> EHadCRUTSummarySeries.HADCRUT_SOUTHERN_HEMISPHERE;
            default -> null;
        };
    }
}
