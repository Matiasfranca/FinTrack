package ui.javafx.components.financialCards.financialCard.expenseCard;

import model.dto.CardData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class ExpenseCard extends FinancialCard {
    public ExpenseCard(CardData cardData) {
        super("Despesas", cardData.getTotalExpense(), ChartMode.EXPENSE);
        this.valueLabel.getStyleClass().add("danger");
    }
}
