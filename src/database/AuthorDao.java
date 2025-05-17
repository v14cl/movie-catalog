package database;

import models.Author;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class AuthorDao {
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO authors (name) VALUES (?)";
    private static final String SELECT_LAST_ID_SQL = "SELECT last_insert_rowid()";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM authors WHERE id = ?";
    private static final String SELECT_BY_NAME_SQL = "SELECT * FROM authors WHERE name = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM authors";
    private static final String DELETE_ALL_SQL = "DELETE FROM authors";

    public AuthorDao(Connection connection) {
        this.connection = connection;
    }

    public void insertAuthor(Author author) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, author.getName());
            stmt.executeUpdate();

            try (Statement idStmt = connection.createStatement();
                ResultSet rs = idStmt.executeQuery(SELECT_LAST_ID_SQL)) {
                if (rs.next()) {
                    author.setId(rs.getInt(1));
                }
            }
        }
    }

    public Author getById(int id) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToAuthor(rs) : null;
            }
        }
    }

    public Author getByName(String name) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_BY_NAME_SQL)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToAuthor(rs) : null;
            }
        }
    }

    public List<Author> getAll() throws SQLException {
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                authors.add(mapResultSetToAuthor(rs));
            }
        }
        return authors;
    }

    public void clearTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(DELETE_ALL_SQL);
        }
    }

    private Author mapResultSetToAuthor(ResultSet rs) throws SQLException {
        return new Author(rs.getInt("id"), rs.getString("name"));
    }
}