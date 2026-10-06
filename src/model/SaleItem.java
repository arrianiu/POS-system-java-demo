package src.model;

public class SaleItem {

    private int productId;
    private String productName;
    private int quantity;
    private double price;

    public SaleItem() {
    }

    public SaleItem(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // ===== DERIVED VALUE =====
    public double getSubtotal() {
        return quantity * price;
    }

    // ===== GETTERS & SETTERS =====
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
