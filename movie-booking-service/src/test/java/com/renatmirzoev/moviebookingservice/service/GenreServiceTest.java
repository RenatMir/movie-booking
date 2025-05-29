package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.GenreAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Genre;
import com.renatmirzoev.moviebookingservice.repository.cache.GenreCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.GenreRepository;
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
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private GenreCacheRepository genreCacheRepository;
    @InjectMocks
    private GenreService genreService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(genreRepository, genreCacheRepository);
    }

    @Test
    void shouldNotSaveGenreIfGenreExistsFromCache() {
        Genre genre = ModelUtils.genre();

        when(genreCacheRepository.exists(anyString())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> genreService.save(genre))
            .isInstanceOf(GenreAlreadyExistsException.class);

        inOrder.verify(genreCacheRepository).exists(anyString());
        inOrder.verify(genreCacheRepository, never()).save(any(Genre.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveGenreIfGenreExistsFromDb() {
        Genre genre = ModelUtils.genre();

        when(genreCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(genreRepository.exists(anyString())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> genreService.save(genre))
            .isInstanceOf(GenreAlreadyExistsException.class);

        inOrder.verify(genreCacheRepository).exists(anyString());
        inOrder.verify(genreRepository).exists(anyString());
        inOrder.verify(genreCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveGenre() {
        Genre genre = ModelUtils.genre();

        when(genreCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(genreRepository.exists(anyString())).thenReturn(false);
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        genreService.save(genre);

        inOrder.verify(genreCacheRepository).exists(anyString());
        inOrder.verify(genreRepository).exists(anyString());
        inOrder.verify(genreCacheRepository).saveExists(anyString());
        inOrder.verify(genreRepository).save(any(Genre.class));
        inOrder.verify(genreCacheRepository).save(any(Genre.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyGenreById() {
        when(genreCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(genreRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Genre> genreOptional = genreService.getGenreById(Long.MAX_VALUE);
        assertThat(genreOptional).isEmpty();

        inOrder.verify(genreCacheRepository).getById(anyLong());
        inOrder.verify(genreRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetGenreByIdFromCache() {
        Genre genre = ModelUtils.genre();

        when(genreCacheRepository.getById(anyLong())).thenReturn(Optional.of(genre));

        Optional<Genre> genreOptional = genreService.getGenreById(Long.MAX_VALUE);
        assertThat(genreOptional).isPresent().contains(genre);

        inOrder.verify(genreCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetGenreByIdFromDB() {
        Genre genre = ModelUtils.genre();

        when(genreCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(genreRepository.getById(anyLong())).thenReturn(Optional.of(genre));

        Optional<Genre> genreOptional = genreService.getGenreById(Long.MAX_VALUE);
        assertThat(genreOptional).isPresent().contains(genre);

        inOrder.verify(genreCacheRepository).getById(anyLong());
        inOrder.verify(genreRepository).getById(anyLong());
        inOrder.verify(genreCacheRepository).save(any(Genre.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(genreCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(genreRepository.exists(anyString())).thenReturn(existParam);

        boolean exists = genreService.genreExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(genreCacheRepository).exists(anyString());
        inOrder.verify(genreRepository).exists(anyString());
        inOrder.verify(genreCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(genreCacheRepository.exists(anyString())).thenReturn(Optional.of(existParam));

        boolean exists = genreService.genreExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(genreCacheRepository).exists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

}