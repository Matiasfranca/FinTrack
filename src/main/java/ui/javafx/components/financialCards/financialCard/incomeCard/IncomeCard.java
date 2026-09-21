package ui.javafx.components.financialCards.financialCard.incomeCard;

import java.util.List;

import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class IncomeCard extends FinancialCard {

    public IncomeCard(CardData cardData, List<DailyFinancialData> dailyFinancialData) {
        super("Receitas", cardData.getTotalIncome(), ChartMode.INCOME, dailyFinancialData);
        this.valueLabel.getStyleClass().add("success");
    }

}
