package ui.javafx.components.financialCards.financialCard.balanceCard;

import java.util.List;

import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class BalanceCard extends FinancialCard {
    public BalanceCard(CardData cardData, List<DailyFinancialData> dailyFinancialData) {
        super("Saldo", cardData.getCashFlowBalance(), ChartMode.BALANCE, dailyFinancialData);
        getStyleClass().add("balance-card");
    }

}
