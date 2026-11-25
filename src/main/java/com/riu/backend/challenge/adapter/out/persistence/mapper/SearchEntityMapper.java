package com.riu.backend.challenge.adapter.out.persistence.mapper;

import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchEntityMapper {

    public SearchEntity toEntity(Search search) {
        SearchEntity entity = new SearchEntity();
        entity.setId(search.id());
        entity.setHotelId(search.hotelId());
        entity.setCheckIn(search.checkIn());
        entity.setCheckOut(search.checkOut());
        entity.setAges(toText(search.ages()));
        return entity;
    }

    public Search toDomain(SearchEntity entity) {
        return new Search(
                entity.getId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                toList(entity.getAges()));
    }

    private String toText(List<Integer> ages) {
        return ages.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private List<Integer> toList(String ages) {
        if (ages == null || ages.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(ages.split(","))
                .map(Integer::valueOf)
                .toList();
    }
}
