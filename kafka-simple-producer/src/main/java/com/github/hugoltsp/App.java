package com.github.hugoltsp;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Stream;

@AllArgsConstructor
@SpringBootApplication
public class App implements CommandLineRunner {

    private final KafkaProducer kafkaProducer;

    public static void main(String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        Stream.generate(this::generateData)
                .limit(10)
                .forEach(character ->
                        kafkaProducer.send("CHARACTERS",
                                character.getUniverse(),
                                character));

        Stream.generate(new Faker()::esports)
                .limit(100)
                .map(Game::new)
                .forEach(game ->
                        kafkaProducer.send("E-SPORTS", game)
                );

    }

    private Character generateData() {
        return RandomUtils.nextBoolean() ? Character.randomGameOfThrones() : Character.randomLordOfTheRings();
    }

}
