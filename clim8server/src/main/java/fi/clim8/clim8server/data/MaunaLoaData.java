package fi.clim8.clim8server.data;

public class MaunaLoaData {
    int year;
    int month;
    double data = 0.0;

    public MaunaLoaData(int year) {
    this.year = year;
    }

    public MaunaLoaData(int year, int month) {
        this.year = year;
        this.month = month;
    }


    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setMonth(int month) {
    this.month = month;
    }

    public int getMonth() {
    return month;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Year: " + year + ", Month: " + month + "Data: " + data;
    }
}
