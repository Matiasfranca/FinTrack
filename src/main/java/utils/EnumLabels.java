package utils;

import model.BankAccountType;
import model.PaymentMethod;
import model.TransactionType;

/**
 * Single source of truth for translating domain enums into the
 * pt-BR labels shown in the UI.
 *
 * Before this class existed, FormTransaction and TransactionRow each
 * kept their own private copy of these same switch statements. Adding
 * a new enum value (like REDEMPTION was) meant remembering to update
 * every copy; now there is exactly one place to update.
 */
public class EnumLabels {

    private EnumLabels() {
        // Static utility class — not meant to be instantiated.
    }

    public static String label(TransactionType type) {
        if (type == null) {
            return "Não informado";
        }
        return switch (type) {
            case INCOME -> "Receita";
            case EXPENSE -> "Despesa";
            case INVESTMENT -> "Investimento";
            case REDEMPTION -> "Resgate";
        };
    }

    public static String label(PaymentMethod method) {
        if (method == null) {
            return "Não informado";
        }
        return switch (method) {
            case PIX -> "Pix";
            case DEBIT_CARD -> "Cartão de débito";
            case CREDIT_CARD -> "Cartão de crédito";
            case CASH -> "Dinheiro";
            case BANK_TRANSFER -> "Transferência";
            case BOLETO -> "Boleto";
        };
    }

    public static String label(BankAccountType type) {
        if (type == null) {
            return "Não informado";
        }
        return switch (type) {
            case CHECKING -> "Corrente";
            case SAVINGS -> "Poupança";
            case CASH -> "Dinheiro";
            case OTHER -> "Outro";
        };
    }
}