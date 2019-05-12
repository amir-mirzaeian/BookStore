package model;

import database.DatabaseManagement;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class BookOperation implements IBook {

    /**
     * Hashmap contains of books and its quantity
     */
    private Map<Book, Integer> bookList = new HashMap<>();

    /**
     * The total price of the basket
     */
    private BigDecimal totalPrice = new BigDecimal(0);

    /**
     * The database page of the book
     */
    public static final String TABLE_BOOK = "book";

    /**
     * the database column for book title
     */
    public static final String COLUMN_TITLE = "title";
    /**
     * the database column for book author
     */
    public static final String COLUMN_AUTHOR = "author";
    /**
     * the database column for price
     */
    public static final String COLUMN_PRICE = "price";
    /**
     * the database column for quantity
     */
    public static final String COLUMN_QUANTITY = "quantity";
    /**
     * sort the books by its title
     */
    public static final int SORT_BY_TITLE = 1;
    /**
     * sort the books by its author
     */
    public static final int SORT_BY_AUTHOR = 2;
    /**
     * Sql statement constant to search by book's author
     */
    public static final String SEARCH_AUTHOR = "SELECT * FROM " + TABLE_BOOK + " WHERE UPPER(" + COLUMN_AUTHOR + " )= ?";
    /**
     * Sql statement constant to search by book;s title
     */
    public static final String SEARCH_TITLE = "SELECT * FROM " + TABLE_BOOK + " WHERE UPPER(" + COLUMN_TITLE + " )=?";
    /**
     * Sql statement constant to insert book to database
     */
    public static final String INSERT_BOOK = "INSERT INTO " + TABLE_BOOK + " VALUES( ?,?,?,?)";
    /**
     * Sql statement to remove book from the database
     */
    public static final String REMOVE_BOOk = "DELETE FROM " + TABLE_BOOK + " WHERE UPPER( " + COLUMN_TITLE + ")=? AND UPPER( " +
            COLUMN_AUTHOR + " )= ?  AND UPPER(" + COLUMN_PRICE + ") = ? AND " + COLUMN_QUANTITY + "=?";
    /**
     * Sql statement to show the candidate books to remove
     */
    public static final String SHOW_BOOK_TO_REMOVE = "SELECT * FROM " + TABLE_BOOK + " WHERE UPPER( " + COLUMN_TITLE + " )=? ";
    /**
     * search by title
     */
    public static final int SEARCH_BY_TITLE = 1;
    /**
     * search by author
     */
    public static final int SEARCH_BY_AUTHOR = 2;
    /**
     * database management instance(singleton)
     */

    DatabaseManagement databaseManagement = DatabaseManagement.getInstance();


    /**
     * Add the book to the database
     *
     * @param book     Book describes Book object
     * @param quantity is the quantity of the added book
     * @return true in case the book is added successfully
     */
    public boolean addToBookStore(Book book, int quantity) {
        try {


            databaseManagement.connect();
            PreparedStatement insertBook = databaseManagement.getConn().prepareStatement(INSERT_BOOK);
            insertBook.setString(1, book.getTitle());
            insertBook.setString(2, book.getAuthor());
            insertBook.setString(3, book.getPrice().toString());
            insertBook.setInt(4, quantity);

            int affectedRows = insertBook.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Cannot add the book.");
            } else {
                System.out.println(book.getTitle() + " has been added to the book store.");
                insertBook.close();
                databaseManagement.disconnect();
                return true;
            }
        } catch (Exception e) {
            System.out.println("Book can not be added");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handles buy query
     *
     * @param books as an array of books
     * @return 0 if the purchase status is successful, 1 if the book is not in stock and 2 if the book is not available
     */

    public int[] buy(Book... books) {

        int index = 0;
        BigDecimal[] prices = new BigDecimal[books.length];
        int[] buyStatus = new int[books.length];
        do {
            for (Book book : bookList.keySet()) {
                if (books[index] == book) {
                    if (bookList.get(book) > 0) {
                        buyStatus[index] = 0;// status = ok
                        bookList.replace(book, bookList.get(book) - 1);
                        prices[index] = book.getPrice();
                        break;
                    } else {
                        buyStatus[index] = 1; // status = not in stuck
                        prices[index] = new BigDecimal(0);
                        break;
                    }
                } else {
                    buyStatus[index] = 2; // status = does not exists
                    prices[index] = new BigDecimal(0);
                }
            }
            index++;
        } while (index < books.length);

        return buyStatus;
    }

    /**
     * Search throuh all the books in the database
     *
     * @param searchType      if searchType = 1,it searches by Title, otherwise by Author
     * @param searchingString the string which needs to be searched
     * @return the hashMap including the books to be searched and its quantity
     */
    public Map<Book, Integer> search(int searchType, String searchingString) {

        databaseManagement.connect();

        try {
            PreparedStatement searching;
            if (searchType == SEARCH_BY_AUTHOR) {
                searching = databaseManagement.getConn().prepareStatement(SEARCH_AUTHOR);
                System.out.println("Author");
            } else {
                searching = databaseManagement.getConn().prepareStatement(SEARCH_TITLE);
                System.out.println("Title");
            }
            searching.setString(1, searchingString.toUpperCase());
            ResultSet resultSet = searching.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Not found");
                return null;
            }
            Map<Book, Integer> b = showBooks(resultSet);
            for (Book book : b.keySet()) {
                System.out.println(book.getTitle() + "," + book.getAuthor() + "," + book.getPrice() + "," + b.get(book));
            }

            return b;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * It removes the book from the database
     *
     * @param book     the book which needs to be removed
     * @param quantity the quantity of the book which is sent by the program itself
     * @return true if the removing process is done successfully.
     */
    public boolean removeFromBookStore(Book book, int quantity) {

        try {
            databaseManagement.connect();
            PreparedStatement removeBook = databaseManagement.getConn().prepareStatement(REMOVE_BOOk);
            removeBook.setString(1, book.getTitle().toUpperCase());
            removeBook.setString(2, book.getAuthor().toUpperCase());
            removeBook.setString(3, book.getPrice().toString());
            removeBook.setInt(4, quantity);
//            removeBook.execute();
            int rowsAffected = removeBook.executeUpdate();
            if (rowsAffected != 1) {
                throw new SQLException("Problem with removing book.");
            } else {
                removeBook.close();
                databaseManagement.disconnect();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Problem with removing book from database.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Show all the books in the database
     *
     * @return a hashmap includes the book and its quantity
     */
    public Map<Book, Integer> showBooks() {


        try {
            databaseManagement.connect();
            Statement statement = databaseManagement.getConn().createStatement();
            statement.execute("SELECT * FROM " + TABLE_BOOK);
            ResultSet resultSet = statement.getResultSet();
            if (resultSet == null) {
                System.out.println("No book has added yet.");
                return null;
            } else {
                Map<Book, Integer> b = showBooks(resultSet);
                int i = 0;
                for (Book book : b.keySet()) {
                    System.out.println((i + 1) + "." + book.toString() + "," + b.get(book));
                    i++;
                }
                databaseManagement.disconnect();
                return b;
            }
        } catch (SQLException e) {
            System.out.println("Problem with Listing the books.");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Sort the book by the title or Author
     *
     * @param sortType if sortType = 1 , it sorts by by title, otherwise it sorts by author
     */
    public void sort(int sortType) {

        StringBuilder s = new StringBuilder("Select * From " + TABLE_BOOK + " ORDER BY ");

        try {
            databaseManagement.connect();
            Statement statement = databaseManagement.getConn().createStatement();
            if (sortType == SORT_BY_TITLE) {
                s.append(COLUMN_TITLE);
                s.append(" ASC");

            } else if (sortType == SORT_BY_AUTHOR) {
                s.append(COLUMN_AUTHOR);
                s.append(" ASC");
            }
            statement.execute(s.toString());
            ResultSet resultSet = statement.getResultSet();
            if (resultSet == null) System.out.println("No book added yet.");
            else {
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(COLUMN_TITLE) + "," + resultSet.getString(COLUMN_AUTHOR) +
                            "," + resultSet.getString(COLUMN_PRICE) + "," + resultSet.getString(COLUMN_QUANTITY));
                }
            }
            databaseManagement.disconnect();

        } catch (SQLException e) {
            System.out.println("Problem with sorting");
            e.printStackTrace();
            return;
        }
//        if (bookList.size() == 0) {
//            System.out.println("No book has added yet.");
//            return;
//        }
//        Comparator<Book> comparator = (b1, b2) -> b1.getAuthor().compareTo(b2.getAuthor());
//        showSortedBooks(comparator);
    }

    /**
     * The updates the basket price
     *
     * @param prices is sent as the book is added or removed to the basket
     * @param flag   if it is true means the book is added otherwise the book is removed
     * @return the total price of the basket
     */
    public String calculatePrice(BigDecimal prices, boolean flag) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        if (!flag) {
            totalPrice = totalPrice.subtract(prices);
        } else {
            totalPrice = totalPrice.add(prices);

        }

        return df.format(totalPrice);
    }

    /**
     * Shows the book
     *
     * @param resultSet
     * @return hashmap of books
     */
    public Map<Book, Integer> showBooks(ResultSet resultSet) {
        Map<Book, Integer> books = new HashMap<>();
        databaseManagement.connect();
        try {
            do {
                String title = resultSet.getString(COLUMN_TITLE);
                String author = resultSet.getString(COLUMN_AUTHOR);
                String price = resultSet.getString(COLUMN_PRICE);
                int quantity = resultSet.getInt(COLUMN_QUANTITY);
                BigDecimal pr = new BigDecimal(price);
                Book book = new Book(title, author, pr);
                books.put(book, quantity);
            } while (resultSet.next());
            return books;
        } catch (SQLException e) {
            System.out.println("Problem with showBooks()." + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a hashmap which includes all the Book in store found with the title
     *
     * @param title Book title is sent to the method
     * @return a map with all the books with the title
     */
    public Map<Book, Integer> showBookToDelete(String title) {
        try {
            databaseManagement.connect();
            PreparedStatement showBookToRemove = databaseManagement.getConn().prepareStatement(SHOW_BOOK_TO_REMOVE);
            showBookToRemove.setString(1, title.toUpperCase());
            ResultSet resultSet = showBookToRemove.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Not found");
                return null;
            }
            return showBooks(resultSet);
        } catch (SQLException e) {
            System.out.println("Problem with showBookToDelete()");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the basket for each purchase
     *
     * @param book is sent to be added or removed from the basket
     * @param flag if true means the book is added, otherwise the book is removed
     * @return true if the updating is successful
     * @throws SQLException
     */
    public boolean UpdateBasket(Book book, boolean flag) throws SQLException {

        databaseManagement.connect();
        Statement statement = databaseManagement.getConn().createStatement();
        statement.execute("Select * from " + TABLE_BOOK + " where " + COLUMN_TITLE + "='" + book.getTitle() +
                "' AND " + COLUMN_AUTHOR + "='" + book.getAuthor() + "' AND " + COLUMN_PRICE + "='" + book.getPrice()
                + "'");
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
            if (flag) {// in case the book is added to the basket, it decrease the quantity in database
                if (resultSet.getInt(COLUMN_QUANTITY) > 0) {
                    int updateQuantity = resultSet.getInt(COLUMN_QUANTITY) - 1;
                    statement.execute("UPDATE " + TABLE_BOOK + " SET " + COLUMN_QUANTITY + "='" + updateQuantity + "'" +
                            " WHERE " + COLUMN_QUANTITY + "='" + resultSet.getInt(COLUMN_QUANTITY) + "'");
                    databaseManagement.disconnect();
                    return true;
                } else {
                    databaseManagement.disconnect();
                    return false;
                }
            } else { // in case the book is removed from the basket, it increments the quantity in database again
                int updateQuantity = resultSet.getInt(COLUMN_QUANTITY) + 1;
                statement.execute("UPDATE " + TABLE_BOOK + " SET " + COLUMN_QUANTITY + "='" + updateQuantity + "'" +
                        " WHERE " + COLUMN_QUANTITY + "='" + resultSet.getInt(COLUMN_QUANTITY) + "'");
                databaseManagement.disconnect();
                return true;
            }
        } else {
            databaseManagement.disconnect();
            return false;
        }
    }
}
