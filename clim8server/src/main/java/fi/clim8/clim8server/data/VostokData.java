package fi.clim8.clim8server.data;

public class VostokData {
    int year;
    double depth;
    double data = 0.0;

    public VostokData(double depth) {
        this.depth = depth;
    }

    public int getYear() {
        return year;
    }

    public double getDepth() {
        return depth;
    }

    public double getData() {
        return data;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Data: " + data;
    }
}
