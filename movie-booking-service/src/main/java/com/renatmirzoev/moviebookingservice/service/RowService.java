package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.repository.cache.RowCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.RowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RowService {

    private final RowRepository rowRepository;
    private final RowCacheRepository rowCacheRepository;
    private final SeatService seatService;

    @Transactional
    public void saveRows(Set<Row> rows) {
        rows.forEach(row -> {
            long rowId = rowRepository.save(row);
            row.getSeats().forEach(seat -> seat.setRowId(rowId));
            seatService.saveSeats(row.getSeats());
        });
    }

    public Optional<Row> getRowById(long id) {
        return rowCacheRepository.getById(id)
            .or(() -> {
                Optional<Row> rowOptional = rowRepository.getById(id);
                rowOptional.ifPresent(rowCacheRepository::save);
                return rowOptional;
            });
    }
}
