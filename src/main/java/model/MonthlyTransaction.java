package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import exceptions.InvalidInput;

public class MonthlyTransaction {
    
    private List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public void del(int option) throws InvalidInput{

        if (transactions.isEmpty()) {
            throw new InvalidInput("\nNão existem transações para remover.");
        }

        if (option <= 0 || option > transactions.size()) {
            throw new InvalidInput("\nNão existe essa transação");
        }

        transactions.remove(--option);

    }

    public List<Transaction> getTransactions() {

        return Collections.unmodifiableList(transactions);
    
    }

    public double totalBalance(){
        double total = 0;

        for (Transaction transaction : transactions) {
            total += transaction.getValue();
        }
        return total;
    }
}
