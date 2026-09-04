package ui.javafx.components.charts.expenseDistribution;

import javafx.scene.paint.Color;

public class ColorGenerator {

    public static Color generateHarmonicColor(int index) {

        double hue = (index * 137.5) % 360;

        double saturation = 0.65;

        double brightness = 0.80;

        return Color.hsb(hue, saturation, brightness);
    }

    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}