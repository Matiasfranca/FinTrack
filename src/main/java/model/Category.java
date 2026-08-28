package model;

public class Category {
    private Integer id;
    private String name;
    private String color;

    public Category(Integer id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }

    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
    
    @Override
    public String toString() {
        return name;
    }
}