package com.riu.backend.challenge.domain.port.out;

import com.riu.backend.challenge.domain.model.Search;

public interface SearchPublisher {
    void publish(Search search);
}
