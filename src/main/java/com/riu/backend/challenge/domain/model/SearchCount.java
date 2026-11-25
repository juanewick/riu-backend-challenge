package com.riu.backend.challenge.domain.model;

public record SearchCount (
        String searchId,
        Search search,
        Long count
) {}