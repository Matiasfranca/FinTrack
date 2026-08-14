package utils;

public class InputValidator {

    public static boolean isValid(String input) {
        return input != null && !input.isBlank();
    }

    public static boolean isValid(int input) {
        return input >= 0;
    }

    public static boolean isValid(double input) {
        return input >= 0;
    }
}