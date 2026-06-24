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
    void obtenerMatchesLlamaUrlCorrecta() {
        when(restTemplate.exchange(
            eq("http://match-service:3003/api/matching"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        List<MatchDto> result = client.getAllMatches();
        assertNotNull(result);
    }

    @Test
    void crearMatchPosteaYRetorna() {
        MatchDto expected = new MatchDto();
        expected.setId(1L);
        when(restTemplate.postForObject(
            eq("http://match-service:3003/api/matching"),
            eq(Map.of("petLostId", 1L, "petFoundId", 2L)),
            eq(MatchDto.class)
        )).thenReturn(expected);

        MatchDto result = client.createMatch(1L, 2L);
        assertEquals(1L, result.getId());
    }

    @Test
    void actualizarMatchHacePutYRetorna() {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setStatus("CONFIRMED");

        when(restTemplate.getForObject("http://match-service:3003/api/matching/1", MatchDto.class))
            .thenReturn(match);

        MatchDto result = client.updateMatchStatus(1L, "CONFIRMED");
        assertEquals("CONFIRMED", result.getStatus());
        verify(restTemplate).put("http://match-service:3003/api/matching/1", Map.of("status", "CONFIRMED"));
    }
}
