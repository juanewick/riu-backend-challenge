package com.riu.backend.challenge.adapter.in.rest;

import com.riu.backend.challenge.adapter.in.rest.api.SearchCountResponse;
import com.riu.backend.challenge.adapter.in.rest.api.SearchRequest;
import com.riu.backend.challenge.adapter.in.rest.api.SearchResponse;
import com.riu.backend.challenge.domain.port.in.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/search")
    SearchResponse search(@RequestBody SearchRequest searchRequest) {
        return new SearchResponse(searchService.search(SearchRequest.toDomain(searchRequest)));
    }

    @GetMapping("/count")
    SearchCountResponse count(@RequestParam("searchId") String searchId) {
        return SearchCountResponse.fromDomain(searchService.count(searchId));
    }
}
