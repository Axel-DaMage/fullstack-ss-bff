package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.client.LocationServiceClient;
import com.sanosysalvos.bff.client.MatchServiceClient;
import com.sanosysalvos.bff.client.PetServiceClient;
import com.sanosysalvos.bff.model.LocationDto;
import com.sanosysalvos.bff.model.MatchDto;
import com.sanosysalvos.bff.model.PetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private PetServiceClient petServiceClient;
    @Mock
    private LocationServiceClient locationServiceClient;
    @Mock
    private MatchServiceClient matchServiceClient;

    private AggregationService aggregationService;

    @BeforeEach
    void setUp() {
        aggregationService = new AggregationService(petServiceClient, locationServiceClient, matchServiceClient);
    }

    @Test
    void getAllMatches_ShouldEnrichWithPetDetails() {
        PetDto lostPet = new PetDto();
        lostPet.setId(1L);
        lostPet.setName("Perdido");
        PetDto foundPet = new PetDto();
        foundPet.setId(2L);
        foundPet.setName("Encontrado");

        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setPetLostId(1L);
        match.setPetFoundId(2L);

        when(matchServiceClient.getAllMatches()).thenReturn(List.of(match));
        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);

        List<MatchDto> result = aggregationService.getAllMatches();
        assertEquals(1, result.size());
        assertEquals("Perdido", result.get(0).getPetLostName());
        assertEquals("Encontrado", result.get(0).getPetFoundName());
    }

    @Test
    void getMatchById_ShouldNotCallPetServiceWhenMatchNotFound() {
        when(matchServiceClient.getMatchById(99L)).thenReturn(null);
        assertNull(aggregationService.getMatchById(99L));
        verify(petServiceClient, never()).getPetById(any());
    }

    @Test
    void getDashboard_ShouldReturnAggregatedStats() {
        PetDto lostPet = new PetDto();
        lostPet.setStatus("PERDIDO");
        PetDto foundPet = new PetDto();
        foundPet.setStatus("ENCONTRADO");
        LocationDto loc = new LocationDto();
        loc.setLatitude(-33.45);
        loc.setLongitude(-70.65);

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(lostPet));
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(foundPet));
        when(matchServiceClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(loc));

        Map<String, Object> dashboard = aggregationService.getDashboard();
        assertEquals(1, dashboard.get("lostPets"));
        assertEquals(1, dashboard.get("foundPets"));
        assertEquals(0, dashboard.get("pendingMatches"));
        assertEquals(1, dashboard.get("totalLocations"));
        assertTrue(dashboard.containsKey("locationsByZone"));
    }

    @Test
    void getDashboard_ShouldGroupLocationsByZone() {
        LocationDto locCentro = new LocationDto();
        locCentro.setLatitude(-33.45);
        locCentro.setLongitude(-70.65);
        LocationDto locCondes = new LocationDto();
        locCondes.setLatitude(-33.43);
        locCondes.setLongitude(-70.55);

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());
        when(matchServiceClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(locCentro, locCondes));

        Map<String, Object> result = aggregationService.getDashboard();
        Map<String, Long> zones = (Map<String, Long>) result.get("locationsByZone");
        assertEquals(2, zones.size());
    }

    @Test
    void getPetWithLocation_ShouldReturnPetAndLocation() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        LocationDto loc = new LocationDto();
        loc.setPetId(1L);

        when(petServiceClient.getPetById(1L)).thenReturn(pet);
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(loc));

        Map<String, Object> result = aggregationService.getPetWithLocation(1L);
        assertEquals(pet, result.get("pet"));
        assertEquals(loc, result.get("location"));
    }
}
