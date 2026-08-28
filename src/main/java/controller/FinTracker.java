// package controller;

// import java.util.List;
// import exceptions.InvalidInput;
// import model.Transaction;
// import model.Month;

// public class FinTracker {

//     private final Month monthlyTransaction = new Month();

//     public void addTransaction(String description, double value, boolean receipt) {

//         Transaction transaction;

//         // transaction = new Transaction(description, value, receipt);
//         // this.monthlyTransaction.add(transaction);

//     }

//     public List<Transaction> listTransaction() {
//         List<Transaction> transactions = this.monthlyTransaction.getTransactions();
//         return transactions;
//     }

//     public void removeTransaction(int option) throws InvalidInput {

//         this.monthlyTransaction.del(option);

//     }

//     public double calculateTotalBalance() {
//         return this.monthlyTransaction.totalBalance();
//     }

// }
