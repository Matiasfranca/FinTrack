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

    private static final double SLICE_SEPARATOR_WIDTH = 1.0;

    private static final double CANVAS_WIDTH = 380;
    private static final double MIN_CANVAS_HEIGHT = 420;
    private static final double LEGEND_ITEM_HEIGHT = 22;
    private static final double LEGEND_BOTTOM_PADDING = 20;

    private static final double CHART_SIZE = 250;
    private static final double CHART_X = (CANVAS_WIDTH - CHART_SIZE) / 2;
    private static final double CHART_Y = 50;

    private final Canvas canvas;

    private List<CategoryChartData> expenseData;
    private List<CategoryChartData> investmentData;

    private ChartMode currentMode = ChartMode.EXPENSE;

    public ExpenseDistribution(List<CategoryChartData> expenseData, List<CategoryChartData> investmentData) {

        setSpacing(10);

        this.canvas = new Canvas(CANVAS_WIDTH, MIN_CANVAS_HEIGHT);

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

        List<CategoryChartData> activeList = (mode == ChartMode.EXPENSE) ? expenseData : investmentData;

        double legendHeight = (activeList == null || activeList.isEmpty())
                ? 0
                : activeList.size() * LEGEND_ITEM_HEIGHT;

        double requiredHeight = CHART_Y + CHART_SIZE + 30 + legendHeight + LEGEND_BOTTOM_PADDING;
        this.canvas.setHeight(Math.max(MIN_CANVAS_HEIGHT, requiredHeight));

        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_WIDTH, this.canvas.getHeight());

        if (activeList == null || activeList.isEmpty()) {
            drawEmptyState(gc);
            return;
        }

        double totalSum = activeList.stream().mapToDouble(data -> data.getTotalValue().doubleValue()).sum();

        if (totalSum == 0) {
            drawEmptyState(gc);
            return;
        }

        gc.save();

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

        gc.restore();

        drawSliceSeparators(gc, activeList, totalSum);
    }

    private void drawSliceSeparators(GraphicsContext gc, List<CategoryChartData> activeList, double totalSum) {

        if (activeList.size() > 1) {

            gc.save();

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

            gc.restore();
        }

        drawLegend(gc, activeList, totalSum);
    }

    private void drawLegend(GraphicsContext gc, List<CategoryChartData> activeList, double totalSum) {

        gc.save();

        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);

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

        gc.restore();
    }

    private void drawEmptyState(GraphicsContext gc) {

        gc.save();

        gc.setStroke(Color.web("#28282C"));
        gc.setLineWidth(1.5);
        gc.strokeOval(CHART_X, CHART_Y, CHART_SIZE, CHART_SIZE);

        gc.setFill(Color.web("#9A9A9E"));
        gc.setFont(javafx.scene.text.Font.font(13));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText("Nenhum dado nesse período",
                CHART_X + CHART_SIZE / 2, CHART_Y + CHART_SIZE / 2);

        gc.restore();
    }

    private Color getCategoryColor(String hexColor, int index) {
        try {
            if (hexColor != null && !hexColor.equalsIgnoreCase("#A9A9A9")) {
                return Color.web(hexColor);
            }
        } catch (IllegalArgumentException e) {
        }

        return ColorGenerator.get(index);
    }

    public void refreshData(List<CategoryChartData> expenseData, List<CategoryChartData> investmentData) {
        this.expenseData = expenseData;
        this.investmentData = investmentData;

        this.drawChart(this.currentMode);
    }
}