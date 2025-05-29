package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.model.entity.Row;
import com.renatmirzoev.moviebookingservice.repository.cache.RowCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.RowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RowServiceTest {

    @Mock
    private RowRepository rowRepository;
    @Mock
    private RowCacheRepository rowCacheRepository;
    @InjectMocks
    private RowService rowService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(rowRepository, rowCacheRepository);
    }

    @Test
    void shouldSaveRow() {
        Row row = ModelUtils.row();

        when(rowRepository.save(anySet())).thenReturn(Set.of(Integer.MAX_VALUE));

        rowService.saveRows(Set.of(row));

        inOrder.verify(rowRepository).save(anySet());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyRowById() {
        when(rowCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(rowRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Row> rowOptional = rowService.getRowById(Long.MAX_VALUE);
        assertThat(rowOptional).isEmpty();

        inOrder.verify(rowCacheRepository).getById(anyLong());
        inOrder.verify(rowRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetRowByIdFromCache() {
        Row row = ModelUtils.row();

        when(rowCacheRepository.getById(anyLong())).thenReturn(Optional.of(row));

        Optional<Row> rowOptional = rowService.getRowById(Long.MAX_VALUE);
        assertThat(rowOptional).isPresent().contains(row);

        inOrder.verify(rowCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetRowByIdFromDB() {
        Row row = ModelUtils.row();

        when(rowCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(rowRepository.getById(anyLong())).thenReturn(Optional.of(row));

        Optional<Row> rowOptional = rowService.getRowById(Long.MAX_VALUE);
        assertThat(rowOptional).isPresent().contains(row);

        inOrder.verify(rowCacheRepository).getById(anyLong());
        inOrder.verify(rowRepository).getById(anyLong());
        inOrder.verify(rowCacheRepository).save(any(Row.class));
        inOrder.verifyNoMoreInteractions();
    }
}