package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.exception.alreadyexists.AuditoriumAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.repository.cache.AuditoriumCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.AuditoriumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AuditoriumService {

    private final AuditoriumRepository auditoriumRepository;
    private final AuditoriumCacheRepository auditoriumCacheRepository;
    private final RowService rowService;

    @Transactional
    public long saveAuditorium(Auditorium auditorium) {
        if (auditoriumExists(auditorium.getName(), auditorium.getTheaterId())) {
            throw new AuditoriumAlreadyExistsException("Auditorium with name %s and theaterId %s already exists".formatted(auditorium.getName(), auditorium.getTheaterId()));
        }

        long id = auditoriumRepository.save(auditorium);
        auditorium.getRows().forEach(row -> row.setAuditoriumId(id));
        rowService.saveRows(auditorium.getRows());
        return id;
    }

    public Optional<Auditorium> getAuditoriumById(long id) {
        return auditoriumCacheRepository.getById(id)
            .or(() -> {
                Optional<Auditorium> auditoriumOptional = auditoriumRepository.getById(id);
                auditoriumOptional.ifPresent(auditoriumCacheRepository::save);
                return auditoriumOptional;
            });
    }

    public boolean auditoriumExists(String name, long id) {
        return auditoriumCacheRepository.exists(name, id)
            .orElseGet(() -> {
                boolean value = auditoriumRepository.exists(name, id);
                auditoriumCacheRepository.saveExists(name, id);
                return value;
            });
    }
}
