package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.client.LocationServiceClient;
import com.sanosysalvos.bff.client.MatchServiceClient;
import com.sanosysalvos.bff.client.PetServiceClient;
import com.sanosysalvos.bff.model.LocationDto;
import com.sanosysalvos.bff.model.MatchDto;
import com.sanosysalvos.bff.model.PetDto;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AggregationService {

    private final PetServiceClient petServiceClient;
    private final LocationServiceClient locationServiceClient;
    private final MatchServiceClient matchServiceClient;

    public AggregationService(PetServiceClient petServiceClient,
                               LocationServiceClient locationServiceClient,
                               MatchServiceClient matchServiceClient) {
        this.petServiceClient = petServiceClient;
        this.locationServiceClient = locationServiceClient;
        this.matchServiceClient = matchServiceClient;
    }

    public List<PetDto> getAllPets() {
        return petServiceClient.getAllPets();
    }

    public PetDto getPetById(Long id) {
        return petServiceClient.getPetById(id);
    }

    public PetDto createPet(PetDto pet) {
        return petServiceClient.createPet(pet);
    }

    public PetDto updatePet(Long id, PetDto pet) {
        return petServiceClient.updatePet(id, pet);
    }

    public void deletePet(Long id) {
        petServiceClient.deletePet(id);
    }

    public List<LocationDto> getAllLocations() {
        return locationServiceClient.getAllLocations();
    }

    public LocationDto getLocationById(Long id) {
        return locationServiceClient.getLocationById(id);
    }

    public List<LocationDto> getLocationsByZone(String zone) {
        return locationServiceClient.getLocationsByZone(zone);
    }

    public List<MatchDto> getAllMatches() {
        return matchServiceClient.getAllMatches();
    }

    public MatchDto getMatchById(Long id) {
        return matchServiceClient.getMatchById(id);
    }

    public MatchDto createMatch(Long petLostId, Long petFoundId) {
        return matchServiceClient.createMatch(petLostId, petFoundId);
    }

    public MatchDto confirmMatch(Long id) {
        return matchServiceClient.updateMatchStatus(id, "CONFIRMED");
    }

    public MatchDto rejectMatch(Long id) {
        return matchServiceClient.updateMatchStatus(id, "REJECTED");
    }

    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        List<PetDto> lostPets = petServiceClient.getPetsByStatus("LOST");
        List<PetDto> foundPets = petServiceClient.getPetsByStatus("FOUND");
        List<MatchDto> pendingMatches = matchServiceClient.getMatchesByStatus("PENDING");
        List<LocationDto> locations = locationServiceClient.getAllLocations();

        dashboard.put("lostPets", lostPets.size());
        dashboard.put("foundPets", foundPets.size());
        dashboard.put("pendingMatches", pendingMatches.size());
        dashboard.put("totalLocations", locations.size());

        Map<String, Long> locationByZone = new HashMap<>();
        for (LocationDto loc : locations) {
            String zone = loc.getZone() != null ? loc.getZone() : "Unknown";
            locationByZone.put(zone, locationByZone.getOrDefault(zone, 0L) + 1);
        }
        dashboard.put("locationsByZone", locationByZone);

        return dashboard;
    }

    public Map<String, Object> getPetWithLocation(Long petId) {
        Map<String, Object> result = new HashMap<>();

        PetDto pet = petServiceClient.getPetById(petId);
        result.put("pet", pet);

        List<LocationDto> locations = locationServiceClient.getAllLocations();
        LocationDto petLocation = locations.stream()
            .filter(l -> l.getPetId() != null && l.getPetId().equals(petId))
            .findFirst()
            .orElse(null);
        result.put("location", petLocation);

        return result;
    }

    public void runAutomaticMatching() {
        matchServiceClient.runAutomaticMatching();
    }
}