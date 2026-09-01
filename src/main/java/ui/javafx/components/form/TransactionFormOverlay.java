package ui.javafx.components.form;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;

public class TransactionFormOverlay extends StackPane {

    private final Rectangle background = new Rectangle();
    private FormTransaction form;

    public TransactionFormOverlay() {

        // Enquanto fechado, essa camada não intercepta NENHUM clique —
        // o dashboard por baixo continua 100% utilizável.
        setPickOnBounds(false);
        setMouseTransparent(true);

        background.setFill(Color.rgb(0, 0, 0, 0.35));
        background.setOnMouseClicked(e -> hide());
    }

    public void show() {

        form = new FormTransaction(this::hide);

        background.widthProperty().bind(widthProperty());
        background.heightProperty().bind(heightProperty());

        StackPane.setAlignment(form, Pos.TOP_RIGHT);
        StackPane.setMargin(form, new Insets(90, 20, 20, 20));

        getChildren().addAll(background, form);
        setMouseTransparent(false);

        animateIn();
    }

    private void hide() {
        getChildren().removeAll(background, form);
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