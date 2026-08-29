package model;

public class BankAccount {
    private Integer id;
    private String name;
    private BankAccountType type;
    private boolean isActive;

    public BankAccount(Integer id, String name, BankAccountType type, boolean isActive) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isActive = isActive;
    }

    public BankAccount(String name, BankAccountType type) {
        this.name = name;
        this.type = type;
        this.isActive = true;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public BankAccountType getType() { return type; }
    public boolean isActive() { return isActive; }
    
    @Override
    public String toString() {
        return name;
    }
}