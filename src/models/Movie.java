package models;

public class Movie {
    private Integer id;
    private String title;
    private Genre genre;
    private double rating;
    private int authorId;

    public Movie(Integer id, String title, Genre genre, double rating, int authorId) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.rating = rating;
        this.authorId = authorId;
    }

    public Movie(String title, Genre genre, double rating, int authorId) {
        this(null, title, genre, rating, authorId);
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "Movie id= " + id + ", title= " + title +
               ", genre= " + genre + ", rating= " + rating +
               ", authorId= " + authorId + ".";
    }
}
