package com.globalbooks.orders;

/**
 * POJO representing an Order Item.
 */
public class OrderItem {
    private int bookId;
    private String bookTitle;
    private int quantity;
    private double price;

    // Constructors
    public OrderItem() {}

    public OrderItem(int bookId, String bookTitle, int quantity, double price) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}