import dao.*;
import test.*;
import models.*;
import generator.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import models.Movie;
import dao.MovieDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        DBInitializer.initialize();
        DBLoader.loadDatabase("db_info.json");
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:movies_catalog.db")) {
            MovieDao movieDao = new MovieDao(conn);
            WatchLaterQueue queue = new WatchLaterQueue();

            List<Movie> allMovies = movieDao.getAll();
            Collections.shuffle(allMovies);

            int count = Math.min(5, allMovies.size());
            for (int i = 0; i < count; i++) {
                queue.enqueue(allMovies.get(i));
            }

            queue.reorder(WatchLaterQueue.Mode.FIFO);
            System.out.println("FIFO peek (oldest): " + queue.peek(WatchLaterQueue.DequeuePriority.OLDEST).getTitle());

            queue.reorder(WatchLaterQueue.Mode.LIFO);
            System.out.println("LIFO peek (newest): " + queue.peek(WatchLaterQueue.DequeuePriority.NEWEST).getTitle());

            Movie topRated = queue.dequeue(WatchLaterQueue.DequeuePriority.HIGHEST_RATING);
            System.out.println("Dequeued highest rated: " + topRated.getTitle());

            System.out.println("Remaining movies in queue:");
            for (Movie m : queue.getAllMovies()) {
                System.out.println(m.getTitle() + " (" + m.getRating() + ")");
            }
        }
        String url = "jdbc:sqlite:movies_catalog.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            MovieDao movieDao = new MovieDao(conn);
            try (RecommendGenerator generator = movieDao.streamRecommendedMovies()) {
                while (generator.hasNext()) {
                    ;
                    Movie movie = generator.next();
                    System.out.println(">> " + movie.getTitle());
                    Thread.sleep(300);
                }
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
