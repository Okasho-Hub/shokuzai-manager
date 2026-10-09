package servlet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Item {
    private int id;
    private int quantity;
    private String name;
    private String category;
    private String barcode;
    private String expiryDate;

    // 期限切れかどうかを判定するメソッド
    public boolean isExpired() {
        if (expiryDate == null || expiryDate.isEmpty()) return false;
        LocalDate target = LocalDate.parse(expiryDate);
        LocalDate today = LocalDate.now();
        return target.isBefore(today);
    }

    // 賞味期限があと3日以内かどうかを判定するメソッド
    public boolean isExpiringSoon() {
        if (expiryDate == null || expiryDate.isEmpty()) return false;
        LocalDate target = LocalDate.parse(expiryDate);
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, target);
        return daysBetween >= 0 && daysBetween <= 3;
    }

    // ゲッターとセッター
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}