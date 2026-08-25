package ui.javafx.components.financialCards.financialCard.balanceCard;

import ui.javafx.components.financialCards.financialCard.FinancialCard;

public class BalanceCard extends FinancialCard {
    public BalanceCard(String value) {
        super("Saldo", value, ChartMode.BALANCE);
        getStyleClass().add("balance-card");
    }

}
