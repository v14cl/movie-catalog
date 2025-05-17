package database;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.sql.*;
import models.*;

public class DBLoader {

    public static void loadDatabase(String jsonFilePath) throws Exception {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:movies_catalog.db");
            conn.setAutoCommit(false);

            AuthorDao authorDao = new AuthorDao(conn);
            MovieDao movieDao = new MovieDao(conn);


            authorDao.clearTable();
            movieDao.clearTable();

            JsonArray moviesArray = JsonParser
            .parseReader(new FileReader(jsonFilePath)).getAsJsonArray();

            for (var element : moviesArray) {
                JsonObject movie = element.getAsJsonObject();
                String title = movie.get("title").getAsString();
                String genre = movie.get("genre").getAsString();
                double rating = movie.get("rating").getAsDouble();
                String authorName = movie.get("author").getAsString();


                Author author = authorDao.getByName(authorName);
                if (author == null) {
                    author = new Author(authorName);
                    authorDao.insertAuthor(author);
                }


                Movie newMovie = new Movie(title, Genre.valueOf(genre), rating, author.getId());
                movieDao.insertMovie(newMovie);
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new Exception(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}