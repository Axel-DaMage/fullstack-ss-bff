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
    void getAllPets_ShouldReturnListFromClient() {
        List<PetDto> expected = List.of(new PetDto());
        when(petServiceClient.getAllPets()).thenReturn(expected);
        assertEquals(expected, aggregationService.getAllPets());
    }

    @Test
    void getPetById_ShouldReturnPet() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        when(petServiceClient.getPetById(1L)).thenReturn(pet);
        assertEquals(pet, aggregationService.getPetById(1L));
    }

    @Test
    void getPetById_ShouldReturnNullWhenNotFound() {
        when(petServiceClient.getPetById(99L)).thenReturn(null);
        assertNull(aggregationService.getPetById(99L));
    }

    @Test
    void createPet_ShouldDelegateAndReturn() {
        PetDto pet = new PetDto();
        pet.setName("Firulais");
        when(petServiceClient.createPet(pet)).thenReturn(pet);
        assertEquals(pet, aggregationService.createPet(pet));
    }

    @Test
    void updatePet_ShouldDelegateAndReturn() {
        PetDto pet = new PetDto();
        pet.setName("Firulais Updated");
        when(petServiceClient.updatePet(1L, pet)).thenReturn(pet);
        assertEquals(pet, aggregationService.updatePet(1L, pet));
    }

    @Test
    void deletePet_ShouldDelegate() {
        aggregationService.deletePet(1L);
        verify(petServiceClient).deletePet(1L);
    }

    @Test
    void getAllLocations_ShouldReturnList() {
        List<LocationDto> expected = List.of(new LocationDto());
        when(locationServiceClient.getAllLocations()).thenReturn(expected);
        assertEquals(expected, aggregationService.getAllLocations());
    }

    @Test
    void getLocationById_ShouldReturnLocation() {
        LocationDto loc = new LocationDto();
        loc.setId(1L);
        when(locationServiceClient.getLocationById(1L)).thenReturn(loc);
        assertEquals(loc, aggregationService.getLocationById(1L));
    }

    @Test
    void getLocationsByZone_ShouldReturnFiltered() {
        List<LocationDto> expected = List.of(new LocationDto());
        when(locationServiceClient.getLocationsByZone("Santiago Centro")).thenReturn(expected);
        assertEquals(expected, aggregationService.getLocationsByZone("Santiago Centro"));
    }

    @Test
    void updateLocation_ShouldDelegate() {
        LocationDto loc = new LocationDto();
        when(locationServiceClient.updateLocation(1L, loc)).thenReturn(loc);
        assertEquals(loc, aggregationService.updateLocation(1L, loc));
    }

    @Test
    void getAllMatches_ShouldReturnEnrichedMatches() {
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
        assertEquals(lostPet, result.get(0).getPetLost());
        assertEquals(foundPet, result.get(0).getPetFound());
    }

    @Test
    void getMatchById_ShouldReturnEnriched() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        pet.setName("Firulais");

        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setPetLostId(1L);

        when(matchServiceClient.getMatchById(1L)).thenReturn(match);
        when(petServiceClient.getPetById(1L)).thenReturn(pet);

        MatchDto result = aggregationService.getMatchById(1L);
        assertEquals("Firulais", result.getPetLostName());
    }

    @Test
    void getMatchById_ShouldReturnNullWhenNotFound() {
        when(matchServiceClient.getMatchById(99L)).thenReturn(null);
        assertNull(aggregationService.getMatchById(99L));
        verify(petServiceClient, never()).getPetById(any());
    }

    @Test
    void createMatch_ShouldDelegateAndEnrich() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        pet.setName("Firulais");

        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setPetLostId(1L);

        when(matchServiceClient.createMatch(1L, 2L)).thenReturn(match);
        when(petServiceClient.getPetById(1L)).thenReturn(pet);

        MatchDto result = aggregationService.createMatch(1L, 2L);
        assertEquals("Firulais", result.getPetLostName());
    }

    @Test
    void confirmMatch_ShouldReturnConfirmed() {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setStatus("CONFIRMED");

        when(matchServiceClient.updateMatchStatus(1L, "CONFIRMED")).thenReturn(match);

        MatchDto result = aggregationService.confirmMatch(1L);
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void rejectMatch_ShouldReturnRejected() {
        MatchDto match = new MatchDto();
        match.setId(1L);
        match.setStatus("REJECTED");

        when(matchServiceClient.updateMatchStatus(1L, "REJECTED")).thenReturn(match);

        MatchDto result = aggregationService.rejectMatch(1L);
        assertEquals("REJECTED", result.getStatus());
    }

    @Test
    void deleteMatch_ShouldDelegate() {
        aggregationService.deleteMatch(1L);
        verify(matchServiceClient).deleteMatch(1L);
    }

    @Test
    void getDashboard_ShouldReturnAggregatedStats() {
        PetDto lostPet = new PetDto();
        lostPet.setStatus("PERDIDO");
        PetDto foundPet = new PetDto();
        foundPet.setStatus("ENCONTRADO");
        LocationDto loc1 = new LocationDto();
        loc1.setLatitude(-33.45);
        loc1.setLongitude(-70.65);
        LocationDto loc2 = new LocationDto();
        loc2.setLatitude(-33.45);
        loc2.setLongitude(-70.65);

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(lostPet));
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(foundPet));
        when(matchServiceClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(loc1, loc2));

        Map<String, Object> dashboard = aggregationService.getDashboard();
        assertEquals(1, dashboard.get("lostPets"));
        assertEquals(1, dashboard.get("foundPets"));
        assertEquals(0, dashboard.get("pendingMatches"));
        assertEquals(2, dashboard.get("totalLocations"));
        assertTrue(dashboard.containsKey("locationsByZone"));
    }

    @Test
    void getPetWithLocation_ShouldReturnPetAndLocation() {
        PetDto pet = new PetDto();
        pet.setId(1L);
        LocationDto loc = new LocationDto();
        loc.setPetId(1L);
        LocationDto otherLoc = new LocationDto();
        otherLoc.setPetId(2L);

        when(petServiceClient.getPetById(1L)).thenReturn(pet);
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(loc, otherLoc));

        Map<String, Object> result = aggregationService.getPetWithLocation(1L);
        assertEquals(pet, result.get("pet"));
        assertEquals(loc, result.get("location"));
    }

    @Test
    void getPetWithLocation_ShouldReturnNullLocationWhenNotFound() {
        PetDto pet = new PetDto();
        pet.setId(1L);

        when(petServiceClient.getPetById(1L)).thenReturn(pet);
        when(locationServiceClient.getAllLocations()).thenReturn(List.of());

        Map<String, Object> result = aggregationService.getPetWithLocation(1L);
        assertNull(result.get("location"));
    }

    @Test
    void runAutomaticMatching_ShouldDelegate() {
        aggregationService.runAutomaticMatching();
        verify(matchServiceClient).runAutomaticMatching();
    }

    @Test
    void getDashboard_ShouldGroupLocationsByZone() {
        LocationDto locCentro = new LocationDto();
        locCentro.setLatitude(-33.45);
        locCentro.setLongitude(-70.65);
        LocationDto locCondes = new LocationDto();
        locCondes.setLatitude(-33.42);
        locCondes.setLongitude(-70.60);
        LocationDto noCoord = new LocationDto();

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());
        when(matchServiceClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(locCentro, locCondes, noCoord));

        Map<String, Object> result = aggregationService.getDashboard();
        Map<String, Long> zones = (Map<String, Long>) result.get("locationsByZone");
        assertNotNull(zones);
        assertTrue(zones.containsKey("Santiago Centro") || zones.containsKey("Las Condes") || zones.containsKey("Sin asignar"));
    }

    @Test
    void getDashboard_ExistingZoneShouldBePreserved() {
        LocationDto loc = new LocationDto();
        loc.setZone("Maipú");
        loc.setLatitude(-33.45);
        loc.setLongitude(-70.65);

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());
        when(matchServiceClient.getMatchesByStatus("PENDIENTE")).thenReturn(List.of());
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(loc));

        Map<String, Object> result = aggregationService.getDashboard();
        Map<String, Long> zones = (Map<String, Long>) result.get("locationsByZone");
        assertEquals(1L, zones.get("Maipú"));
    }
}
