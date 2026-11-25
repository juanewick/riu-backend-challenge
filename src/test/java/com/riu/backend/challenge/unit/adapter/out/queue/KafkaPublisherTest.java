package com.riu.backend.challenge.unit.adapter.out.queue;

import com.riu.backend.challenge.adapter.out.queue.KafkaPublisher;
import com.riu.backend.challenge.domain.exception.QueueUnavailableException;
import com.riu.backend.challenge.domain.model.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherTest {

    private static final String TOPIC = "searches";

    @Mock
    private KafkaTemplate<String, Search> kafkaTemplate;
    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaPublisher, "topic", TOPIC);
    }

    @Test
    public void givenASearch_whenPublishIsCalled_thenSendsItToTheTopic() {
        Search search = createSearch();
        when(kafkaTemplate.send(TOPIC, search.id(), search))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));

        kafkaPublisher.publish(search);

        verify(kafkaTemplate).send(TOPIC, search.id(), search);
    }

    @Test
    public void givenKafkaIsDown_whenPublishIsCalled_thenThrowsQueueUnavailable() {
        Search search = createSearch();
        when(kafkaTemplate.send(TOPIC, search.id(), search))
                .thenReturn(CompletableFuture.failedFuture(new KafkaException("Broker not available")));

        assertThatThrownBy(() -> kafkaPublisher.publish(search))
                .isInstanceOf(QueueUnavailableException.class)
                .hasMessage("Queue message provider service unavailable.");
    }

    private static Search createSearch() {
        return new Search(
                "someId",
                "someHotelId",
                LocalDate.of(2026, 12, 15),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );
    }

}
