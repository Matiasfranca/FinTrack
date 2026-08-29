package model.dto;

import java.math.BigDecimal;

public class CardData {

    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal investment;

    public CardData(BigDecimal income, BigDecimal expense, BigDecimal investment) {
        this.income = income;
        this.expense = expense;
        this.investment = investment;
    }

    public BigDecimal getCashFlowBalance() { return income.subtract(expense).subtract(investment); }
    public BigDecimal getTotalIncome() { return income; }
    public BigDecimal getTotalExpense() { return expense; }
    
}
