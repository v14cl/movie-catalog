import models.Movie;
import java.util.*;

public class WatchLaterQueue {
    final LinkedList<Movie> movies = new LinkedList<>();
    private Mode mode = Mode.FIFO;

    public enum Mode {
        FIFO,
        LIFO,
        HIGHEST_RATING,
        LOWEST_RATING
    }

    public enum DequeuePriority {
        HIGHEST_RATING,
        LOWEST_RATING,
        OLDEST,
        NEWEST
    }

    public void enqueue(Movie movie) {
        movies.add(movie);
        reorder(this.mode);
    }

    public Movie dequeue(DequeuePriority priority) {
        if (movies.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }

        switch (priority) {
            case HIGHEST_RATING:
                return removeHighestRated();
            case LOWEST_RATING:
                return removeLowestRated();
            case NEWEST:
                return movies.removeLast();
            default:
                return movies.removeFirst();
        }
    }

    public Movie peek(DequeuePriority priority) {
        if (movies.isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }

        switch (priority) {
            case HIGHEST_RATING:
                return getHighestRated();
            case LOWEST_RATING:
                return getLowestRated();
            case NEWEST:
                return movies.getLast();
            default:
                return movies.getFirst();
        }
    }

    public void reorder(Mode newMode) {
        this.mode = newMode;
        switch (newMode) {
            case FIFO:
                break;
            case LIFO:
                Collections.reverse(movies);
                break;
            case HIGHEST_RATING:
                movies.sort(Comparator.comparingDouble(Movie::getRating).reversed());
                break;
            case LOWEST_RATING:
                movies.sort(Comparator.comparingDouble(Movie::getRating));
                break;
        }
    }

    public void removeWatched(Movie movie) {
        movies.remove(movie);
    }

    private Movie removeHighestRated() {
        Movie highest = getHighestRated();
        movies.remove(highest);
        return highest;
    }

    private Movie removeLowestRated() {
        Movie lowest = getLowestRated();
        movies.remove(lowest);
        return lowest;
    }

    private Movie getHighestRated() {
        return movies.stream()
                .max(Comparator.comparingDouble(Movie::getRating))
                .orElseThrow();
    }

    private Movie getLowestRated() {
        return movies.stream()
                .min(Comparator.comparingDouble(Movie::getRating))
                .orElseThrow();
    }

    public Mode getCurrentMode() {
        return mode;
    }

    public boolean isEmpty() {
        return movies.isEmpty();
    }

    public int size() {
        return movies.size();
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }
}