package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:movies_catalog.db");
            Statement stmt = conn.createStatement()) {

            String createAuthors = """
                CREATE TABLE IF NOT EXISTS authors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                );
            """;

            String createMovies = """
                CREATE TABLE IF NOT EXISTS movies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    genre TEXT,
                    rating DECIMAL(3,1) CHECK (rating BETWEEN 0 AND 10),
                    author_id INTEGER NOT NULL,
                    FOREIGN KEY (author_id) REFERENCES authors(id)
                );
            """;

            stmt.execute(createAuthors);
            stmt.execute(createMovies);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
