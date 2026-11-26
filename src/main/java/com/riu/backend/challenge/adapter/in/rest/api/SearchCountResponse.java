package com.riu.backend.challenge.adapter.in.rest.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.model.SearchCount;

import java.time.LocalDate;
import java.util.List;

public record SearchCountResponse (
        String searchId,
        Long count,
        SearchResponse search
) {
    public record SearchResponse(
        String hotelId,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,
        List<Integer> ages
    ){
        public static SearchResponse fromDomain(Search search) {
            return new SearchResponse(
                    search.hotelId(),
                    search.checkIn(),
                    search.checkOut(),
                    List.copyOf(search.ages())
            );
        }
    }

    public static SearchCountResponse fromDomain(SearchCount searchCount) {
        return new SearchCountResponse(
                searchCount.searchId(),
                searchCount.count(),
                SearchResponse.fromDomain(searchCount.search()));
    }
}

