package com.riu.backend.challenge.integration.search;

import com.jayway.jsonpath.JsonPath;
import com.riu.backend.challenge.adapter.out.persistence.repository.SearchJpaRepository;
import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import com.riu.backend.challenge.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreateSearchIT extends IntegrationTestBase {

    @Autowired
    private SearchJpaRepository searchRepository;

    @Test
    void givenAValidSearch_whenSearchIsPosted_thenItIsStored() throws Exception {
        String response = mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": "1234aBc",
                                  "checkIn": "2023-12-29",
                                  "checkOut": "2023-12-31",
                                  "ages": [30, 29, 1, 3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String searchId = JsonPath.read(response, "$.searchId");

        await().atMost(Duration.ofSeconds(30))
                .until(() -> searchRepository.existsById(searchId));

        SearchEntity expected = new SearchEntity(
                searchId,
                "1234aBc",
                LocalDate.of(2023, 12, 29),
                LocalDate.of(2023, 12, 31),
                "30,29,1,3");
        assertThat(searchRepository.findAll()).containsExactly(expected);
    }
}
