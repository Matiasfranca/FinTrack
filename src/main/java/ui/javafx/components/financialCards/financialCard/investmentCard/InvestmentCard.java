package ui.javafx.components.financialCards.financialCard.investmentCard;

import java.util.List;
import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class InvestmentCard extends FinancialCard {
    public InvestmentCard(CardData cardData, List<DailyFinancialData> dailyData) {
        super("Investimentos", cardData.getTotalInvestment(), ChartMode.INVESTMENT, dailyData);
    }
}