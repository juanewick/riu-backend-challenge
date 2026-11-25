package com.riu.backend.challenge.adapter.out.persistence.repository;

import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface SearchJpaRepository extends JpaRepository<SearchEntity, String> {

    @Query("""
            select count(s) from SearchEntity s
            where s.hotelId = :hotelId
              and s.checkIn = :checkIn
              and s.checkOut = :checkOut
              and s.ages = :ages
            """)
    long countByHotelIdAndCheckInAndCheckOutAndAges(String hotelId, LocalDate checkIn, LocalDate checkOut, String ages);
}
