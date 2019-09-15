package com.github.hugoltsp;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Character {

    private final String universe;
    private final String character;
    private final String favoriteBeer;

    public static Character randomLordOfTheRings() {
        var faker = new Faker();
        return new Character("Lord of The Rings", faker.lordOfTheRings().character(), faker.beer().name());
    }

    public static Character randomGameOfThrones() {
        var faker = new Faker();
        return new Character("Game of Thrones", faker.gameOfThrones().character(), faker.beer().name());
    }

}
