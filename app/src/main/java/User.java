package com.example.marinac.riddletheflag;
import com.example.marinac.riddletheflag.Flag;

import java.util.List;

public class User {

    public String name;
    public List<Flag> flags;
    public List<User> friends;
    public int level;
    public String picture;
    public String rank;

    public User(String name, int level, String picture, String rank) {
        this.name = name;
        this.level = level;
        this.picture = picture;
        this.rank = rank;
    }
}
