package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.AuditoriumAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Auditorium;
import com.renatmirzoev.moviebookingservice.repository.cache.AuditoriumCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.AuditoriumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriumServiceTest {

    @Mock
    private AuditoriumRepository auditoriumRepository;
    @Mock
    private AuditoriumCacheRepository auditoriumCacheRepository;
    @Mock
    private RowService rowService;
    @InjectMocks
    private AuditoriumService auditoriumService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(auditoriumRepository, auditoriumCacheRepository);
    }

    @Test
    void shouldNotSaveAuditoriumIfAuditoriumExistsFromCache() {
        Auditorium auditorium = ModelUtils.auditorium();

        when(auditoriumCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> auditoriumService.saveAuditorium(auditorium))
            .isInstanceOf(AuditoriumAlreadyExistsException.class);

        inOrder.verify(auditoriumCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumCacheRepository, never()).save(any(Auditorium.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveAuditoriumIfAuditoriumExistsFromDb() {
        Auditorium auditorium = ModelUtils.auditorium();

        when(auditoriumCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(auditoriumRepository.exists(anyString(), anyLong())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> auditoriumService.saveAuditorium(auditorium))
            .isInstanceOf(AuditoriumAlreadyExistsException.class);

        inOrder.verify(auditoriumCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveAuditorium() {
        Auditorium auditorium = ModelUtils.auditorium();

        when(auditoriumCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(auditoriumRepository.exists(anyString(), anyLong())).thenReturn(false);
        when(auditoriumRepository.save(any(Auditorium.class))).thenReturn(Long.MAX_VALUE);

        auditoriumService.saveAuditorium(auditorium);

        inOrder.verify(auditoriumCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verify(auditoriumRepository).save(any(Auditorium.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyAuditoriumById() {
        when(auditoriumCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(auditoriumRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Auditorium> auditoriumOptional = auditoriumService.getAuditoriumById(Long.MAX_VALUE);
        assertThat(auditoriumOptional).isEmpty();

        inOrder.verify(auditoriumCacheRepository).getById(anyLong());
        inOrder.verify(auditoriumRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetAuditoriumByIdFromCache() {
        Auditorium auditorium = ModelUtils.auditorium();

        when(auditoriumCacheRepository.getById(anyLong())).thenReturn(Optional.of(auditorium));

        Optional<Auditorium> auditoriumOptional = auditoriumService.getAuditoriumById(Long.MAX_VALUE);
        assertThat(auditoriumOptional).isPresent().contains(auditorium);

        inOrder.verify(auditoriumCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetAuditoriumByIdFromDB() {
        Auditorium auditorium = ModelUtils.auditorium();

        when(auditoriumCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(auditoriumRepository.getById(anyLong())).thenReturn(Optional.of(auditorium));

        Optional<Auditorium> auditoriumOptional = auditoriumService.getAuditoriumById(Long.MAX_VALUE);
        assertThat(auditoriumOptional).isPresent().contains(auditorium);

        inOrder.verify(auditoriumCacheRepository).getById(anyLong());
        inOrder.verify(auditoriumRepository).getById(anyLong());
        inOrder.verify(auditoriumCacheRepository).save(any(Auditorium.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(auditoriumCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(auditoriumRepository.exists(anyString(), anyLong())).thenReturn(existParam);

        boolean exists = auditoriumService.auditoriumExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(auditoriumCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumRepository).exists(anyString(), anyLong());
        inOrder.verify(auditoriumCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(auditoriumCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(existParam));

        boolean exists = auditoriumService.auditoriumExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(auditoriumCacheRepository).exists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

}