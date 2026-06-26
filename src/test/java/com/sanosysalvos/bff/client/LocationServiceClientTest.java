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
    void obtenerTodasLasUbicaciones_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(new LocationDto())));

        assertEquals(1, client.getAllLocations().size());
    }

    @Test
    void obtenerUbicacionPorId_DeberiaUsarGetForObject() {
        LocationDto esperado = new LocationDto();
        esperado.setId(1L);
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/1", LocationDto.class))
            .thenReturn(esperado);

        assertEquals(1L, client.getLocationById(1L).getId());
    }

    @Test
    void obtenerUbicacionPorId_CuandoNoExiste_DeberiaRetornarNulo() {
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/99", LocationDto.class))
            .thenReturn(null);

        assertNull(client.getLocationById(99L));
    }

    @Test
    void obtenerUbicacionesPorZona_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations/search/zone/Las Condes"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getLocationsByZone("Las Condes").isEmpty());
    }

    @Test
    void obtenerUbicacionesPorRangoFecha_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://geo-service:3002/api/locations/search/date-range?startDate=2024-01-01&endDate=2024-12-31"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getLocationsByDateRange("2024-01-01", "2024-12-31").isEmpty());
    }

    @Test
    void crearUbicacion_DeberiaUsarPostForObject() {
        LocationDto entrada = new LocationDto();
        entrada.setLatitude(-33.45);
        when(restTemplate.postForObject("http://geo-service:3002/api/locations", entrada, LocationDto.class))
            .thenReturn(entrada);

        LocationDto resultado = client.createLocation(entrada);
        assertEquals(-33.45, resultado.getLatitude());
    }

    @Test
    void actualizarUbicacion_DeberiaUsarPutYGet() {
        LocationDto actualizada = new LocationDto();
        actualizada.setLatitude(-33.5);
        when(restTemplate.getForObject("http://geo-service:3002/api/locations/1", LocationDto.class))
            .thenReturn(actualizada);

        LocationDto resultado = client.updateLocation(1L, actualizada);
        assertEquals(-33.5, resultado.getLatitude());
        verify(restTemplate).put("http://geo-service:3002/api/locations/1", actualizada);
    }

    @Test
    void eliminarUbicacion_DeberiaUsarDelete() {
        client.deleteLocation(1L);
        verify(restTemplate).delete("http://geo-service:3002/api/locations/1");
    }
}
