package model.dto;

import java.math.BigDecimal;

public class CardData {

    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal investment;
    private BigDecimal redemption = BigDecimal.ZERO;

    public CardData(BigDecimal income, BigDecimal expense, BigDecimal investment, BigDecimal redemption) {
        this(income,expense,investment);
        this.redemption = redemption;
    }

    public CardData(BigDecimal income, BigDecimal expense, BigDecimal investment) {
        this.income = income;
        this.expense = expense;
        this.investment = investment;
    }

    public BigDecimal getCashFlowBalance() { return income.subtract(expense).subtract(investment).add(redemption); }

    //Getters
    public BigDecimal getTotalIncome() { return income; }
    public BigDecimal getTotalExpense() { return expense; }
    public BigDecimal getTotalInvestment() { return investment; }
    
    //Setters
    public void setTotalIncome(BigDecimal income) { this.income = income; }
    public void setTotalExpense(BigDecimal expense) { this.expense = expense; }
    public void setTotalInvestment(BigDecimal investment) { this.investment = investment; }
    public void setTotalRedemption(BigDecimal redemption) { this.redemption = redemption; }
    
    
}
