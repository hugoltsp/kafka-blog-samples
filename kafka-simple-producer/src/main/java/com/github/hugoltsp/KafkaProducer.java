package com.github.hugoltsp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void send(String topic, String key, T payload) {
        kafkaTemplate.send(topic, key, payload).addCallback(this::onSuccess, this::onFailure);
    }

    public <T> void send(String topic, T payload) {
        kafkaTemplate.send(topic, payload).addCallback(this::onSuccess, this::onFailure);
    }

    private <T> void onSuccess(SendResult<String, T> result) {
        var recordMetadata = result.getRecordMetadata();
        var producerRecord = result.getProducerRecord();

        log.info("Successfully sent data: [{}] to topic: [{}] | partition: [{}] | offset: [{}]", producerRecord.value(),
                recordMetadata.topic(),
                recordMetadata.partition(),
                recordMetadata.offset());
    }

    private void onFailure(Throwable t) {
        log.error("Error", t);
    }

    @AllArgsConstructor
    private static class Config {

        private final KafkaSettings kafkaSettings;

        @Bean
        private KafkaTemplate<String, Object> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        private ProducerFactory<String, Object> producerFactory() {
            return new DefaultKafkaProducerFactory<>(configurationProperties());
        }

        private Map<String, Object> configurationProperties() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSettings.bootstrapServers());
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return properties;
        }

    }

}