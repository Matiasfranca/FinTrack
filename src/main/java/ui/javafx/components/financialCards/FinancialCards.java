package ui.javafx.components.financialCards;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import controller.FinTracker;
import exceptions.InvalidInput;
import javafx.scene.layout.HBox;
import model.dto.CardData;
import model.dto.DailyFinancialData;
import ui.javafx.components.financialCards.financialCard.balanceCard.BalanceCard;
import ui.javafx.components.financialCards.financialCard.expenseCard.ExpenseCard;
import ui.javafx.components.financialCards.financialCard.incomeCard.IncomeCard;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;
import ui.javafx.events.TransactionEventBus.Type;

public class FinancialCards extends HBox {

    private final FinTracker fintracker = new FinTracker();
    private CardData cardData;
    private List<DailyFinancialData> dailyFinancialData;

    private BalanceCard balanceCard;
    private IncomeCard incomeCard;
    private ExpenseCard expenseCard;

    public FinancialCards() {

        setSpacing(40);

        try {
            this.cardData = fintracker.getMonthlyCard(YearMonth.now());
            this.dailyFinancialData = fintracker.getMonthlyOverview(YearMonth.now());
        } catch (InvalidInput e) {
            e.printStackTrace();
        }

        this.balanceCard = new BalanceCard(cardData, dailyFinancialData);
        this.incomeCard = new IncomeCard(cardData, dailyFinancialData);
        this.expenseCard = new ExpenseCard(cardData, dailyFinancialData);

        getChildren().addAll(balanceCard, incomeCard, expenseCard);

        TransactionEventBus.getInstance().subscribe(Type.CREATED, this::updateAllCards);
        TransactionEventBus.getInstance().subscribe(Type.UPDATED, this::updateAllCards);
        TransactionEventBus.getInstance().subscribe(Type.DELETED, this::updateAllCards);

        // Component stylesheet
        getStylesheets().add(getClass().getResource("FinancialCards.css").toExternalForm());

    }

    private void updateAllCards(Event e) {
        CardData newData = this.cardData;
        try {
            newData = fintracker.getMonthlyCard(YearMonth.now());
            balanceCard.refreshData(newData.getCashFlowBalance());
            incomeCard.refreshData(newData.getTotalIncome());
            expenseCard.refreshData(newData.getTotalExpense());
        } catch (InvalidInput e1) {
            e1.printStackTrace();
        }

    }

}
