package ui.javafx.components.financialCards.financialCard.balanceCard;

import model.dto.CardData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class BalanceCard extends FinancialCard {
    public BalanceCard(CardData cardData) {
        super("Saldo", cardData.getCashFlowBalance(), ChartMode.BALANCE);
        getStyleClass().add("balance-card");
    }

}
