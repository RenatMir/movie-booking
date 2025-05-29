package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.CityAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.City;
import com.renatmirzoev.moviebookingservice.repository.cache.CityCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CityRepository;
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
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private CityCacheRepository cityCacheRepository;
    @InjectMocks
    private CityService cityService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(cityRepository, cityCacheRepository);
    }

    @Test
    void shouldNotSaveCityIfCityExistsFromCache() {
        City city = ModelUtils.city();

        when(cityCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> cityService.saveCity(city))
            .isInstanceOf(CityAlreadyExistsException.class);

        inOrder.verify(cityCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(cityCacheRepository, never()).save(any(City.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveCityIfCityExistsFromDb() {
        City city = ModelUtils.city();

        when(cityCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(cityRepository.exists(anyString(), anyLong())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> cityService.saveCity(city))
            .isInstanceOf(CityAlreadyExistsException.class);

        inOrder.verify(cityCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(cityRepository).exists(anyString(), anyLong());
        inOrder.verify(cityCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveCity() {
        City city = ModelUtils.city();

        when(cityCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(cityRepository.exists(anyString(), anyLong())).thenReturn(false);
        when(cityRepository.save(any(City.class))).thenReturn(city);

        cityService.saveCity(city);

        inOrder.verify(cityCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(cityRepository).exists(anyString(), anyLong());
        inOrder.verify(cityCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verify(cityRepository).save(any(City.class));
        inOrder.verify(cityCacheRepository).save(any(City.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyCityById() {
        when(cityCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(cityRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<City> cityOptional = cityService.getCityById(Long.MAX_VALUE);
        assertThat(cityOptional).isEmpty();

        inOrder.verify(cityCacheRepository).getById(anyLong());
        inOrder.verify(cityRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetCityByIdFromCache() {
        City city = ModelUtils.city();

        when(cityCacheRepository.getById(anyLong())).thenReturn(Optional.of(city));

        Optional<City> cityOptional = cityService.getCityById(Long.MAX_VALUE);
        assertThat(cityOptional).isPresent().contains(city);

        inOrder.verify(cityCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetCityByIdFromDB() {
        City city = ModelUtils.city();

        when(cityCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(cityRepository.getById(anyLong())).thenReturn(Optional.of(city));

        Optional<City> cityOptional = cityService.getCityById(Long.MAX_VALUE);
        assertThat(cityOptional).isPresent().contains(city);

        inOrder.verify(cityCacheRepository).getById(anyLong());
        inOrder.verify(cityRepository).getById(anyLong());
        inOrder.verify(cityCacheRepository).save(any(City.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(cityCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.empty());
        when(cityRepository.exists(anyString(), anyLong())).thenReturn(existParam);

        boolean exists = cityService.cityExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(cityCacheRepository).exists(anyString(), anyLong());
        inOrder.verify(cityRepository).exists(anyString(), anyLong());
        inOrder.verify(cityCacheRepository).saveExists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(cityCacheRepository.exists(anyString(), anyLong())).thenReturn(Optional.of(existParam));

        boolean exists = cityService.cityExists(UUID.randomUUID().toString(), Long.MAX_VALUE);

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(cityCacheRepository).exists(anyString(), anyLong());
        inOrder.verifyNoMoreInteractions();
    }
}