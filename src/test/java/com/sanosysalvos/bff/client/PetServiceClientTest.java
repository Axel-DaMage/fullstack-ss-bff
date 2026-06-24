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
    void getAllPets_ShouldCallCorrectUrl() {
        when(restTemplate.exchange(
            eq("http://pet-service:3001/api/pets"),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        List<PetDto> result = client.getAllPets();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPetById_ShouldReturnPetOrNull() {
        PetDto expected = new PetDto();
        expected.setId(1L);
        when(restTemplate.getForObject("http://pet-service:3001/api/pets/1", PetDto.class))
            .thenReturn(expected);
        assertEquals(1L, client.getPetById(1L).getId());

        when(restTemplate.getForObject("http://pet-service:3001/api/pets/99", PetDto.class))
            .thenReturn(null);
        assertNull(client.getPetById(99L));
    }

    @Test
    void createPet_ShouldPostAndReturn() {
        PetDto pet = new PetDto();
        pet.setName("Firulais");
        when(restTemplate.postForObject("http://pet-service:3001/api/pets", pet, PetDto.class))
            .thenReturn(pet);

        PetDto result = client.createPet(pet);
        assertEquals("Firulais", result.getName());
    }
}
