package com.riu.backend.challenge.integration.search;

import com.riu.backend.challenge.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.json.JsonCompareMode;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CountSearchIT extends IntegrationTestBase {
    private static final LocalDate CHECK_IN = LocalDate.of(2023, 12, 29);
    private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);

    @Test
    void givenStoredSearches_whenCountIsRequested_thenCountsOnlyIdenticalSearches() throws Exception {
        insertSearch("1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");
        insertSearch("5678xYz", CHECK_IN, CHECK_OUT, "30,29,1,3");
        String searchId = insertSearch("1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");
        insertSearch("1234aBc", CHECK_IN, LocalDate.of(2024, 1, 3), "30,29");
        insertSearch("1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");

        mockMvc.perform(get("/count").param("searchId", searchId))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "searchId": "%s",
                          "count": 3,
                          "search": {
                            "hotelId": "1234aBc",
                            "checkIn": "29/12/2023",
                            "checkOut": "31/12/2023",
                            "ages": [30, 29, 1, 3]
                          }
                        }
                        """.formatted(searchId), JsonCompareMode.STRICT));
    }

    @Test
    void givenAnUnknownSearchId_whenCountIsRequested_thenReturnsNotFound() throws Exception {
        String searchId = UUID.randomUUID().toString();

        mockMvc.perform(get("/count").param("searchId", searchId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Search not found: " + searchId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "3f2a1c9e5b7d4e8fa0b1c2d3e4f5a6b7"})
    void givenAnInvalidSearchId_whenCountIsRequested_thenReturnsBadRequest(String searchId) throws Exception {
        mockMvc.perform(get("/count").param("searchId", searchId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request."))
                .andExpect(jsonPath("$.invalidParams", hasSize(1)))
                .andExpect(jsonPath("$.invalidParams[0].field").value("searchId"))
                .andExpect(jsonPath("$.invalidParams[0].message").value("must be a valid UUID"));
    }

    @Test
    void givenNoSearchId_whenCountIsRequested_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/count"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request."))
                .andExpect(jsonPath("$.invalidParams", hasSize(1)))
                .andExpect(jsonPath("$.invalidParams[0].field").value("searchId"))
                .andExpect(jsonPath("$.invalidParams[0].message").value("must not be null"));
    }

    @Test
    void givenAnEmptySearchId_whenCountIsRequested_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/count").param("searchId", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid request."))
                .andExpect(jsonPath("$.invalidParams", hasSize(1)))
                .andExpect(jsonPath("$.invalidParams[0].field").value("searchId"))
                .andExpect(jsonPath("$.invalidParams[0].message").value("must not be null"));
    }

    private String insertSearch(String hotelId, LocalDate checkIn, LocalDate checkOut, String ages) {
        String id = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "insert into searches (id, hotel_id, check_in, check_out, ages) values (?, ?, ?, ?, ?)",
                id, hotelId, checkIn, checkOut, ages);
        return id;
    }
}
