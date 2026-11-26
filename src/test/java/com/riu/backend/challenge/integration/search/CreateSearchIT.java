package com.riu.backend.challenge.integration.search;

import com.jayway.jsonpath.JsonPath;
import com.riu.backend.challenge.adapter.out.persistence.repository.SearchJpaRepository;
import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import com.riu.backend.challenge.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
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
                                  "checkIn": "29/12/2023",
                                  "checkOut": "31/12/2023",
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

    @ParameterizedTest
    @MethodSource("invalidSearches")
    void givenAnInvalidSearch_whenSearchIsPosted_thenReturnsBadRequest(String body, String field, String message) throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request."))
                .andExpect(jsonPath("$.invalidParams", hasSize(1)))
                .andExpect(jsonPath("$.invalidParams[0].field").value(field))
                .andExpect(jsonPath("$.invalidParams[0].message").value(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-12-29", "29-12-2023"})
    void givenAMalformedDate_whenSearchIsPosted_thenReturnsBadRequest(String checkIn) throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("\"1234aBc\"", "\"" + checkIn + "\"", "\"31/12/2023\"", "[30, 29]")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Malformed request body."));
    }

    private static Stream<Arguments> invalidSearches() {
        return Stream.of(
                Arguments.of(body("null", "\"29/12/2023\"", "\"31/12/2023\"", "[30]"), "hotelId", "must not be null"),
                Arguments.of(body("\"1234-aBc\"", "\"29/12/2023\"", "\"31/12/2023\"", "[30]"), "hotelId", "must contain only letters and numbers"),
                Arguments.of(body("\"1234aBc\"", "null", "\"31/12/2023\"", "[30]"), "checkIn", "must not be null"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "null", "[30]"), "checkOut", "must not be null"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "\"29/12/2023\"", "[30]"), "checkOutAfterCheckIn", "checkOut must be after checkIn"),
                Arguments.of(body("\"1234aBc\"", "\"31/12/2023\"", "\"29/12/2023\"", "[30]"), "checkOutAfterCheckIn", "checkOut must be after checkIn"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "\"31/12/2023\"", "null"), "ages", "must not be empty"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "\"31/12/2023\"", "[]"), "ages", "must not be empty"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "\"31/12/2023\"", "[30, -1]"), "ages[1]", "must be zero or greater"),
                Arguments.of(body("\"1234aBc\"", "\"29/12/2023\"", "\"31/12/2023\"", "[30, null]"), "ages[1]", "must not be null")
        );
    }

    private static String body(String hotelId, String checkIn, String checkOut, String ages) {
        return """
                {
                  "hotelId": %s,
                  "checkIn": %s,
                  "checkOut": %s,
                  "ages": %s
                }
                """.formatted(hotelId, checkIn, checkOut, ages);
    }
}
