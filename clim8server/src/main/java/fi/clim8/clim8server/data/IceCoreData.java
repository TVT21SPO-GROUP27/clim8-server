package fi.clim8.clim8server.data;

public class IceCoreData {
    int year;
    double data = 0.0;
    String series;

    public IceCoreData(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Series: " + series + ", Data: " + data;
    }
}
