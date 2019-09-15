package com.github.hugoltsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaSettings {

    private final List<String> bootstrapServers;

    public String bootstrapServers() {
        return String.join(",", getBootstrapServers());
    }

}