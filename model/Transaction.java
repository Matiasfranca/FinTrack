package model;

import java.time.LocalDate;

public class Transaction {
    private double value;
    private String description;
    private boolean receipt;
    private LocalDate date;

    public Transaction(String description, double value, boolean receipt) {
        this.description = description;
        this.value = value;
        this.receipt = receipt;
        this.date = LocalDate.now();
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public LocalDate getDate() {
        return date;
    }
}
