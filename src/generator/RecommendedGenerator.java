package generator;

import models.*;
import database.*;
import java.util.List;
import java.sql.SQLException;
import java.util.Iterator;


public class RecommendedGenerator implements Iterator<Movie> {
    private final MovieDao movieDao;
    private final int pageSize;
    private List<Movie> currentBatch;
    private int currentIndex = 0;
    private int currentOffset = 0;

    public RecommendedGenerator(MovieDao movieDao, int pageSize) {
        this.movieDao = movieDao;
        this.pageSize = pageSize;
        loadNextBatch();
    }

    public void loadNextBatch() {
        try {
            currentBatch = movieDao.getRecommendedMovies(pageSize, currentOffset);
            currentOffset += pageSize;
            currentIndex = 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load movies", e);
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Movie next() {
        if (currentBatch.isEmpty()) {
            loadNextBatch();
        }
        if (currentIndex >= currentBatch.size()) {
            loadNextBatch();
        }
        return currentBatch.get(currentIndex++);
    }
}