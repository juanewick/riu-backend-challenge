package com.riu.backend.challenge.unit.adapter.out.persistence;

import com.riu.backend.challenge.adapter.out.persistence.SearchPersistenceAdapter;
import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import com.riu.backend.challenge.adapter.out.persistence.mapper.SearchEntityMapper;
import com.riu.backend.challenge.adapter.out.persistence.repository.SearchJpaRepository;
import com.riu.backend.challenge.domain.model.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPersistenceAdapterTest {
    private static final LocalDate CHECK_IN = LocalDate.of(2026, 12, 15);
    private static final LocalDate CHECK_OUT = LocalDate.of(2026, 12, 31);

    @Mock
    private SearchJpaRepository searchJpaRepository;
    private SearchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchPersistenceAdapter(searchJpaRepository, new SearchEntityMapper());
    }

    @Test
    public void givenASearch_whenSaveIsCalled_thenStoresItsEntity() {
        adapter.save(createSearch());

        verify(searchJpaRepository).save(createSearchEntity());
    }

    @Test
    public void givenAStoredSearch_whenFindByIdIsCalled_thenReturnsItAsDomain() {
        when(searchJpaRepository.findById("someId")).thenReturn(Optional.of(createSearchEntity()));

        assertThat(adapter.findById("someId")).contains(createSearch());
    }

    @Test
    public void givenAnUnknowSearchId_whenFindByIdIsCalled_thenReturnsEmpty() {
        when(searchJpaRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThat(adapter.findById("unknown")).isEmpty();
    }

    @Test
    public void givenASearch_whenCountSameSearchesIsCalled_thenCountsByAllItsFields() {
        when(searchJpaRepository.countByHotelIdAndCheckInAndCheckOutAndAges("someHotelId", CHECK_IN, CHECK_OUT, "30,29,1,3"))
                .thenReturn(3L);

        assertThat(adapter.countSameSearches(createSearch())).isEqualTo(3L);
    }

    private static Search createSearch() {
        return new Search("someId", "someHotelId", CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));
    }

    private static SearchEntity createSearchEntity() {
        return new SearchEntity("someId", "someHotelId", CHECK_IN, CHECK_OUT, "30,29,1,3");
    }
}
