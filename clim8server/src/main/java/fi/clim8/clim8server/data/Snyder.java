package fi.clim8.clim8server.data;

public class Snyder {
    int year;
    double data;

    public Snyder(int year) {
    this.year = year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getData() {
        return data;
    }
   @Override
    public String toString() {
        return "Year: " + year  + "Data: " + data;
    }
}
