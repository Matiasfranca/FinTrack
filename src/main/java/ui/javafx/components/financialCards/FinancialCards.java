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
import ui.javafx.events.TransactionEventBus.Event;

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

        this.fetchData();

        this.balanceCard = new BalanceCard(this.globCardData, newDailyData);
        this.incomeCard = new IncomeCard(this.cardData, newDailyData);
        this.expenseCard = new ExpenseCard(this.cardData, newDailyData);
        this.investmentCard = new InvestmentCard(this.globCardData, newDailyData);

        for (var card : List.of(balanceCard, incomeCard, expenseCard, investmentCard)) {
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setMaxWidth(Double.MAX_VALUE);
        }

        getChildren().addAll(balanceCard, incomeCard, expenseCard, investmentCard);

        TransactionEventBus.getInstance().subscribe(this::updateAllCards);

        // Component stylesheet
        getStylesheets().add(getClass().getResource("FinancialCards.css").toExternalForm());

    }

    private void fetchData() {
        try {
            this.cardData = this.fintracker.getMonthlyCard(YearMonth.now());
            this.globCardData = this.fintracker.getGlobalCard();
            this.newDailyData = this.fintracker.getMonthlyOverview(YearMonth.now());
        } catch (InvalidInput e) {
            e.printStackTrace();
        }
    }

    private void updateAllCards(Event e) {
        this.fetchData();

        this.balanceCard.refreshData(this.globCardData.getCashFlowBalance(), this.newDailyData);
        this.incomeCard.refreshData(this.cardData.getTotalIncome(), this.newDailyData);
        this.expenseCard.refreshData(this.cardData.getTotalExpense(), this.newDailyData);
        this.investmentCard.refreshData(this.globCardData.getTotalInvestment(), this.newDailyData);
    }

}
