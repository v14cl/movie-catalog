package memoization;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import dao.MovieDao;
import models.Movie;

public class AuthorMoviesCountMemoizer {
    private final Map<Integer, Integer> cache = new HashMap<>();
    private final MovieDao movieDao;

    public AuthorMoviesCountMemoizer(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    public int getCount(int authorId) {
        if (cache.containsKey(authorId)) {
            return cache.get(authorId);
        }

        int count = 0;
        try {
            List<Movie> movies = movieDao.getAll();
            for (Movie movie : movies) {
                if (movie.getAuthorId() == authorId) {
                    count++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("error while counting", e);
        }

        cache.put(authorId, count);
        return count;
    }

    public void clearCacheForAuthor(int authorId) {
        cache.remove(authorId);
    }

    public void clearCache() {
        cache.clear();
    }
}
