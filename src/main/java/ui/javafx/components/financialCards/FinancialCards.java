package ui.javafx.components.financialCards;

import java.time.YearMonth;
import java.util.List;

import controller.FinTracker;
import exceptions.InvalidInput;
import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.FinancialCard;
import ui.javafx.components.financialCards.financialCard.balanceCard.BalanceCard;
import ui.javafx.components.financialCards.financialCard.expenseCard.ExpenseCard;
import ui.javafx.components.financialCards.financialCard.incomeCard.IncomeCard;
import ui.javafx.components.financialCards.financialCard.investmentCard.InvestmentCard;
import ui.javafx.events.TransactionEventBus;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class FinancialCards extends HBox {

    private final FinTracker fintracker = new FinTracker();
    private CardData cardData;
    private CardData globCardData;
    private List<DailyFinancialData> newDailyData;

    private FinancialCard balanceCard;
    private FinancialCard incomeCard;
    private FinancialCard expenseCard;
    private FinancialCard investmentCard;

    public FinancialCards() {

        setSpacing(40);

        fetchData();

        balanceCard = new BalanceCard(globCardData, newDailyData);
        incomeCard = new IncomeCard(cardData, newDailyData);
        expenseCard = new ExpenseCard(cardData, newDailyData);
        investmentCard = new InvestmentCard(globCardData, newDailyData);

        for (var card : List.of(balanceCard, incomeCard, expenseCard, investmentCard)) {
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
        }

        getChildren().addAll(balanceCard, incomeCard, expenseCard, investmentCard);

        TransactionEventBus.getInstance().subscribe(e -> updateAllCards());

        // Component stylesheet
        getStylesheets().add(getClass().getResource("FinancialCards.css").toExternalForm());

    }

    private void fetchData() {
        try {
            cardData = fintracker.getMonthlyCard(YearMonth.now());
            globCardData = fintracker.getGlobalCard();
            newDailyData = fintracker.getMonthlyOverview(YearMonth.now());
        } catch (InvalidInput e) {
            e.printStackTrace();
        }
    }

    private void updateAllCards() {
        fetchData();

        balanceCard.refreshData(globCardData.getCashFlowBalance(), newDailyData);
        incomeCard.refreshData(cardData.getTotalIncome(), newDailyData);
        expenseCard.refreshData(cardData.getTotalExpense(), newDailyData);
        investmentCard.refreshData(globCardData.getTotalInvestment(), newDailyData);
    }

}
