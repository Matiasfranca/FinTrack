package model.dto;

import java.math.BigDecimal;

public class DailyFinancialData {
    private final int dayOfMonth;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal investment;

    public DailyFinancialData(int dayOfMonth, BigDecimal income, BigDecimal expense, BigDecimal investment) {
        this.dayOfMonth = dayOfMonth;
        this.income = income;
        this.expense = expense;
        this.investment = investment;
    }

    public int getDayOfMonth() { return dayOfMonth; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    public BigDecimal getInvestment() { return investment; }
    
    public BigDecimal getBalance() {
        return income.subtract(expense).subtract(investment);
    }
}