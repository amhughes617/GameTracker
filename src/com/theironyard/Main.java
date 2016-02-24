package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
	// write your code here
        Spark.externalStaticFileLocation("Public");
        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    HashMap m = new HashMap();

                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = users.get(name);
                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    }
                    else {
                        return new ModelAndView(user, "home.html");
                    }
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
                    Game game = new Game(gameName, gameGenre, platform, gameYear);
                    users.get(user.name).games.add(game);
                    response.redirect("/");
                    return"";
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
}
