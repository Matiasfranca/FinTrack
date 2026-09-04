package ui.javafx.components.charts.expenseDistribution;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.dto.CategoryChartData;

public class ExpenseDistribution extends VBox {

    private enum ChartMode {
        EXPENSE, INVESTMENT
    }

    private static final String[] FALLBACK_PALETTE = {
            "#C9A227",
            "#A8894A",
            "#7E8B5A",
            "#628B78",
            "#647A8A",
            "#7A708C",
            "#986B73",
            "#A56F4F",
            "#7B7770"
    };

    private static final double SLICE_SEPARATOR_WIDTH = 1.0;

    private static final double CANVAS_WIDTH = 380;
    private static final double CANVAS_HEIGHT = 420;

    private static final double CHART_SIZE = 250;
    private static final double CHART_X = (CANVAS_WIDTH - CHART_SIZE) / 2;
    private static final double CHART_Y = 50;

    private final Canvas canvas;

    private List<CategoryChartData> expenseData;
    private List<CategoryChartData> investmentData;

    private ChartMode currentMode = ChartMode.EXPENSE;

    public ExpenseDistribution(List<CategoryChartData> expenseData, List<CategoryChartData> investmentData) {

        setSpacing(10);

        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        this.expenseData = expenseData;
        this.investmentData = investmentData;

        getStyleClass().addAll("chart-card", "surface");
        getChildren().addAll(this.buildToggle(), this.canvas);

        this.drawChart(this.currentMode);
    }

    private HBox buildToggle() {

        Label title = new Label("Categorias");
        title.getStyleClass().addAll("title", "text-primary");

        ToggleGroup group = new ToggleGroup();

        ToggleButton expenseTab = new ToggleButton("Despesas");
        expenseTab.setToggleGroup(group);
        expenseTab.setSelected(true);
        expenseTab.getStyleClass().add("chart-tab");

        ToggleButton investmentTab = new ToggleButton("Investimentos");
        investmentTab.setToggleGroup(group);
        investmentTab.getStyleClass().add("chart-tab");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        expenseTab.setOnAction(e -> {
            this.currentMode = ChartMode.EXPENSE;
            this.drawChart(this.currentMode);
        });

        investmentTab.setOnAction(e -> {
            this.currentMode = ChartMode.INVESTMENT;
            this.drawChart(this.currentMode);
        });

        HBox toggle = new HBox(8, title, spacer, expenseTab, investmentTab);
        toggle.setAlignment(Pos.CENTER_RIGHT);
        toggle.getStyleClass().add("chart-toggle");
        return toggle;
    }

    private void drawChart(ChartMode mode) {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        List<CategoryChartData> activeList = (mode == ChartMode.EXPENSE) ? expenseData : investmentData;

        if (activeList == null || activeList.isEmpty()) {
            return;
        }

        double totalSum = activeList.stream().mapToDouble(data -> data.getTotalValue().doubleValue()).sum();

        if (totalSum == 0)
            return;

        double startAngle = 0;

        for (int i = 0; i < activeList.size(); i++) {
            CategoryChartData data = activeList.get(i);

            double value = data.getTotalValue().doubleValue();
            double arcExtent = (value / totalSum) * 360.0;

            Color color = getCategoryColor(data.getCategoryColor(), i);

            gc.setFill(color);
            gc.fillArc(CHART_X, CHART_Y, CHART_SIZE, CHART_SIZE, startAngle, arcExtent, ArcType.ROUND);

            startAngle += arcExtent;
        }

        drawSliceSeparators(gc, activeList, totalSum);
    }

    private void drawSliceSeparators(GraphicsContext gc, List<CategoryChartData> activeList, double totalSum) {

        double centerX = CHART_X + CHART_SIZE / 2;
        double centerY = CHART_Y + CHART_SIZE / 2;
        double radius = CHART_SIZE / 2;

        double angle = 0;

        gc.setStroke(Color.web("#1C1C1F"));
        gc.setLineWidth(SLICE_SEPARATOR_WIDTH);

        for (CategoryChartData data : activeList) {

            double value = data.getTotalValue().doubleValue();
            double arcExtent = (value / totalSum) * 360.0;

            double radians = Math.toRadians(angle);

            double x = centerX + Math.cos(radians) * radius;
            double y = centerY - Math.sin(radians) * radius;

            gc.strokeLine(centerX, centerY, x, y);

            angle += arcExtent;
        }

        drawLegend(gc, activeList, totalSum);
    }

    private void drawLegend(GraphicsContext gc, List<CategoryChartData> activeList, double totalSum) {
        double legendY = CHART_Y + CHART_SIZE + 30;
        double dotSize = 10;

        for (int i = 0; i < activeList.size(); i++) {
            CategoryChartData data = activeList.get(i);
            double y = legendY + i * 22;

            Color color = getCategoryColor(data.getCategoryColor(), i);

            gc.setFill(color);
            gc.fillOval(20, y, dotSize, dotSize);

            double percentage = (data.getTotalValue().doubleValue() / totalSum) * 100.0;

            gc.setFill(Color.web("#F5F5F0"));
            gc.setFont(javafx.scene.text.Font.font(13));

            String labelText = String.format("%s  %.1f%%", data.getCategoryName(), percentage);
            gc.fillText(labelText, 40, y + dotSize);
        }
    }

    private Color getCategoryColor(String hexColor, int index) {
        try {
            if (hexColor != null && !hexColor.equalsIgnoreCase("#A9A9A9")) {
                return Color.web(hexColor);
            }
        } catch (IllegalArgumentException e) {
        }

        if (index < FALLBACK_PALETTE.length) {
            return Color.web(FALLBACK_PALETTE[index]);
        }

        Color base = Color.web(
                FALLBACK_PALETTE[FALLBACK_PALETTE.length - 1]);

        double hue = (base.getHue() + index * 137.5) % 360;

        return Color.hsb(
                hue,
                base.getSaturation(),
                base.getBrightness());

    }

    public void refreshData(List<CategoryChartData> expenseData, List<CategoryChartData> investmentData) {
        this.expenseData = expenseData;
        this.investmentData = investmentData;

        this.drawChart(this.currentMode);
    }
}