package ui.javafx.components.financialCards.financialCard.incomeCard;

import model.dto.CardData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class IncomeCard extends FinancialCard {

    public IncomeCard(CardData cardData) {
        super("Receitas", cardData.getTotalIncome(), ChartMode.INCOME);
        this.valueLabel.getStyleClass().add("success");
    }

}
