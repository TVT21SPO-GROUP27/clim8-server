package fi.clim8.clim8server.data;

public class AbstractData {

    int year;
    double data = 0.0;

    public AbstractData(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public double getData() {
        return data;
    }

    public void setData(double degC) {
        data = degC;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Data: " + data;
    }
}