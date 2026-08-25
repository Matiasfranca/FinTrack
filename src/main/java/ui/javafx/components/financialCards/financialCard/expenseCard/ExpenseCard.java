package ui.javafx.components.financialCards.financialCard.expenseCard;

import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class ExpenseCard extends FinancialCard {
    public ExpenseCard(String value) {
        super("Despesas", value, ChartMode.EXPENSE);
        this.valueLabel.getStyleClass().add("danger");
    }
}
