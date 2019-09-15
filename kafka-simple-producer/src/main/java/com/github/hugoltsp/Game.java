package com.github.hugoltsp;

import com.github.javafaker.Esports;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Game {

    private final String team;
    private final String player;
    private final String gameName;

    public Game(Esports esports) {
        this.gameName = esports.game();
        this.player = esports.player();
        this.team = esports.team();
    }

}
