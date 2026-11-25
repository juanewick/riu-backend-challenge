package com.riu.backend.challenge.domain.port.in;

import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.model.SearchCount;

public interface SearchService {
    String search(Search search);

    void create(Search search);

    SearchCount count(String searchId);
}
