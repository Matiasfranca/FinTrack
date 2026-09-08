package ui.javafx.components.financialCards.financialCard;

import java.math.BigDecimal;
import java.util.List;
import utils.FormatCurrency;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.dto.DailyFinancialData;

public abstract class FinancialCard extends VBox {

    public enum ChartMode {
        BALANCE, INCOME, EXPENSE
    }

    private static final double CHART_WIDTH = 333; // The chart width accounts for the card's horizontal padding.
    private static final double CHART_HEIGHT = 50;

    private final Canvas chartCanvas;
    private List<DailyFinancialData> dailyFinancialData;

    protected Label valueLabel;
    private final ChartMode chartMode;

    public FinancialCard(String title, BigDecimal value, ChartMode chartMode,
            List<DailyFinancialData> dailyFinancialData, String scopeHint) {

        this.chartMode = chartMode;
        this.dailyFinancialData = dailyFinancialData;

        setSpacing(14);
        getStyleClass().addAll("card", "surface");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().addAll("text-primary", "title");

        this.valueLabel = new Label(FormatCurrency.formatCurrency(value));
        valueLabel.getStyleClass().addAll("text-primary", "value");

        this.chartCanvas = new Canvas(CHART_WIDTH, CHART_HEIGHT);
        drawMiniChart();

        getChildren().add(titleLabel);
        getChildren().add(valueLabel);

        if (scopeHint != null) {
            Label hint = new Label(scopeHint);
            hint.getStyleClass().addAll("text-secondary", "scope-hint");
            getChildren().add(hint);
        }

        getChildren().add(chartCanvas);
    }

    public FinancialCard(String title, BigDecimal value, ChartMode chartMode,
            List<DailyFinancialData> dailyFinancialData) {
        this(title, value, chartMode, dailyFinancialData, null);
    }

    public void refreshData(BigDecimal newValue, List<DailyFinancialData> dailyFinancialData) {
        this.valueLabel.setText(FormatCurrency.formatCurrency(newValue));
        this.dailyFinancialData = dailyFinancialData;

        drawMiniChart();
    }

    private double[] getChartValuesAsArray() {
        int daysInMonth = java.time.LocalDate.now().lengthOfMonth();
        double[] monthValues = new double[daysInMonth];

        if (dailyFinancialData != null) {
            dailyFinancialData.forEach(data -> {
                int index = data.getDayOfMonth() - 1;

                monthValues[index] = switch (chartMode) {
                    case BALANCE -> data.getBalance().doubleValue();
                    case INCOME -> data.getIncome().doubleValue();
                    case EXPENSE -> data.getExpense().doubleValue();
                };
            });
        }
        return monthValues;
    }

    /**
     * Selects the appropriate chart according to the card's purpose.
     */
    private void drawMiniChart() {
        // Reset Graphic
        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());

        double[] values = getChartValuesAsArray();
        switch (chartMode) {
            case BALANCE -> drawBalanceChart(gc, values);
            case INCOME -> drawSingleColorChart(gc, values, "#34D399");
            case EXPENSE -> drawSingleColorChart(gc, values, "#F87171");
        }
    }

    /**
     * Draws the balance chart using a single amber line.
     *
     * Positive values are represented by a green filled area,
     * while negative values are represented by a red filled area.
     * The zero line is positioned at the vertical center of the chart.
     */
    private void drawBalanceChart(GraphicsContext gc, double[] values) {

        double maxAbs = maxAbs(values);
        double baseline = CHART_HEIGHT / 2.0;
        double stepX = CHART_WIDTH / (values.length - 1);

        gc.setFill(Color.web("#34D399", 0.25));
        fillAreaClampedToBaseline(gc, values, maxAbs, baseline, stepX, true);

        gc.setFill(Color.web("#F87171", 0.25));
        fillAreaClampedToBaseline(gc, values, maxAbs, baseline, stepX, false);

        // The balance line uses the primary amber color.
        gc.setStroke(Color.web("#C9A227"));
        gc.setLineWidth(1.5);

        // Connects each data point to the next one to form the line chart.
        for (int i = 0; i < values.length - 1; i++) {

            double x1 = i * stepX;
            double y1 = baseline - (values[i] / maxAbs) * baseline;

            double x2 = (i + 1) * stepX;
            double y2 = baseline - (values[i + 1] / maxAbs) * baseline;

            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    /**
     * Fills the area between the chart line and the zero baseline.
     *
     * When positiveSide is true, only the area above the baseline
     * is filled. When false, only the area below the baseline is filled.
     */
    private void fillAreaClampedToBaseline(GraphicsContext gc, double[] values, double maxAbs,
            double baseline, double stepX, boolean positiveSide) {

        gc.beginPath();

        // Start the filled area at the zero baseline.
        gc.moveTo(0, baseline);

        for (int i = 0; i < values.length; i++) {

            double x = i * stepX;
            double y = baseline - (values[i] / maxAbs) * baseline;

            /*
             * Clamp the point to the baseline when it falls on
             * the opposite side of the zero line.
             */
            y = positiveSide ? Math.min(y, baseline) : Math.max(y, baseline);

            gc.lineTo(x, y);
        }

        // Close the area back at the baseline.
        gc.lineTo(CHART_WIDTH, baseline);
        gc.closePath();
        gc.fill();
    }

    /**
     * Draws a single-color line chart with a translucent filled area.
     *
     * This is used for income and expense cards, where all values
     * belong to the same financial category.
     */
    private void drawSingleColorChart(GraphicsContext gc, double[] values, String colorHex) {

        // Determines the vertical scale from the highest value.
        double max = max(values);

        // Distributes all data points evenly across the chart width.
        double stepX = CHART_WIDTH / (values.length - 1);

        // Draw the translucent area below the chart line.
        gc.setFill(Color.web(colorHex, 0.25));

        gc.beginPath();

        // Start at the bottom-left corner of the chart.
        gc.moveTo(0, CHART_HEIGHT);

        for (int i = 0; i < values.length; i++) {

            double x = i * stepX;
            double y = CHART_HEIGHT - (values[i] / max) * CHART_HEIGHT;

            gc.lineTo(x, y);
        }

        // Close the area at the bottom-right corner.
        gc.lineTo(CHART_WIDTH, CHART_HEIGHT);
        gc.closePath();
        gc.fill();

        // Draw the chart line using the same color at full opacity.
        gc.setStroke(Color.web(colorHex));
        gc.setLineWidth(1.5);

        for (int i = 0; i < values.length - 1; i++) {

            double x1 = i * stepX;
            double y1 = CHART_HEIGHT - (values[i] / max) * CHART_HEIGHT;

            double x2 = (i + 1) * stepX;
            double y2 = CHART_HEIGHT - (values[i + 1] / max) * CHART_HEIGHT;

            gc.strokeLine(x1, y1, x2, y2);
        }
    }

    /**
     * Returns the largest absolute value in the dataset.
     *
     * Absolute values are used because the balance chart can contain
     * both positive and negative values.
     */
    private double maxAbs(double[] values) {

        double max = 0;

        for (double v : values)
            max = Math.max(max, Math.abs(v));

        // Prevents division by zero when all values are zero.
        return max == 0 ? 1 : max;
    }

    /**
     * Returns the largest positive value in the dataset.
     *
     * Used to normalize income and expense charts vertically.
     */
    private double max(double[] values) {

        double max = 0;

        for (double v : values)
            max = Math.max(max, v);

        return max == 0 ? 1 : max;
    }

}
