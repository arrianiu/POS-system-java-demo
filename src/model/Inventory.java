package src.model;

import java.time.LocalDateTime;

public class Inventory {

    private int id;
    private int productId;
    private int changeQty;
    private String action;
    private LocalDateTime logDate;

    public Inventory() {
    }

    public Inventory(int productId, int changeQty, String action) {
        this.productId = productId;
        this.changeQty = changeQty;
        this.action = action;
        this.logDate = LocalDateTime.now();
    }

    // ===== GETTERS & SETTERS =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getChangeQty() {
        return changeQty;
    }

    public void setChangeQty(int changeQty) {
        this.changeQty = changeQty;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDateTime logDate) {
        this.logDate = logDate;
    }
}
