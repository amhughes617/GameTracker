package com.theironyard;

import com.sun.javafx.geom.transform.Identity;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS games (id IDENTITY, name VARCHAR, genre VARCHAR, platform VARCHAR, release_year INT)");
	// write your code here
        Spark.externalStaticFileLocation("Public");
        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    ArrayList<Game> games = selectGames(conn);
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    m.put("games", games);
                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    else {
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/edit",
                ((request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));
                    Game game = selectGame(conn, id);
                    return new ModelAndView(game, "edit.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/create-user",
                ((request, response) -> {
                    String name = request.queryParams("loginName");
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name);
                        users.put(name, user);
                    }

                    Session session = request.session();
                    session.attribute("userName", name);

                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "create-game",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    if (user == null) {
                        //throw new Exception("User is not logged in");
                        Spark.halt("403");
                    }

                    String gameName = request.queryParams("gameName");
                    String gameGenre= request.queryParams("gameGenre");
                    String platform = request.queryParams("gamePlatform");
                    if (gameName == null || gameGenre == null || platform == null) {
                        throw new Exception("Didnt receive all queryparams");
                    }
                    int gameYear = Integer.valueOf(request.queryParams("gameYear"));
                    Game game = new Game(1, gameName, gameGenre, platform, gameYear);
                    insertGame(conn, game);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/delete",
                ((request, response) -> {
                    String idStr = request.queryParams("gameId");
                    int id = Integer.valueOf(idStr);
                    deleteGame(conn, id);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "edit",
                ((request, response) -> {
                    int id = Integer.valueOf(request.queryParams("id"));
                    Game game = selectGame(conn, id);
                    String name = request.queryParams("name");
                    String genre = request.queryParams("genre");
                    String platform = request.queryParams("platform");
                    String releaseYear = request.queryParams("releaseYear");
                    if (!name.isEmpty()) {
                        game.setName(name);
                    }
                    if (genre != null) {
                        game.setGenre(genre);
                    }
                    if (platform != null) {
                        game.setPlatform(platform);
                    }
                    if (!releaseYear.isEmpty()) {
                        game.setReleaseYear(Integer.valueOf(releaseYear));
                    }
                    updateGame(conn, game);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
    }
    static User getUserFromSession(Session session) {
        String name = session.attribute("userName");
        return users.get(name);
    }
    static void insertGame(Connection conn, Game game) throws SQLException {
        PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO games VALUES(NULL, ?, ?, ?, ?)");
        stmt2.setString(1, game.name);
        stmt2.setString(2, game.genre);
        stmt2.setString(3, game.platform);
        stmt2.setInt(4, game.releaseYear);
        stmt2.execute();
    }
    static void deleteGame(Connection conn, int id) throws SQLException {
        PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM games WHERE id = ?");
        stmt2.setInt(1, id);
        stmt2.execute();
    }
    static void updateGame(Connection conn, Game game) throws SQLException {
        PreparedStatement stmt2 = conn.prepareStatement("UPDATE games SET name = ?, genre = ?, platform = ?, release_year = ? WHERE id = ?");
        stmt2.setString(1, game.name);
        stmt2.setString(2, game.genre);
        stmt2.setString(3, game.platform);
        stmt2.setInt(4, game.releaseYear);
        stmt2.setInt(5, game.id);
        stmt2.execute();
    }
    static ArrayList<Game> selectGames(Connection conn) throws SQLException {
        Statement stmt2 = conn.createStatement();
        ResultSet results = stmt2.executeQuery("SELECT * FROM games");
        ArrayList<Game> games = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String genre = results.getString("genre");
            String platform = results.getString("platform");
            int releaseYear = results.getInt("release_year");
            Game game = new Game (id, name, genre, platform, releaseYear);
            games.add(game);
        }
        return games;
    }
    static Game selectGame(Connection conn, int id) throws SQLException {
        PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM games WHERE id = ?");
        stmt2.setInt(1, id);
        ResultSet results = stmt2.executeQuery();
        if (results.next()) {
            int gameId = results.getInt("id");
            String name = results.getString("name");
            String genre = results.getString("genre");
            String platform = results.getString("platform");
            int releaseYear = results.getInt("release_year");
            Game game = new Game(gameId, name, genre, platform, releaseYear);
            return game;
        }
        return null;
    }
}
