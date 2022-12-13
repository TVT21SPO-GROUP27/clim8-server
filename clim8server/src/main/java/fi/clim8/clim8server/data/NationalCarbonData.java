package fi.clim8.clim8server.data;

public class NationalCarbonData extends AbstractData {

    String country;

    public NationalCarbonData(int year, String country) {
        super(year);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}
