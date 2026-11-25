package com.riu.backend.challenge.adapter.in.rest.api;

import com.riu.backend.challenge.domain.model.Search;

import java.time.LocalDate;
import java.util.List;

public record SearchRequest(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {
    public static Search toDomain(SearchRequest searchRequest) {
        return new Search(null,
                searchRequest.hotelId,
                searchRequest.checkIn,
                searchRequest.checkOut,
                List.copyOf(searchRequest.ages));
    }
}
