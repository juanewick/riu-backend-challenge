package com.riu.backend.challenge.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "searches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchEntity {
    @Id
    private String id;
    private String hotelId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String ages;
}
