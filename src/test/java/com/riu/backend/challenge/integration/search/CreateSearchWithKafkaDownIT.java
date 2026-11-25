package com.riu.backend.challenge.integration.search;

import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateSearchWithKafkaDownIT extends IntegrationTestBase {

    @MockitoBean
    private KafkaTemplate<String, Search> kafkaTemplate;

    @Test
    void givenKafkaIsDown_whenSearchIsPosted_thenReturnsServiceUnavailable() throws Exception {
        when(kafkaTemplate.send(anyString(), anyString(), any(Search.class)))
                .thenReturn(CompletableFuture.failedFuture(new KafkaException("Broker not available")));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "1234aBc",
                                  "checkIn": "2023-12-29",
                                  "checkOut": "2023-12-31",
                                  "ages": [30, 29, 1, 3]
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.detail").value("Queue message provider service unavailable."));
    }
}
