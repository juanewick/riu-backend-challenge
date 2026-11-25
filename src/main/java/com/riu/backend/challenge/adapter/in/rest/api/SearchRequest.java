package com.riu.backend.challenge.adapter.in.rest.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.riu.backend.challenge.domain.model.Search;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public record SearchRequest(
        @NotNull(message = "must not be null")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "must contain only letters and numbers")
        String hotelId,

        @NotNull(message = "must not be null")
        @JsonFormat(pattern = "dd/MM/uuuu", lenient = OptBoolean.FALSE)
        LocalDate checkIn,

        @NotNull(message = "must not be null")
        @JsonFormat(pattern = "dd/MM/uuuu", lenient = OptBoolean.FALSE)
        LocalDate checkOut,

        @NotEmpty(message = "must not be empty")
        List<@NotNull(message = "must not be null") @PositiveOrZero(message = "must be zero or greater") Integer> ages
) {
    @AssertTrue(message = "checkOut must be after checkIn")
    public boolean isCheckOutAfterCheckIn() {
        return checkIn == null || checkOut == null || checkOut.isAfter(checkIn);
    }

    public static Search toDomain(SearchRequest searchRequest) {
        return new Search(null,
                searchRequest.hotelId,
                searchRequest.checkIn,
                searchRequest.checkOut,
                List.copyOf(searchRequest.ages));
    }
}
