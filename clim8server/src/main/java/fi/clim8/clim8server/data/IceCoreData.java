package fi.clim8.clim8server.data;

public class IceCoreData {
    int year;
    double data = 0.0;

    public IceCoreData(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Data: " + data;
    }
}
