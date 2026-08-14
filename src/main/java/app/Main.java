package app;

import java.util.Scanner;
// import javafx.application.Application;
import ui.console.ConsoleUI;
// import ui.javafx.FinTrackUI;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== FinTrack ===");
        System.out.println("1 - Terminal");
        System.out.println("2 - Interface gráfica");
        System.out.print("Escolha: ");

        int option = sc.nextInt();

        switch (option) {
            case 1 -> new ConsoleUI().start(sc);
            case 2 -> {
                System.out.println("in development, starting the terminal...\npress Enter...");
                sc.nextLine();
                sc.nextLine();

                new ConsoleUI().start(sc);
            }
            default -> System.out.println("Invalid option, aborting operation.");
        }
    }
}