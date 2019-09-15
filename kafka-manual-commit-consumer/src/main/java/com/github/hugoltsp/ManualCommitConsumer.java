package com.github.hugoltsp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL;

@Slf4j
@Component
public class ManualCommitConsumer implements AcknowledgingMessageListener<String, String> {

    @Override
    @KafkaListener(topics = "E-SPORTS")
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        var game = data.value();

        if (shouldAcknowledge(game)) {
            log.info(game);
            acknowledgment.acknowledge();
        } else {
            log.info("Not acknowledged: {}", game);
        }

    }

    private boolean shouldAcknowledge(String game) {
        return !game.contains("League of Legends");
    }

    @AllArgsConstructor
    private static class Config {

        private final KafkaSettings kafkaSettings;

        @Bean
        private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            factory.setMessageConverter(new StringJsonMessageConverter());
            factory.setConcurrency(2);
            factory.getContainerProperties().setAckMode(MANUAL);
            return factory;
        }

        private Map<String, Object> consumerConfigs() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSettings.bootstrapServers());
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return properties;
        }

        private ConsumerFactory<String, String> consumerFactory() {
            return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new StringDeserializer());
        }

    }
}
