package model.dto;

import java.math.BigDecimal;

public class DailyFinancialData {
    private final int dayOfMonth;
    private final BigDecimal income;
    private final BigDecimal expense;

    public DailyFinancialData(int dayOfMonth, BigDecimal income, BigDecimal expense) {
        this.dayOfMonth = dayOfMonth;
        this.income = income;
        this.expense = expense;
    }

    public int getDayOfMonth() { return dayOfMonth; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    
    public BigDecimal getBalance() {
        return income.subtract(expense);
    }
}