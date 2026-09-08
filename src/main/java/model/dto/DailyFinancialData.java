package model.dto;

import java.math.BigDecimal;

public class DailyFinancialData {
    private final int dayOfMonth;
    private final BigDecimal income;
    private final BigDecimal expense;
    private final BigDecimal investment;
    private final BigDecimal redemption;

    public DailyFinancialData(int dayOfMonth, BigDecimal income, BigDecimal expense, BigDecimal investment, BigDecimal redemption) {
        this.dayOfMonth = dayOfMonth;
        this.income = income;
        this.expense = expense;
        this.investment = investment;
        this.redemption = redemption;
    }


    public int getDayOfMonth() { return dayOfMonth; }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getExpense() { return expense; }
    public BigDecimal getInvestment() { return investment.subtract(redemption).compareTo(BigDecimal.ZERO) > 0 ? investment.subtract(redemption) : BigDecimal.ZERO; }
    
    public BigDecimal getBalance() {
        return income.subtract(expense).subtract(investment).add(redemption);
    }
}