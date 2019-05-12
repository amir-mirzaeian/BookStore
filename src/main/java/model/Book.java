package model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Book {

    /**
     * Book title
     */
    private String title;
    /**
     * Book author
     */
    private String author;
    /**
     * Book price
     */
    private BigDecimal price;

    /**
     * Constructor to create a book object
     * @param title
     * @param author
     * @param price
     */
    public Book(String title, String author, BigDecimal price) {

        this.title = title;
        this.author = author;
        this.price = price;
    }

    /**
     * Create a string for object representation
     * @return
     */
    @Override
    public String toString() {
        return title  + ", " + author + "," + price;
    }
}
