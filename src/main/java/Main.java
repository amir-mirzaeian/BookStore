import model.Book;
import model.BookOperation;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class Main {
    /**
     * To get the input from the user
     */
    static Scanner scanner = new Scanner(System.in);
    /**
     * Create an instance of BookOperation class
     */
    static BookOperation bookOperation = new BookOperation();
    /**
     * Intended books to buy
     */
    static List<Book> booksToBuy = new ArrayList<>();
    //private static String fileName = "/Programming/Java/IntelliJ/Maven/BookStore/book.txt";

    /**
     * Main Method
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {

//        Path path = Paths.get(fileName);
//        BufferedReader br = Files.newBufferedReader(path);
//
//        String input;
//        while ((input = br.readLine()) !=null) {
//            String[] itemPiece = input.split(";");
//
//            String title = itemPiece[0];
//            String author = itemPiece[1];
//            BigDecimal price = new BigDecimal(itemPiece[2]);
//            String quantity = itemPiece[3];
//            Book book = new Book(title,author,price);
//            bookOperation.addToBookStore(book,Integer.parseInt(quantity));
//        }

        System.out.println("Welcome to Book Store:");

        showMenu();

        while (true) {

            int opt = 0;
            try {
                opt = scanner.nextInt();
                if (opt < 1 || opt > 9) {
                    System.out.println("You need to press 1 to 9.");
                    showMenu();
                }
            } catch (InputMismatchException e) {
                System.err.println("Wrong Input. The program will be terminated now.");
                System.exit(-1);
                showMenu();
            }
            switch (opt) {
                case 1: {
                    addBook();
                    break;
                }
                case 2:
                    removeBook();
                    break;
                case 3:
                    String[] s = search();
                    int searchType = Integer.parseInt(s[0]);
                    String searchingString = s[1];
                    bookOperation.search(searchType, searchingString);
                    showMenu();
                    break;
                case 4:
                    bookOperation.showBooks();
                    showMenu();
                    break;
                case 5:
                    bookOperation.sort(sort());
                    showMenu();
                    break;
                case 6:
                    showBasket();
                    showMenu();
                    break;
                case 7:
                    removeFromBasket();
                    showMenu();
                    break;
                case 8:
                    addBookToBasket();
                    showMenu();
                    break;
                case 9:
                    System.out.println("Thanks for using the app.");
                    System.exit(-1);
            }
        }
    }

    /**
     * Show the menu on console
     */

    public static void showMenu() {
        System.out.println("Press:\n" +
                "1)To add book into your bookstore\n" +
                "2)To remove a book from your bookstore\n" +
                "3)To search \n" +
                "4)To list all the books in this bookstore\n" +
                "5)To sort all books by title or author\n" +
                "6)To show basket\n" +
                "7)To remove from basket\n" +
                "8)To add book to basket\n" +
                "9)To Exit");
        System.out.println("===========================================");

        System.out.println("Your option:");

    }

    /**
     * To add books
     */
    public static void addBook() {
        try {
            scanner.nextLine();
            System.out.println("Book title:");
            String title = scanner.nextLine();
            System.out.println("Book Author:");
            String author = scanner.nextLine();
            System.out.println("Book price:");
            BigDecimal price = scanner.nextBigDecimal();
            System.out.println("Quantity:");
            int quantity = scanner.nextInt();
            Book book = new Book(title, author, price);
            if (bookOperation.addToBookStore(book, quantity))
                System.out.println(book.toString() + " has been added.");
            else System.out.println("The book cannot be added.Please try again...");
        } catch (InputMismatchException e) {
            System.out.println("Wrong input." + e.getMessage());
            showMenu();
            return;
        } finally {
            showMenu();
        }
    }

    /**
     * To remove books
     */
    public static void removeBook() {

        System.out.println("Now Please Enter the book title you need to remove:");
        scanner.nextLine();
        String title = scanner.nextLine();
        Map<Book, Integer> mapList = bookOperation.showBookToDelete(title);
        if (mapList == null) {
            showMenu();
            return;
        } else {
            Book[] books = new Book[mapList.size()];
            int[] quantity = new int[mapList.size()];
            int i = 0;
            for (Book book : mapList.keySet()) {
                books[i] = book;
                quantity[i] = mapList.get(book);
                System.out.println((i + 1) + "." + book.toString() + "," + mapList.get(book));
                i++;
            }

            System.out.println("Now please select:");
            try {
                int opt = scanner.nextInt();
                opt--;
                if (bookOperation.removeFromBookStore(books[opt], quantity[opt])) {
                    System.out.println(title + " has been removed.");
                    showMenu();
                } else {
                    System.err.println("Not such a book with mentioned Author.Please try again.");
                    showMenu();
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong input.");
                showMenu();
                return;
            }

        }
    }

    /**
     * To search for a book
     * @return
     */
    public static String[] search() {
        String[] s = new String[2];
        try {
            System.out.println("Press 1 to search by Book Title\n" +
                    "Press 2 to search by Book Author\n");
            int opt = scanner.nextInt();
            s[0] = Integer.toString(opt);
            if (opt < 1 || opt > 2) System.out.println("Wrong number has been typed. The default sort is Title.");
            System.out.println("Enter searching String:");
            scanner.nextLine();
            String searchingString = scanner.nextLine();
            s[1] = searchingString;
            return s;
        } catch (InputMismatchException e){
            System.out.println("Wrong input. The program will be terminated now.");
            return null;
        }
    }

    /**
     * To add book to the basket
     * @throws SQLException
     */
    public static void addBookToBasket() throws Exception {

        System.out.println("This is the list of Books:");
        List<Book> bookList = new ArrayList<>();
        for (Book book : bookOperation.showBooks().keySet()) {
            bookList.add(book);
        }
        System.out.println("Now you can choose which book to add to your basket:");
        try {
            int opt = scanner.nextInt();
            if (bookOperation.UpdateBasket(bookList.get(opt - 1), true)) {
                booksToBuy.add(bookList.get(opt - 1));

                System.out.println("Successfully added to basket. Total of Basket Price:" +
                        bookOperation.calculatePrice(bookList.get(opt - 1).getPrice(), true));
            } else {
                System.out.println("Not available in stock.");
            }
        } catch (InputMismatchException e)
        {
            System.out.println("Wrong input. the program will be terminated now.");
        }

    }

    /**
     * To sort the basket
     * @return
     */
    public static int sort() {
        try {
            System.out.println("Press 1 to sort by Book Title\n" +
                    "Press 2 to sort by Book Author");
            int op = scanner.nextInt();
            if (op < 1 || op > 2) {
                System.out.println("Wrong number(" + op +") has been typed. The default sort is author.");
                op = 2;
            }
            return op;
        } catch (InputMismatchException e){
            System.out.println("Wrong input.You need to insert a number.The default sort is author:");
            return 2;
        }
    }

    /**
     * To show the basket
     * @return
     */
    public static boolean  showBasket() {
        if (booksToBuy.size() == 0) {
            System.err.println("No book in basket. You need to add a book to basket.\n");
            return false;
        }
        else {
            int i = 0;
            for (Book book : booksToBuy) {
                System.out.println((i+1) + "." + book.getTitle() + "," + book.getAuthor() + "," + book.getPrice());
                i++;
            }
            return true;
        }
    }

    /**
     * To remove from the basket
     * @throws SQLException
     */
    public static void removeFromBasket() throws SQLException {
        if (!showBasket()) return;
        System.out.println("What to remove? select it please.");
        try {
            int opt = scanner.nextInt();
            bookOperation.UpdateBasket(booksToBuy.get(opt - 1), false);
            bookOperation.calculatePrice(booksToBuy.get(opt - 1).getPrice(), false);
            booksToBuy.remove(opt - 1);
            System.out.println("The Book has been removed.");
        } catch (InputMismatchException e){
            System.out.println("Wrong input. The program will be terminated now.");
        }

    }
}
