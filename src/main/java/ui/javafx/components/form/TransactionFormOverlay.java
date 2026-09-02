package ui.javafx.components.form;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Transaction;

public class TransactionFormOverlay extends StackPane {

    private final Rectangle background = new Rectangle();
    private FormTransaction form;

    public TransactionFormOverlay() {

        setPickOnBounds(false);
        setMouseTransparent(true);

        background.setFill(Color.rgb(0, 0, 0, 0.35));
        
        background.widthProperty().bind(widthProperty());
        background.heightProperty().bind(heightProperty());
        
        background.setOnMouseClicked(e -> hide());
    }

    public void show() {
        open(null);
    }

    public void show(Transaction transactionToEdit) {
        open(transactionToEdit);
    }

    private void open(Transaction transactionToEdit) {
        form = new FormTransaction(this::hide, transactionToEdit);

        StackPane.setAlignment(form, Pos.TOP_RIGHT);
        StackPane.setMargin(form, new Insets(90, 20, 20, 20));

        getChildren().addAll(background, form);
        setMouseTransparent(false);

        animateIn();
    }

    private void hide() {
        getChildren().clear();
        form = null;
        setMouseTransparent(true);
    }

    private void animateIn() {

        form.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(200), form);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(200), form);
        slide.setFromY(-10);
        slide.setToY(0);

        fade.play();
        slide.play();
    }
}