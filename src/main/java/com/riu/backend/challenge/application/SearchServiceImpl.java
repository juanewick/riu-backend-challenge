package com.riu.backend.challenge.application;

import com.riu.backend.challenge.domain.exception.SearchNotFoundException;
import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.model.SearchCount;
import com.riu.backend.challenge.domain.port.in.SearchService;
import com.riu.backend.challenge.domain.port.out.SearchPublisher;
import com.riu.backend.challenge.domain.port.out.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final SearchPublisher searchPublisher;

    @Override
    public String search(Search search) {
        String id = UUID.randomUUID().toString();
        Search searchWithId = new Search(
                id,
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages());
        log.info("A search with id: {} is being sent to queue.", id);
        searchPublisher.publish(searchWithId);

        return id;
    }

    @Override
    @Transactional
    public void create(Search search) {
        searchRepository.save(search);
        log.info("A search with id: {} is saved successfully.", search.id());
    }

    @Override
    @Transactional(readOnly = true)
    public SearchCount count(String searchId) {
        Search search = searchRepository.findById(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));
        long count = searchRepository.countSameSearches(search);

        return new SearchCount(searchId, search, count);
    }
}
