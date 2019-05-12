package model;

import java.math.BigDecimal;
import java.util.Map;

public interface IBook {


    /**
     * Add book to Store
     * @param book
     * @param quantity
     * @return
     */
    boolean addToBookStore(Book book, int quantity);

    /**
     * Show books
     * @return
     */
    Map<Book,Integer> showBooks();

    /**
     * Search for books
     * @param searchType
     * @param searchingString
     * @return
     */

    Map<Book, Integer> search(int searchType, String searchingString); //search type: 1 -> search Title, 2->search author

    /**
     * Sort books
     * @param sortType
     */

    void sort(int sortType); // 1->sort by title, 2-> sort by author

    /**
     * Calculate the price
     * @param prices
     * @param flag
     * @return
     */

    String calculatePrice(BigDecimal prices, boolean flag);

    /**
     * Remove books from the database
     * @param book
     * @param quantity
     * @return
     */

    boolean removeFromBookStore(Book book, int quantity);

    /**
     * Update the basket
     * @param book
     * @param flag
     * @return
     * @throws Exception
     */

    boolean UpdateBasket(Book book, boolean flag) throws Exception;
}
