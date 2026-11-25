package com.riu.backend.challenge.adapter.out.queue;

import com.riu.backend.challenge.domain.exception.QueueUnavailableException;
import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.port.out.SearchPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPublisher implements SearchPublisher {
    private final KafkaTemplate<String, Search> kafkaTemplate;
    @Value("${riu.kafka.searches-topic}")
    private String topic;

    @Override
    public void publish(Search search) {
        try {
            kafkaTemplate.send(topic, search.id(), search).join();
        } catch (RuntimeException e) {
            log.error("A search {} could not be published to topic {}.", search.id(), topic, e);
            throw new QueueUnavailableException();
        }
        log.info("A search with id: {} published to topic {}.", search.id(), topic);
    }
}
