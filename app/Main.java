package app;

import java.util.InputMismatchException;
import java.util.Scanner;
import controller.FinTracker;
import utils.Formatter;

class Main {
    //variables
    static Scanner sc = new Scanner(System.in);
    static int option = 0;

    public static void main(String[] args) {

        while (option != 5) {
            option = 0;
            Formatter.showMenu();
         
            try {
                option = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                sc.nextLine();
            }
            
            switch (option) {
                case 1:
                    
                    FinTracker.addTransaction(sc);
                    Formatter.pause(sc);
                    Formatter.clearScreen();
                    break;
                
                case 2:
                    
                    FinTracker.listTransaction();
                    Formatter.pause(sc);
                    Formatter.clearScreen();
                    break;

                case 3:
                    
                    FinTracker.removeTransaction(sc);
                    Formatter.pause(sc);
                    Formatter.clearScreen();
                    break;

                case 4:
                    
                    FinTracker.calculateTotalBalance();
                    Formatter.pause(sc);
                    Formatter.clearScreen();
                    break;

                case 5:
                    
                    System.out.println("\nSaindo....");
                    break;
            
                default:

                    System.err.println("\nDigite uma opção válida");
                    Formatter.pause(sc);
                    break;
            }
        }

        sc.close();
    }
}