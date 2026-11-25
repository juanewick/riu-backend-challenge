package com.riu.backend.challenge.adapter.in.rest;

import com.riu.backend.challenge.adapter.in.rest.api.SearchCountResponse;
import com.riu.backend.challenge.adapter.in.rest.api.SearchRequest;
import com.riu.backend.challenge.adapter.in.rest.api.SearchResponse;
import com.riu.backend.challenge.domain.port.in.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    SearchResponse search(@Valid @RequestBody SearchRequest searchRequest) {
        return new SearchResponse(searchService.search(SearchRequest.toDomain(searchRequest)));
    }

    @GetMapping("/count")
    SearchCountResponse count(
            @RequestParam("searchId")
            @NotNull(message = "must not be null")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "must be a valid UUID")
            String searchId) {
        return SearchCountResponse.fromDomain(searchService.count(searchId));
    }
}
