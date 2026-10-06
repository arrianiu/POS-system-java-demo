package src.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private int id;
    private double total;
    private LocalDateTime saleDate;
    private String cashier;

    // Optional but clean design
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {
        this.saleDate = LocalDateTime.now();
    }

    public Sale(String cashier) {
        this.cashier = cashier;
        this.saleDate = LocalDateTime.now();
    }

    // ===== LOGIC =====
    public void addItem(SaleItem item) {
        items.add(item);
        total += item.getSubtotal();
    }

    // ===== GETTERS & SETTERS =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public List<SaleItem> getItems() {
        return items;
    }
}
