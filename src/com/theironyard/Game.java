package com.theironyard;

/**
 * Created by alexanderhughes on 2/23/16.
 */
public class Game {
    String name;
    String genre;
    String platform;
    int releaseYear;
    int id;

    public Game(int id, String name, String genre, String platform, int releaseYear) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.releaseYear = releaseYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
