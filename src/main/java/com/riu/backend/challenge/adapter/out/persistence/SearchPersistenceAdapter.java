package com.riu.backend.challenge.adapter.out.persistence;

import com.riu.backend.challenge.adapter.out.persistence.entity.SearchEntity;
import com.riu.backend.challenge.adapter.out.persistence.mapper.SearchEntityMapper;
import com.riu.backend.challenge.adapter.out.persistence.repository.SearchJpaRepository;
import com.riu.backend.challenge.domain.model.Search;
import com.riu.backend.challenge.domain.port.out.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SearchPersistenceAdapter implements SearchRepository {
    private final SearchJpaRepository searchJpaRepository;
    private final SearchEntityMapper entityMapper;

    @Override
    public void save(Search search) {
        searchJpaRepository.save(entityMapper.toEntity(search));
    }

    @Override
    public Optional<Search> findById(String searchId) {
        return searchJpaRepository.findById(searchId).map(entityMapper::toDomain);
    }

    @Override
    public long countSameSearches(Search search) {
        SearchEntity entity = entityMapper.toEntity(search);
        return searchJpaRepository.countByHotelIdAndCheckInAndCheckOutAndAges(
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                entity.getAges());
    }
}
