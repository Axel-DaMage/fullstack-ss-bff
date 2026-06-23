package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.model.LocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private LocationServiceClient client;

    @BeforeEach
    void setUp() {
        client = new LocationServiceClient(restTemplate, "http://geo-service:3002");
    }

    @Test
    void getAllLocations_ShouldCallCorrectUrl() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        List<LocationDto> result = client.getAllLocations();
        assertNotNull(result);
    }

    @Test
    void getLocationById_ShouldReturnLocation() {
        LocationDto expected = new LocationDto();
        expected.setId(1L);
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/1", LocationDto.class))
            .thenReturn(expected);

        LocationDto result = client.getLocationById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getLocationById_ShouldReturnNullWhenNotFound() {
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/99", LocationDto.class))
            .thenReturn(null);
        assertNull(client.getLocationById(99L));
    }

    @Test
    void getLocationsByZone_ShouldCallCorrectUrl() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations/search/zone/Centro"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(new LocationDto())));

        List<LocationDto> result = client.getLocationsByZone("Centro");
        assertEquals(1, result.size());
    }

    @Test
    void getLocationsByDateRange_ShouldCallCorrectUrl() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations/search/date-range?startDate=2024-01-01&endDate=2024-12-31"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        List<LocationDto> result = client.getLocationsByDateRange("2024-01-01", "2024-12-31");
        assertNotNull(result);
    }

    @Test
    void createLocation_ShouldPostAndReturn() {
        LocationDto loc = new LocationDto();
        when(restTemplate.postForObject("http://geo-service:3002/api/locations", loc, LocationDto.class))
            .thenReturn(loc);
        assertNotNull(client.createLocation(loc));
    }

    @Test
    void deleteLocation_ShouldCallDelete() {
        client.deleteLocation(1L);
        verify(restTemplate).delete("http://geo-service:3002/api/locations/1");
    }

    @Test
    void updateLocation_ShouldPutAndReturn() {
        LocationDto loc = new LocationDto();
        loc.setId(1L);
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/1", LocationDto.class))
            .thenReturn(loc);

        LocationDto result = client.updateLocation(1L, loc);
        assertEquals(1L, result.getId());
        verify(restTemplate).put("http://geo-service:3002/api/locations/1", loc);
    }
}
