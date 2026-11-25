package com.riu.backend.challenge.integration.search;

import com.riu.backend.challenge.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.test.json.JsonCompareMode;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CountSearchIT extends IntegrationTestBase {
    private static final LocalDate CHECK_IN = LocalDate.of(2023, 12, 29);
    private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);

    @Test
    void givenStoredSearches_whenCountIsRequested_thenCountsOnlyIdenticalSearches() throws Exception {
        insertSearch("search1", "1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");
        insertSearch("search2", "5678xYz", CHECK_IN, CHECK_OUT, "30,29,1,3");
        insertSearch("search3", "1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");
        insertSearch("search4", "1234aBc", CHECK_IN, LocalDate.of(2024, 1, 3), "30,29");
        insertSearch("search5", "1234aBc", CHECK_IN, CHECK_OUT, "30,29,1,3");

        mockMvc.perform(get("/count").param("searchId", "search3"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "searchId": "search3",
                          "count": 3,
                          "search": {
                            "hotelId": "1234aBc",
                            "checkIn": "2023-12-29",
                            "checkOut": "2023-12-31",
                            "ages": [30, 29, 1, 3]
                          }
                        }
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void givenAnUnknownSearchId_whenCountIsRequested_thenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/count").param("searchId", "unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Search not found: unknown"));
    }

    private void insertSearch(String id, String hotelId, LocalDate checkIn, LocalDate checkOut, String ages) {
        jdbcTemplate.update(
                "insert into searches (id, hotel_id, check_in, check_out, ages) values (?, ?, ?, ?, ?)",
                id, hotelId, checkIn, checkOut, ages);
    }
}
