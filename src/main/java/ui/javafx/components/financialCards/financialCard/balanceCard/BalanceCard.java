package ui.javafx.components.financialCards.financialCard.balanceCard;

import java.util.List;

import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class BalanceCard extends FinancialCard {
    public BalanceCard(CardData globalData, List<DailyFinancialData> monthlyData) {
        super("Saldo", globalData.getCashFlowBalance(), ChartMode.BALANCE, monthlyData,
                "evolução do mês");
        getStyleClass().add("balance-card");
    }

}
