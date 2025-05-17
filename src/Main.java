import database.*;
import models.Author;
import models.Movie;
import models.Genre;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {


            DBInitializer.initialize();



            DBLoader.loadDatabase("src/db_info.json");


            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:movies_catalog.db")) {
                AuthorDao authorDao = new AuthorDao(conn);
                MovieDao movieDao = new MovieDao(conn);



                List<Author> authors = authorDao.getAll();
                authors.forEach(author -> System.out.println(
                    String.format("ID: %d, Имя: %s", author.getId(), author.getName())
                ));



                List<Movie> movies = movieDao.getAll();
                movies.forEach(movie -> System.out.println(
                    String.format("ID: %d, Название: %-25s, Жанр: %-10s, Рейтинг: %.1f, Автор ID: %d",
                        movie.getId(),
                        movie.getTitle(),
                        movie.getGenre(),
                        movie.getRating(),
                        movie.getAuthorId())
                ));


                System.out.println("\n=== Проверка поиска по ID ===");
                Movie foundMovie = movieDao.getById(5);
                if (foundMovie != null) {
                    System.out.println("Найден фильм с ID 5: " + foundMovie.getTitle());
                }


                System.out.println("\n=== Проверка поиска автора ===");
                Author nolan = authorDao.getByName("Christopher Nolan");
                if (nolan != null) {
                    System.out.println("Найден автор: " + nolan.getName() + " (ID: " + nolan.getId() + ")");
                    

                    System.out.println("\n=== Фильмы Кристофера Нолана ===");
                    movies.stream()
                        .filter(m -> m.getAuthorId() == nolan.getId())
                        .forEach(m -> System.out.println(
                            String.format("%s (%.1f)", m.getTitle(), m.getRating())
                        ));
                }


                System.out.println("\n=== Фильмы в жанре DRAMA ===");
                movies.stream()
                    .filter(m -> m.getGenre() == Genre.DRAMA)
                    .forEach(m -> System.out.println(m.getTitle()));


                System.out.println("\n=== Фильмы с рейтингом > 9.0 ===");
                movies.stream()
                    .filter(m -> m.getRating() > 9.0)
                    .forEach(m -> System.out.println(
                        String.format("%s (%.1f) - %s", 
                            m.getTitle(), 
                            m.getRating(), 
                            authors.stream()
                                .filter(a -> a.getId() == m.getAuthorId())
                                .findFirst()
                                .get()
                                .getName())
                    ));

            } catch (Exception e) {
                System.err.println("Ошибка при работе с базой данных:");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Критическая ошибка инициализации:");
            e.printStackTrace();
        }
    }
}