package model.dto;

import java.math.BigDecimal;

public class CategoryChartData {
    private final String categoryName;
    private final String categoryColor;
    private final BigDecimal totalValue;

    public CategoryChartData(String categoryName, String categoryColor, BigDecimal totalValue) {
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.totalValue = totalValue;
    }

    public String getCategoryName() { return categoryName; }
    public String getCategoryColor() { return categoryColor; }
    public BigDecimal getTotalValue() { return totalValue; }
}