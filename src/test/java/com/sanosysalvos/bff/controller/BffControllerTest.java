package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.model.PetDto;
import com.sanosysalvos.bff.service.AggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BffController.class)
class BffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AggregationService aggregationService;

    @Test
    void health_DeberiaRetornar200() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("BFF is running"));
    }

    @Test
    void obtenerPets_DeberiaRetornarLista() throws Exception {
        PetDto pet = new PetDto();
        pet.setId(1L);
        when(aggregationService.getAllPets()).thenReturn(List.of(pet));

        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerPetPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
        PetDto pet = new PetDto();
        pet.setId(1L);
        pet.setName("Firulais");
        when(aggregationService.getPetById(1L)).thenReturn(pet);

        mockMvc.perform(get("/api/pets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Firulais"));
    }

    @Test
    void obtenerPetPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(aggregationService.getPetById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/pets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void crearPet_CuandoValido_DeberiaRetornar201() throws Exception {
        PetDto creado = new PetDto();
        creado.setId(1L);
        creado.setName("Nuevo");
        when(aggregationService.createPet(any())).thenReturn(creado);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Nuevo\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarPet_DeberiaRetornar200() throws Exception {
        PetDto actualizado = new PetDto();
        actualizado.setName("Actualizado");
        when(aggregationService.updatePet(eq(1L), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Actualizado\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Actualizado"));
    }

    @Test
    void eliminarPet_DeberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void obtenerLocations_DeberiaRetornarLista() throws Exception {
        when(aggregationService.getAllLocations()).thenReturn(List.of());

        mockMvc.perform(get("/api/locations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerLocationsPorZona_DeberiaRetornarFiltradas() throws Exception {
        when(aggregationService.getLocationsByZone("Centro")).thenReturn(List.of());

        mockMvc.perform(get("/api/locations/zone/Centro"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerMatches_DeberiaRetornarLista() throws Exception {
        when(aggregationService.getAllMatches()).thenReturn(List.of());

        mockMvc.perform(get("/api/matches"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerMatchPorId_DeberiaRetornar200() throws Exception {
        when(aggregationService.getMatchById(1L)).thenReturn(new com.sanosysalvos.bff.model.MatchDto());

        mockMvc.perform(get("/api/matches/1"))
            .andExpect(status().isOk());
    }

    @Test
    void obtenerMatchPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(aggregationService.getMatchById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/matches/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void crearMatch_CuandoValido_DeberiaRetornar201() throws Exception {
        when(aggregationService.createMatch(1L, 2L)).thenReturn(new com.sanosysalvos.bff.model.MatchDto());

        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petLostId\": 1, \"petFoundId\": 2}"))
            .andExpect(status().isCreated());
    }

    @Test
    void crearMatch_SinIds_DeberiaRetornar400() throws Exception {
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void confirmarMatch_DeberiaRetornar200() throws Exception {
        when(aggregationService.confirmMatch(1L)).thenReturn(new com.sanosysalvos.bff.model.MatchDto());

        mockMvc.perform(put("/api/matches/1/confirm"))
            .andExpect(status().isOk());
    }

    @Test
    void rechazarMatch_DeberiaRetornar200() throws Exception {
        when(aggregationService.rejectMatch(1L)).thenReturn(new com.sanosysalvos.bff.model.MatchDto());

        mockMvc.perform(put("/api/matches/1/reject"))
            .andExpect(status().isOk());
    }

    @Test
    void eliminarMatch_DeberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/api/matches/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void dashboard_DeberiaRetornarEstadisticas() throws Exception {
        Map<String, Object> stats = Map.of("lostPets", 5, "foundPets", 3);
        when(aggregationService.getDashboard()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lostPets").value(5))
            .andExpect(jsonPath("$.foundPets").value(3));
    }

    @Test
    void obtenerPetConUbicacion_DeberiaRetornar200() throws Exception {
        when(aggregationService.getPetWithLocation(1L)).thenReturn(Map.of("pet", new PetDto()));

        mockMvc.perform(get("/api/pets/1/with-location"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pet").exists());
    }

    @Test
    void ejecutarMatchingAutomatico_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/matching/run-automatic"))
            .andExpect(status().isOk())
            .andExpect(content().string("Automatic matching triggered"));
    }
}
