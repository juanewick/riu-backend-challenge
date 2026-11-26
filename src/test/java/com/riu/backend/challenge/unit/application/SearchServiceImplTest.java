package com.riu.backend.challenge.unit.application;

import com.riu.backend.challenge.application.SearchServiceImpl;
import com.riu.backend.challenge.domain.exception.SearchNotFoundException;
import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.model.SearchCount;
import com.riu.backend.challenge.domain.port.out.SearchPublisher;
import com.riu.backend.challenge.domain.port.out.SearchRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {
    @Mock
    private SearchRepository searchRepository;
    @Mock
    private SearchPublisher searchPublisher;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    public void givenAValidSearch_whenSearchIsCalled_thenPublishesItWithANewId() {
        Search search = createSearch();

        String searchId = searchService.search(search);

        ArgumentCaptor<Search> published = ArgumentCaptor.forClass(Search.class);
        verify(searchPublisher).publish(published.capture());

        assertThat(searchId).isNotBlank();
        assertThat(published.getValue())
                .isEqualTo(new Search(searchId, search.hotelId(), search.checkIn(), search.checkOut(), search.ages()));
        verifyNoInteractions(searchRepository);
    }

    @Test
    public void givenAValidSearch_whenCreateIsCalled_thenSaveIt() {
        Search search = createSearch();

        searchService.create(search);

        verify(searchRepository).save(search);
        verifyNoInteractions(searchPublisher);
    }

    @Test
    public void givenAValidSearchId_whenCountIsCalled_thenReturnsTheSearchCount() {
        String searchId = "someId";
        Search search = createSearch();
        when(searchRepository.findById(searchId)).thenReturn(Optional.of(search));
        when(searchRepository.countSameSearches(search)).thenReturn(3L);

        SearchCount count = searchService.count(searchId);

        assertThat(count).isEqualTo(new SearchCount(searchId, search, 3L));
        verifyNoInteractions(searchPublisher);
    }

    @Test
    public void givenAnUnknownSearchId_whenCountIsCalled_thenThrowsSearchNotFound() {
        String searchId = "unknownSearchId";
        when(searchRepository.findById(searchId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchService.count(searchId))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessage("Search not found: " + searchId);

        verify(searchRepository, never()).countSameSearches(any());
        verifyNoInteractions(searchPublisher);
    }

    private static @NonNull Search createSearch() {
        return new Search(
                "someId",
                "someHotelId",
                LocalDate.of(2026, 12, 15),
                LocalDate.of(2026, 12, 31),
                List.of(30, 29, 1, 3)
        );
    }
}
