package model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class BookOperationTest {

    BookOperation bookOperation;

    public BookOperationTest() {
        bookOperation = new BookOperation();
    }

    @Test
    public void checkAddBookToStore() {
        String title = "Basics of Data Structure";
        String author = "Padma Reddy";
        BigDecimal price = new BigDecimal(75);
        int Quantity = 5;
        Book book = new Book(title, author, price);
        this.bookOperation.addToBookStore(book, Quantity);

        //Searching for the added book by string
        Map<Book, Integer> bookMap = this.bookOperation.search(1, "Basics of Data Structure");
        Set<Book> bookSet = bookMap.keySet();
        Book targetBook = null;
        for (Book theBook : bookSet) targetBook = theBook;
        Assert.assertEquals("Basics of Data Structure", targetBook.getTitle());
    }

    @Test
    public void checkSearch() {
        String title = "title sample";
        String title2 = "Random Sales";
        Assert.assertEquals(null, this.bookOperation.search(1, title));
        Map<Book, Integer> bookMap = this.bookOperation.search(1, "Random Sales");
        Set<Book> bookSet = bookMap.keySet();
        Book targetBook = null;
        for (Book theBook : bookSet) targetBook = theBook;
        Assert.assertEquals("Random Sales", targetBook.getTitle());
    }
}