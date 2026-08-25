package ui.javafx.components.charts.expenseDistribution;

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
import ui.javafx.components.financialCards.financialCard.FinancialCard.ChartMode;

public class ExpenseDistribution extends VBox {

    private static final double CANVAS_WIDTH = 380;
    private static final double CANVAS_HEIGHT = 420;

    private static final double CHART_SIZE = 250;
    private static final double CHART_X = (CANVAS_WIDTH - CHART_SIZE) / 2;
    private static final double CHART_Y = 50;

    private final Canvas canvas;

    private static final String[] CATEGORY_COLORS = {
            "#C9A227", "#E0B84B", "#9A9A9E", "#4A4A4E"
    };
    private static final String[] CATEGORY_NAMES = {
            "Alimentação", "Cartão", "Pix", "Outros"
    };
    private static final double[] CATEGORY_VALUES = {
            40, 30, 20, 10 // % — placeholder, troca por dado real depois
    };

    public ExpenseDistribution() {

        setSpacing(10);

        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        getStyleClass().addAll("chart-card", "surface");
        getChildren().addAll(this.buildToggle(), this.canvas);

        this.drawChart();
    }

    // ExpenseDistribution.java — adiciona no topo do card, antes do canvas
    private ChartMode mode = ChartMode.EXPENSE;

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
            mode = ChartMode.EXPENSE;
            this.drawChart();
        });
        investmentTab.setOnAction(e -> {
            // mode = ChartMode.INVESTMENT;
            GraphicsContext gc = this.canvas.getGraphicsContext2D();

            gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        });

        HBox toggle = new HBox(8, title, spacer, expenseTab, investmentTab);
        toggle.setAlignment(Pos.CENTER_RIGHT);
        toggle.getStyleClass().add("chart-toggle");
        return toggle;
    }

    private void drawChart() {

        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        double startAngle = 0;

        for (int i = 0; i < CATEGORY_VALUES.length; i++) {

            double arcExtent = CATEGORY_VALUES[i] * 3.6; // % → graus (360/100)

            gc.setFill(Color.web(CATEGORY_COLORS[i]));
            gc.fillArc(CHART_X, CHART_Y, CHART_SIZE, CHART_SIZE, startAngle, arcExtent, ArcType.ROUND);

            startAngle += arcExtent;
        }

        drawLegend(gc);

    }

    private void drawLegend(GraphicsContext gc) {

        double legendY = CHART_Y + CHART_SIZE + 30;
        double dotSize = 10;

        for (int i = 0; i < CATEGORY_NAMES.length; i++) {

            double y = legendY + i * 22;

            gc.setFill(Color.web(CATEGORY_COLORS[i]));
            gc.fillOval(20, y, dotSize, dotSize);

            gc.setFill(Color.web("#F5F5F0"));
            gc.setFont(javafx.scene.text.Font.font(13));
            gc.fillText(CATEGORY_NAMES[i] + "  " + (int) CATEGORY_VALUES[i] + "%", 40, y + dotSize);
        }
    }

}
