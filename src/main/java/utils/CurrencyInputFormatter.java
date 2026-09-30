package utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * Attaches pt-BR currency masking and parsing behaviour to a TextField:
 * only digits, dots and commas are accepted while typing, and the field
 * is reformatted to "1.234,56" once it loses focus.
 *
 * This logic used to live twice inside FormTransaction — once in
 * configureValueField() (live masking) and again in getValue() (final
 * parsing) — with the same dot/comma normalization repeated in both.
 * Keeping it in one place means both behaviours can never drift apart.
 */
public class CurrencyInputFormatter {

    private CurrencyInputFormatter() {
        // Static utility class — not meant to be instantiated.
    }

    /** Wires live masking and on-blur reformatting onto the given field. */
    public static void attach(TextField field) {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            return text.matches("[0-9.,]*") ? change : null;
        };

        field.setTextFormatter(new TextFormatter<>(filter));
        field.setPromptText("0,00");

        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                reformatQuietly(field);
            }
        });
    }

    private static void reformatQuietly(TextField field) {
        try {
            BigDecimal value = parse(field.getText());
            field.setText(decimalFormat().format(value));
        } catch (NumberFormatException e) {
            // Leaves the field untouched — the caller is responsible for
            // validating before actually saving the transaction.
        }
    }

    /**
     * Parses the field's text into a BigDecimal, accepting both
     * "1234,56" and "1234.56" typed forms.
     *
     * @throws NumberFormatException if the text isn't a valid number.
     */
    public static BigDecimal parse(String rawText) {

        String text = rawText == null ? "" : rawText.trim();

        // A lone dot followed by 1-2 digits is treated as a decimal
        // separator typed the "English" way (e.g. "1234.5").
        if (text.contains(".") && !text.contains(",") && text.matches(".*\\.\\d{1,2}$")) {
            int lastDot = text.lastIndexOf('.');
            text = text.substring(0, lastDot) + "," + text.substring(lastDot + 1);
        }

        String normalized = text.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }

    private static DecimalFormat decimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("pt", "BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        return new DecimalFormat("#,##0.00", symbols);
    }
}