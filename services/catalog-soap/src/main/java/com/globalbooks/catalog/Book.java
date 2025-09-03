package com.globalbooks.catalog;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO representing a Book in the catalog.
 * Used for SOAP responses.
 */
@XmlRootElement
public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private String category;

    // Default constructor for JAXB
    public Book() {}

    public Book(int id, String title, String author, double price, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}