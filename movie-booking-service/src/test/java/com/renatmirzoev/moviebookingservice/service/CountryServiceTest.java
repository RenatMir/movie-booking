package com.renatmirzoev.moviebookingservice.service;

import com.renatmirzoev.moviebookingservice.ModelUtils;
import com.renatmirzoev.moviebookingservice.exception.alreadyexists.CountryAlreadyExistsException;
import com.renatmirzoev.moviebookingservice.model.entity.Country;
import com.renatmirzoev.moviebookingservice.repository.cache.CountryCacheRepository;
import com.renatmirzoev.moviebookingservice.repository.db.CountryRepository;
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
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountryCacheRepository countryCacheRepository;
    @InjectMocks
    private CountryService countryService;

    private InOrder inOrder;

    @BeforeEach
    void init() {
        inOrder = Mockito.inOrder(countryRepository, countryCacheRepository);
    }

    @Test
    void shouldNotSaveCountryIfCountryExistsFromCache() {
        Country country = ModelUtils.country();

        when(countryCacheRepository.exists(anyString())).thenReturn(Optional.of(true));

        assertThatException()
            .isThrownBy(() -> countryService.saveCountry(country))
            .isInstanceOf(CountryAlreadyExistsException.class);

        inOrder.verify(countryCacheRepository).exists(anyString());
        inOrder.verify(countryCacheRepository, never()).save(any(Country.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldNotSaveCountryIfCountryExistsFromDb() {
        Country country = ModelUtils.country();

        when(countryCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(countryRepository.exists(anyString())).thenReturn(true);

        assertThatException()
            .isThrownBy(() -> countryService.saveCountry(country))
            .isInstanceOf(CountryAlreadyExistsException.class);

        inOrder.verify(countryCacheRepository).exists(anyString());
        inOrder.verify(countryRepository).exists(anyString());
        inOrder.verify(countryCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldSaveCountry() {
        Country country = ModelUtils.country();

        when(countryCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(countryRepository.exists(anyString())).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(country);

        countryService.saveCountry(country);

        inOrder.verify(countryCacheRepository).exists(anyString());
        inOrder.verify(countryRepository).exists(anyString());
        inOrder.verify(countryCacheRepository).saveExists(anyString());
        inOrder.verify(countryRepository).save(any(Country.class));
        inOrder.verify(countryCacheRepository).save(any(Country.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetEmptyCountryById() {
        when(countryCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(countryRepository.getById(anyLong())).thenReturn(Optional.empty());

        Optional<Country> countryOptional = countryService.getCountryById(Long.MAX_VALUE);
        assertThat(countryOptional).isEmpty();

        inOrder.verify(countryCacheRepository).getById(anyLong());
        inOrder.verify(countryRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetCountryByIdFromCache() {
        Country country = ModelUtils.country();

        when(countryCacheRepository.getById(anyLong())).thenReturn(Optional.of(country));

        Optional<Country> countryOptional = countryService.getCountryById(Long.MAX_VALUE);
        assertThat(countryOptional).isPresent().contains(country);

        inOrder.verify(countryCacheRepository).getById(anyLong());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldGetCountryByIdFromDB() {
        Country country = ModelUtils.country();

        when(countryCacheRepository.getById(anyLong())).thenReturn(Optional.empty());
        when(countryRepository.getById(anyLong())).thenReturn(Optional.of(country));

        Optional<Country> countryOptional = countryService.getCountryById(Long.MAX_VALUE);
        assertThat(countryOptional).isPresent().contains(country);

        inOrder.verify(countryCacheRepository).getById(anyLong());
        inOrder.verify(countryRepository).getById(anyLong());
        inOrder.verify(countryCacheRepository).save(any(Country.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromDb(boolean existParam) {
        when(countryCacheRepository.exists(anyString())).thenReturn(Optional.empty());
        when(countryRepository.exists(anyString())).thenReturn(existParam);

        boolean exists = countryService.countryExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(countryCacheRepository).exists(anyString());
        inOrder.verify(countryRepository).exists(anyString());
        inOrder.verify(countryCacheRepository).saveExists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldGetExistFromCache(boolean existParam) {
        when(countryCacheRepository.exists(anyString())).thenReturn(Optional.of(existParam));

        boolean exists = countryService.countryExists(UUID.randomUUID().toString());

        assertThat(exists).isEqualTo(existParam);

        inOrder.verify(countryCacheRepository).exists(anyString());
        inOrder.verifyNoMoreInteractions();
    }

}