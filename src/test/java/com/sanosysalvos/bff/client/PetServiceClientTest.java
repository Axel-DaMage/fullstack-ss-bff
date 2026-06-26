package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.model.PetDto;
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
class PetServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private PetServiceClient client;

    @BeforeEach
    void setUp() {
        client = new PetServiceClient(restTemplate, "http://pet-service:3001");
    }

    @Test
    void obtenerTodasLasMascotas_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://pet-service:3001/api/pets"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(new PetDto())));

        List<PetDto> resultado = client.getAllPets();
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerMascotaPorId_DeberiaUsarGetForObject() {
        PetDto esperado = new PetDto();
        esperado.setId(1L);
        when(restTemplate.getForObject("http://pet-service:3001/api/pets/1", PetDto.class))
            .thenReturn(esperado);

        assertEquals(1L, client.getPetById(1L).getId());
    }

    @Test
    void obtenerMascotaPorId_CuandoNoExiste_DeberiaRetornarNulo() {
        when(restTemplate.getForObject("http://pet-service:3001/api/pets/99", PetDto.class))
            .thenReturn(null);

        assertNull(client.getPetById(99L));
    }

    @Test
    void obtenerMascotasPorStatus_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://pet-service:3001/api/pets/search/status/PERDIDO"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getPetsByStatus("PERDIDO").isEmpty());
    }

    @Test
    void obtenerMascotasPorRaza_DeberiaUsarExchange() {
        when(restTemplate.exchange(
            eq("http://pet-service:3001/api/pets/search/race/Beagle"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        assertTrue(client.getPetsByRace("Beagle").isEmpty());
    }

    @Test
    void crearMascota_DeberiaUsarPostForObject() {
        PetDto entrada = new PetDto();
        entrada.setName("Firulais");
        when(restTemplate.postForObject("http://pet-service:3001/api/pets", entrada, PetDto.class))
            .thenReturn(entrada);

        PetDto resultado = client.createPet(entrada);
        assertEquals("Firulais", resultado.getName());
    }

    @Test
    void actualizarMascota_DeberiaUsarPutYGet() {
        PetDto actualizado = new PetDto();
        actualizado.setName("Actualizado");
        when(restTemplate.getForObject("http://pet-service:3001/api/pets/1", PetDto.class))
            .thenReturn(actualizado);

        PetDto resultado = client.updatePet(1L, actualizado);
        assertEquals("Actualizado", resultado.getName());
        verify(restTemplate).put("http://pet-service:3001/api/pets/1", actualizado);
    }

    @Test
    void eliminarMascota_DeberiaUsarDelete() {
        client.deletePet(1L);
        verify(restTemplate).delete("http://pet-service:3001/api/pets/1");
    }
}
