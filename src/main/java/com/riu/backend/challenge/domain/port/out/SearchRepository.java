package com.riu.backend.challenge.domain.port.out;

import com.riu.backend.challenge.domain.model.Search;

import java.util.Optional;

public interface SearchRepository {
    void save(Search search);

    Optional<Search> findById(String searchId);

    long countSameSearches(Search search);
}
