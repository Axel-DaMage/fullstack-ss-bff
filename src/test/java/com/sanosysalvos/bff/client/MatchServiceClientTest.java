package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.model.MatchDto;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private MatchServiceClient client;

    @BeforeEach
    void setUp() {
        client = new MatchServiceClient(restTemplate, "http://match-service:3003");
    }

    @Test
    void obtenerTodosLosMatches_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://match-service:3003/api/matching"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(new MatchDto())));

        assertEquals(1, client.getAllMatches().size());
    }

    @Test
    void obtenerMatchPorId_DeberiaUsarGetForObject() {
        MatchDto esperado = new MatchDto();
        esperado.setId(1L);
        when(restTemplate.getForObject("http://match-service:3003/api/matching/1", MatchDto.class))
            .thenReturn(esperado);

        assertEquals(1L, client.getMatchById(1L).getId());
    }

    @Test
    void obtenerMatchPorId_CuandoNoExiste_DeberiaRetornarNulo() {
        when(restTemplate.getForObject("http://match-service:3003/api/matching/99", MatchDto.class))
            .thenReturn(null);

        assertNull(client.getMatchById(99L));
    }

    @Test
    void obtenerMatchesPorStatus_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://match-service:3003/api/matching/search/status/PENDIENTE"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getMatchesByStatus("PENDIENTE").isEmpty());
    }

    @Test
    void obtenerMatchesPorPorcentaje_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://match-service:3003/api/matching/search/percentage/80"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getMatchesByPercentage(80).isEmpty());
    }

    @Test
    void crearMatch_DeberiaUsarPostForObject() {
        MatchDto esperado = new MatchDto();
        esperado.setId(1L);
        when(restTemplate.postForObject(
            eq("http://match-service:3003/api/matching"),
            eq(Map.of("petLostId", 1L, "petFoundId", 2L)),
            eq(MatchDto.class)
        )).thenReturn(esperado);

        MatchDto resultado = client.createMatch(1L, 2L);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void actualizarMatchStatus_DeberiaUsarPutYGet() {
        MatchDto actualizado = new MatchDto();
        actualizado.setStatus("CONFIRMED");
        when(restTemplate.getForObject("http://match-service:3003/api/matching/1", MatchDto.class))
            .thenReturn(actualizado);

        MatchDto resultado = client.updateMatchStatus(1L, "CONFIRMED");
        assertEquals("CONFIRMED", resultado.getStatus());
        verify(restTemplate).put("http://match-service:3003/api/matching/1", Map.of("status", "CONFIRMED"));
    }

    @Test
    void eliminarMatch_DeberiaUsarDelete() {
        client.deleteMatch(1L);
        verify(restTemplate).delete("http://match-service:3003/api/matching/1");
    }

    @Test
    void ejecutarMatchingAutomatico_DeberiaUsarPostForObject() {
        when(restTemplate.postForObject(
            eq("http://match-service:3003/api/matching/run-automatic"),
            isNull(),
            eq(String.class)
        )).thenReturn("OK");

        client.runAutomaticMatching();
        verify(restTemplate).postForObject(
            "http://match-service:3003/api/matching/run-automatic",
            null,
            String.class
        );
    }
}
