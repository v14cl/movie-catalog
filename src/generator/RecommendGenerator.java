package generator;

import models.*;
import java.sql.*;
import java.util.Iterator;

public class RecommendGenerator implements Iterator<Movie>, AutoCloseable {
    private final PreparedStatement stmt;
    private final ResultSet resultSet;
    private boolean hasNext;

    public RecommendGenerator(PreparedStatement stmt, ResultSet resultSet) {
        this.stmt = stmt;
        this.resultSet = resultSet;
        try {
            this.hasNext = resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize iterator", e);
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Movie next() {
        try {
            Movie movie = new Movie(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                Genre.valueOf(resultSet.getString("genre")),
                resultSet.getDouble("rating"),
                resultSet.getInt("author_id")
            );
            hasNext = resultSet.next();
            return movie;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read next movie", e);
        }
    }

    @Override
    public void close() {
        try {
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
