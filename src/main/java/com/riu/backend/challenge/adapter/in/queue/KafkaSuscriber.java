package com.riu.backend.challenge.adapter.in.queue;

import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.port.in.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaSuscriber {

    private final SearchService searchService;

    @KafkaListener(topics = "${riu.kafka.searches-topic}")
    public void onSearch(Search search) {
        log.info("A search {} received from Kafka.", search.id());
        searchService.create(search);
    }
}
