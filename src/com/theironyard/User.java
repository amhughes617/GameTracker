package com.theironyard;

import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 * Created by alexanderhughes on 2/23/16.
 */
public class User {
    String name;
    ArrayList<Game> games = new ArrayList<>();

    public User(String name) {
        this.name = name;
    }
}
