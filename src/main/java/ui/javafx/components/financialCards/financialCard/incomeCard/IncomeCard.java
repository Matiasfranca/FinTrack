package ui.javafx.components.financialCards.financialCard.incomeCard;

import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class IncomeCard extends FinancialCard {

    public IncomeCard(String value) {
        super("Receitas", value, ChartMode.INCOME);
        this.valueLabel.getStyleClass().add("success");
    }

}
