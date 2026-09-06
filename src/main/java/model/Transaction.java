package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private Integer id;
    private String description;
    private BigDecimal value;
    private TransactionType type;
    private PaymentMethod paymentMethod;
    private LocalDate date;
    private Integer bankAccountId;
    private Integer categoryId;

    public Transaction(String description, BigDecimal value, TransactionType type,
            PaymentMethod paymentMethod, LocalDate date) {

        this.description = description;
        this.value = value;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.date = date;
    }

    public Transaction(int id, String description, BigDecimal value, TransactionType type,
            PaymentMethod paymentMethod, LocalDate date,
            int bankAccountId, Integer categoryId) {

        this(description, value, type, paymentMethod, date);
        this.id = id;
        this.bankAccountId = bankAccountId;
        this.categoryId = categoryId;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getValue() {
        return value;
    }

    public TransactionType getTransactionType() {
        return type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    // Foreign Keys
    public Integer getBankAccountId() {
        return bankAccountId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public boolean isReceipt() {
        return type == TransactionType.INCOME || type == TransactionType.REDEMPTION;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBankAccountId(Integer bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}