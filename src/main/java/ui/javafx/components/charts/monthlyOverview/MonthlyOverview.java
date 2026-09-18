package ui.javafx.components.charts.monthlyOverview;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ui.javafx.components.FinancialData;

// import java.util.Random;

public class MonthlyOverview extends VBox {

    private static final double CANVAS_WIDTH = 700;
    private static final double CANVAS_HEIGHT = 400;

    private static final double CHART_START_X = 50;
    private static final double CHART_END_X = CANVAS_WIDTH - 20;
    private static final double CENTER_Y = 200;
    private static final double CHART_HALF_HEIGHT = 150;

    private static final double CANDLE_WIDTH = 12;
    private static final int DAYS_IN_MONTH = 31;

    private double[] dailyValues = ui.javafx.components.FinancialData.dailyBalance();
    private Canvas canvas;

    private LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

    public MonthlyOverview() {

        setSpacing(10);

        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.canvas.getStyleClass().add("monthly-chart");

        getStyleClass().addAll("chart-card", "surface");
        getChildren().addAll(this.buildHeader(), this.canvas);

        this.drawChart();
    }

    private HBox buildHeader() {

        Label title = new Label("Balanço Mensal");
        title.getStyleClass().addAll("title", "text-primary");

        Button prevButton = new Button("‹");
        prevButton.getStyleClass().add("month-nav-button");
        prevButton.setOnAction(e -> changeMonth(-1));

        Label periodLabel = new Label(formatMonth(currentMonth));
        periodLabel.getStyleClass().addAll("text-secondary", "month-label");

        Button nextButton = new Button("›");
        nextButton.getStyleClass().add("month-nav-button");
        nextButton.setOnAction(e -> changeMonth(1));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(8, title, spacer, prevButton, periodLabel, nextButton);
        header.setAlignment(Pos.CENTER_LEFT);

        return header;
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        this.dailyValues = FinancialData.dailyBalanceFor(currentMonth.getYear(), currentMonth.getMonthValue());
        drawChart();
    }

    private String formatMonth(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        return Character.toUpperCase(month.charAt(0)) + month.substring(1) + " " + date.getYear();
    }

    private void drawChart() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        double maxAbsValue = 0;
        for (double v : dailyValues) {
            maxAbsValue = Math.max(maxAbsValue, Math.abs(v));
        }

        AxisScale scale = new AxisScale(maxAbsValue, 4); 

        drawGrid(gc, scale);
        drawAxisLines(gc);
        drawCandles(gc, scale);
        drawYAxisLabels(gc, scale);
        drawXAxisLabels(gc);

    }

    private void drawGrid(GraphicsContext gc, AxisScale scale) {

        gc.setStroke(Color.web("#28282C"));
        gc.setLineWidth(1);

        for (double tick : scale.ticks) {
            double offset = (tick / scale.maxTick) * CHART_HALF_HEIGHT;
            gc.strokeLine(CHART_START_X, CENTER_Y - offset, CHART_END_X, CENTER_Y - offset);
            if (tick != 0) {
                gc.strokeLine(CHART_START_X, CENTER_Y + offset, CHART_END_X, CENTER_Y + offset);
            }
        }
    }

    private void drawAxisLines(GraphicsContext gc) {

        gc.setLineWidth(1);
        gc.setStroke(Color.web("#9A9A9E"));

        gc.strokeLine(CHART_START_X, CENTER_Y, CHART_END_X, CENTER_Y);
        gc.strokeLine(CHART_START_X, CENTER_Y - CHART_HALF_HEIGHT - 15,
                CHART_START_X, CENTER_Y + CHART_HALF_HEIGHT + 15);
    }

    private void drawCandles(GraphicsContext gc, AxisScale scale) {

        double chartWidth = CHART_END_X - CHART_START_X;
        double dayWidth = chartWidth / DAYS_IN_MONTH;

        for (int day = 0; day < DAYS_IN_MONTH; day++) {

            double x = CHART_START_X + 10 + day * dayWidth * 0.95;
            double value = dailyValues[day];
            double height = (Math.abs(value) / scale.maxTick) * CHART_HALF_HEIGHT;

            if (value >= 0) {
                gc.setFill(Color.web("#34D399"));
                gc.fillRect(x, CENTER_Y - height, CANDLE_WIDTH, height);
            } else {
                gc.setFill(Color.web("#F87171"));
                gc.fillRect(x, CENTER_Y, CANDLE_WIDTH, height);
            }
        }
    }

    private void drawYAxisLabels(GraphicsContext gc, AxisScale scale) {

        gc.setFill(Color.web("#9A9A9E"));
        gc.setFont(Font.font(11));

        for (double tick : scale.ticks) {
            double offset = (tick / scale.maxTick) * CHART_HALF_HEIGHT;
            String label = formatCurrency(tick);

            gc.fillText(label, 2, CENTER_Y - offset + 4);
            if (tick != 0) {
                gc.fillText("-" + label, 2, CENTER_Y + offset + 4);
            }
        }
    }

    private void drawXAxisLabels(GraphicsContext gc) {

        gc.setFill(Color.web("#9A9A9E"));
        gc.setFont(Font.font(11));

        double chartWidth = CHART_END_X - CHART_START_X;
        double dayWidth = chartWidth / DAYS_IN_MONTH;

        for (int day = 1; day <= DAYS_IN_MONTH; day += 5) {
            double x = CHART_START_X + day * dayWidth;
            gc.fillText(String.valueOf(day), x, CENTER_Y + CHART_HALF_HEIGHT + 30);
        }
    }

    private String formatCurrency(double value) {
        if (value >= 1000) {
            return String.format("%.1fk", value / 1000);
        }
        return String.format("R$%.0f", value);
    }

}
