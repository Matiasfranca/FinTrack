package ui.javafx.components.form;

import model.Category;

public record CategoryOption(Category category, String label) {
    @Override
    public String toString() {
        return label;
    }
}
