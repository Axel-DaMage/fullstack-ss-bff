package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.model.PetDto;
import com.sanosysalvos.bff.service.AggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    void healthRetornaOk() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("BFF is running"));
    }

    @Test
    void obtenerMascotaRetorna200() throws Exception {
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
    void obtenerMascotaRetorna404() throws Exception {
        when(aggregationService.getPetById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/pets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void dashboardRetornaEstadisticas() throws Exception {
        Map<String, Object> stats = Map.of("lostPets", 5, "foundPets", 3);
        when(aggregationService.getDashboard()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lostPets").value(5))
            .andExpect(jsonPath("$.foundPets").value(3));
    }

    @Test
    void crearMatchRetorna400() throws Exception {
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
