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
                        PaymentMethod paymentMethod, LocalDate date,
                        int bankAccountId, Integer categoryId) {

        this.description = description;
        this.value = value;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.date = date;
        this.bankAccountId = bankAccountId;
        this.categoryId = categoryId;
    }

    public Transaction(int id, String description, BigDecimal value, TransactionType type,
                        PaymentMethod paymentMethod, LocalDate date,
                        int bankAccountId, Integer categoryId) {

        this(description, value, type, paymentMethod, date, bankAccountId, categoryId);
        this.id = id;
    }

    public Integer getId() { return id; }
    public LocalDate getDate() { return date; }
    public BigDecimal getValue() { return value; }
    public TransactionType getTransactionType() { return type; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getDescription() { return description; }

    //Foreign Keys
    public Integer getBankAccountId() { return bankAccountId; }
    public Integer getCategoryId() { return categoryId; }

    public boolean isReceipt() {
        return type == TransactionType.INCOME;
    }
}