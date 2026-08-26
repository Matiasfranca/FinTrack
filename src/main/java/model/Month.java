package model;

import java.time.YearMonth;

public class Month {

    private Integer id;
    private int year;
    private int month;

    public Month(Integer id, int year, int month) {
        this.id = id;
        this.year = year;
        this.month = month;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }


    /**
     * Convenience method to facilitate integration with the UI and Controller.
     * Returns the YearMonth object corresponding to this month.
     */
    public YearMonth getYearMonth() {
        return YearMonth.of(this.year, this.month);
    }
}