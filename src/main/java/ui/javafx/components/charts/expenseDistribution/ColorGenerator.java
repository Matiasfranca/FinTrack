package ui.javafx.components.charts.expenseDistribution;

import javafx.scene.paint.Color;

public class ColorGenerator {

    private static final String[] PALETTE = {
            "#C9A227",
            "#E0B84B",
            "#7E8B5A",
            "#6B6B70",
            "#8A6C1B",
            "#9A9A9E",
            "#B08A45",
            "#A8894A",
    };

    public static Color get(int index) {

        if (index < PALETTE.length) {
            return Color.web(PALETTE[index]);
        }

        double lightnessStep = ((index - PALETTE.length) % 5) * 0.08;
        Color base = Color.web("#C9A227");
        return base.deriveColor(0, 1.0, 1.0 - lightnessStep, 1.0);
    }

    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}