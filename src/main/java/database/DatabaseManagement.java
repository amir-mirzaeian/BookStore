package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManagement {

    public static final String DB_NAME = "book.db";
    public static final String DB_PATH = "jdbc:sqlite:/Programming/Java/IntelliJ/Maven/BookStore/src/main/resources/" + DB_NAME;

    public static DatabaseManagement dbInstance = new DatabaseManagement();
    public static Connection conn;

    private DatabaseManagement() {

    }

    public static DatabaseManagement getInstance() {
        return dbInstance;
    }

    public boolean connect(){
        try {

            conn = DriverManager.getConnection(DB_PATH);
            return true;
        } catch (SQLException e){
            System.out.println("Cannot connect to Database.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnect(){
        try {
            conn.close();
            return true;
        } catch (SQLException e){
            System.out.println("Cannot close the Database.");
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConn() {
        return conn;
    }
}
