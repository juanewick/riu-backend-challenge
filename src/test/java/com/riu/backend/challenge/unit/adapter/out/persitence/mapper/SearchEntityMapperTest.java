package com.riu.backend.challenge.unit.adapter.out.persitence.mapper;

import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import com.riu.backend.challenge.adapter.out.persistence.mapper.SearchEntityMapper;
import com.riu.backend.challenge.domain.model.Search;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchEntityMapperTest {

    private final SearchEntityMapper mapper = new SearchEntityMapper();

    @Test
    public void givenASearch_whenToEntityIsCalled_thenMapsAllFields() {
        Search search = createSearch();

        SearchEntity entity = mapper.toEntity(search);

        assertThat(entity).isEqualTo(createSearchEntity());
    }

    @Test
    public void givenASearchWithoutAges_whenToEntityIsCalled_thenAgesIsEmptyText() {
        Search search = createSearch(List.of());

        SearchEntity entity = mapper.toEntity(search);

        assertThat(entity.getAges()).isEmpty();
    }

    @Test
    public void givenAnEntity_whenToDomainIsCalled_thenMapsAllFields() {
        SearchEntity entity = createSearchEntity();

        Search search = mapper.toDomain(entity);

        assertThat(search).isEqualTo(createSearch());
    }

    @Test
    public void givenAnEntityWithNullAges_whenToDomainIsCalled_thenAgesIsEmptyList() {
        SearchEntity entity = createSearchEntity(null);

        Search search = mapper.toDomain(entity);

        assertThat(search.ages()).isEmpty();
    }

    @Test
    public void givenAnEntityWithEmptyAges_whenToDomainIsCalled_thenAgesIsEmptyList() {
        SearchEntity entity = createSearchEntity("");

        Search search = mapper.toDomain(entity);

        assertThat(search.ages()).isEmpty();
    }

    private static @NonNull Search createSearch() {
        return createSearch(List.of(30, 29, 1, 3));
    }

    private static @NonNull Search createSearch(List<Integer> ages) {
        return new Search(
                "someId",
                "someHotelId",
                LocalDate.of(2026, 12, 15),
                LocalDate.of(2026, 12, 31),
                ages
        );
    }

    private static @NonNull SearchEntity createSearchEntity() {
        return createSearchEntity("30,29,1,3");
    }

    private static @NonNull SearchEntity createSearchEntity(String ages) {
        return new SearchEntity(
                "someId",
                "someHotelId",
                LocalDate.of(2026, 12, 15),
                LocalDate.of(2026, 12, 31),
                ages
        );
    }

}
