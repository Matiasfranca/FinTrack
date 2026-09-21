package ui.javafx.components.financialCards.financialCard.expenseCard;

import java.util.List;

import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class ExpenseCard extends FinancialCard {
    public ExpenseCard(CardData cardData, List<DailyFinancialData> dailyFinancialData) {
        super("Despesas", cardData.getTotalExpense(), ChartMode.EXPENSE, dailyFinancialData);
        this.valueLabel.getStyleClass().add("danger");
    }
}
