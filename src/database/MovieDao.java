package database;

import models.Movie;
import models.Genre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {
    private final Connection connection;

    private static final String INSERT_SQL = 
        "INSERT INTO movies (title, genre, rating, author_id) VALUES (?, ?, ?, ?)";
    private static final String SELECT_LAST_ID_SQL = "SELECT last_insert_rowid()";
    private static final String SELECT_BY_ID_SQL = 
        "SELECT m.*, a.name FROM movies m JOIN authors a ON m.author_id = a.id WHERE m.id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM movies";
    private static final String DELETE_ALL_SQL = "DELETE FROM movies";

    public MovieDao(Connection connection) {
        this.connection = connection;
    }

    public void insertMovie(Movie movie) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre().name());
            stmt.setDouble(3, movie.getRating());
            stmt.setInt(4, movie.getAuthorId());
            stmt.executeUpdate();

            try (Statement idStmt = connection.createStatement();
                 ResultSet rs = idStmt.executeQuery(SELECT_LAST_ID_SQL)) {
                if (rs.next()) {
                    movie.setId(rs.getInt(1));
                }
            }
        }
    }

    public Movie getById(int id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        Genre.valueOf(rs.getString("genre")),
                        rs.getDouble("rating"),
                        rs.getInt("author_id")
                    );
                }
            }
        }
        return null;
    }

    public List<Movie> getAll() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                movies.add(new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    Genre.valueOf(rs.getString("genre")),
                    rs.getDouble("rating"),
                    rs.getInt("author_id")
                ));
            }
        }
        return movies;
    }

    public void clearTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(DELETE_ALL_SQL);
        }
    }
}