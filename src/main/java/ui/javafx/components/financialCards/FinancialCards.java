package ui.javafx.components.financialCards;

import javafx.scene.layout.HBox;
import ui.javafx.components.FinancialData;
import ui.javafx.components.financialCards.financialCard.balanceCard.BalanceCard;
import ui.javafx.components.financialCards.financialCard.expenseCard.ExpenseCard;
import ui.javafx.components.financialCards.financialCard.incomeCard.IncomeCard;

public class FinancialCards extends HBox {
    public FinancialCards() {

        setSpacing(40);

        getChildren().addAll(
                new BalanceCard(FinancialData.formatCurrency(FinancialData.totalBalance())),
                new IncomeCard(FinancialData.formatCurrency(FinancialData.totalIncome())),
                new ExpenseCard(FinancialData.formatCurrency(FinancialData.totalExpense())));
                
        // Component stylesheet
        getStylesheets().add(getClass().getResource("FinancialCards.css").toExternalForm());

    }

}
