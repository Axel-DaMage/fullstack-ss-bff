package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.model.LocationDto;
import com.sanosysalvos.bff.model.MatchDto;
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
    void health_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("BFF is running"));
    }

    @Test
    void getAllPets_ShouldReturnList() throws Exception {
        when(aggregationService.getAllPets()).thenReturn(List.of());
        mockMvc.perform(get("/api/pets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getPetById_ShouldReturnPet() throws Exception {
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
    void getPetById_ShouldReturn404WhenNotFound() throws Exception {
        when(aggregationService.getPetById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/pets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createPet_ShouldReturn201() throws Exception {
        PetDto pet = new PetDto();
        pet.setId(1L);
        pet.setName("Firulais");
        when(aggregationService.createPet(any())).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Firulais\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Firulais"));
    }

    @Test
    void updatePet_ShouldReturnUpdated() throws Exception {
        PetDto pet = new PetDto();
        pet.setId(1L);
        pet.setName("Updated");
        when(aggregationService.updatePet(eq(1L), any())).thenReturn(pet);

        mockMvc.perform(put("/api/pets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deletePet_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/pets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void getAllLocations_ShouldReturnList() throws Exception {
        when(aggregationService.getAllLocations()).thenReturn(List.of());
        mockMvc.perform(get("/api/locations"))
            .andExpect(status().isOk());
    }

    @Test
    void getLocationById_ShouldReturnLocation() throws Exception {
        LocationDto loc = new LocationDto();
        loc.setId(1L);
        when(aggregationService.getLocationById(1L)).thenReturn(loc);
        mockMvc.perform(get("/api/locations/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getLocationById_ShouldReturn404WhenNotFound() throws Exception {
        when(aggregationService.getLocationById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/locations/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getLocationsByZone_ShouldReturnFiltered() throws Exception {
        when(aggregationService.getLocationsByZone("Centro")).thenReturn(List.of());
        mockMvc.perform(get("/api/locations/zone/Centro"))
            .andExpect(status().isOk());
    }

    @Test
    void updateLocation_ShouldReturnUpdated() throws Exception {
        LocationDto loc = new LocationDto();
        loc.setId(1L);
        when(aggregationService.updateLocation(eq(1L), any())).thenReturn(loc);
        mockMvc.perform(put("/api/locations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"zone\":\"Centro\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void getAllMatches_ShouldReturnList() throws Exception {
        when(aggregationService.getAllMatches()).thenReturn(List.of());
        mockMvc.perform(get("/api/matches"))
            .andExpect(status().isOk());
    }

    @Test
    void getMatchById_ShouldReturnMatch() throws Exception {
        MatchDto match = new MatchDto();
        match.setId(1L);
        when(aggregationService.getMatchById(1L)).thenReturn(match);
        mockMvc.perform(get("/api/matches/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getMatchById_ShouldReturn404WhenNotFound() throws Exception {
        when(aggregationService.getMatchById(99L)).thenReturn(null);
        mockMvc.perform(get("/api/matches/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createMatch_ShouldReturn201() throws Exception {
        MatchDto match = new MatchDto();
        match.setId(1L);
        when(aggregationService.createMatch(1L, 2L)).thenReturn(match);

        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petLostId\":1,\"petFoundId\":2}"))
            .andExpect(status().isCreated());
    }

    @Test
    void createMatch_ShouldReturn400WhenMissingIds() throws Exception {
        mockMvc.perform(post("/api/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void confirmMatch_ShouldReturnOk() throws Exception {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setStatus("CONFIRMED");
        when(aggregationService.confirmMatch(1L)).thenReturn(match);

        mockMvc.perform(put("/api/matches/1/confirm"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CONFIRMED"));
    }

    @Test
    void rejectMatch_ShouldReturnOk() throws Exception {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setStatus("REJECTED");
        when(aggregationService.rejectMatch(1L)).thenReturn(match);

        mockMvc.perform(put("/api/matches/1/reject"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("REJECTED"));
    }

    @Test
    void deleteMatch_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/matches/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void getDashboard_ShouldReturnStats() throws Exception {
        Map<String, Object> stats = Map.of("lostPets", 5, "foundPets", 3);
        when(aggregationService.getDashboard()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lostPets").value(5))
            .andExpect(jsonPath("$.foundPets").value(3));
    }

    @Test
    void getPetWithLocation_ShouldReturnCombined() throws Exception {
        when(aggregationService.getPetWithLocation(1L)).thenReturn(Map.of("pet", new PetDto()));
        mockMvc.perform(get("/api/pets/1/with-location"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pet").exists());
    }

    @Test
    void runAutomaticMatching_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/matching/run-automatic"))
            .andExpect(status().isOk())
            .andExpect(content().string("Automatic matching triggered"));
    }
}
