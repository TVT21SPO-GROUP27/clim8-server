package fi.clim8.clim8server.data;

public class GlobalGHGData {

    String country;
    double greenhouseGas;

    public GlobalGHGData(String country, double greenhouseGas) {
        this.country = country;
        this.greenhouseGas = greenhouseGas;
    }

    public String getCountry() {
        return country;
    }

    public double getGreenhouseGas() {
        return greenhouseGas;
    }
}
