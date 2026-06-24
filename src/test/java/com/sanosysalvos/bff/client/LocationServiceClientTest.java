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
    void getLocationById_ShouldReturnLocationOrNull() {
        LocationDto expected = new LocationDto();
        expected.setId(1L);
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/1", LocationDto.class))
            .thenReturn(expected);
        assertEquals(1L, client.getLocationById(1L).getId());

        when(restTemplate.getForObject("http://geo-service:3002/api/locations/99", LocationDto.class))
            .thenReturn(null);
        assertNull(client.getLocationById(99L));
    }

    @Test
    void createLocation_ShouldPostAndReturn() {
        LocationDto loc = new LocationDto();
        when(restTemplate.postForObject("http://geo-service:3002/api/locations", loc, LocationDto.class))
            .thenReturn(loc);
        assertNotNull(client.createLocation(loc));
    }
}
