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

    public LocationDto updateLocation(Long id, LocationDto location) {
        return locationServiceClient.updateLocation(id, location);
    }

    public List<MatchDto> getAllMatches() {
        List<MatchDto> matches = matchServiceClient.getAllMatches();
        matches.forEach(this::enrichMatch);
        return matches;
    }

    private void enrichMatch(MatchDto match) {
        if (match.getPetLostId() != null) {
            PetDto pet = petServiceClient.getPetById(match.getPetLostId());
            if (pet != null) {
                match.setPetLost(pet);
                match.setPetLostName(pet.getName());
            }
        }
        if (match.getPetFoundId() != null) {
            PetDto pet = petServiceClient.getPetById(match.getPetFoundId());
            if (pet != null) {
                match.setPetFound(pet);
                match.setPetFoundName(pet.getName());
            }
        }
    }

    public MatchDto getMatchById(Long id) {
        MatchDto match = matchServiceClient.getMatchById(id);
        if (match != null) enrichMatch(match);
        return match;
    }

    public MatchDto createMatch(Long petLostId, Long petFoundId) {
        MatchDto match = matchServiceClient.createMatch(petLostId, petFoundId);
        if (match != null) enrichMatch(match);
        return match;
    }

    public MatchDto confirmMatch(Long id) {
        MatchDto match = matchServiceClient.updateMatchStatus(id, "CONFIRMED");
        if (match != null) enrichMatch(match);
        return match;
    }

    public MatchDto rejectMatch(Long id) {
        MatchDto match = matchServiceClient.updateMatchStatus(id, "REJECTED");
        if (match != null) enrichMatch(match);
        return match;
    }

    public void deleteMatch(Long id) {
        matchServiceClient.deleteMatch(id);
    }

    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        List<PetDto> lostPets = petServiceClient.getPetsByStatus("PERDIDO");
        List<PetDto> foundPets = petServiceClient.getPetsByStatus("ENCONTRADO");
            List<MatchDto> pendingMatches = matchServiceClient.getMatchesByStatus("PENDIENTE");
        List<LocationDto> locations = locationServiceClient.getAllLocations();

        dashboard.put("lostPets", lostPets.size());
        dashboard.put("foundPets", foundPets.size());
        dashboard.put("pendingMatches", pendingMatches.size());
        dashboard.put("totalLocations", locations.size());

        Map<String, Long> locationByZone = new HashMap<>();
        for (LocationDto loc : locations) {
            String zone = determineZone(loc.getLatitude(), loc.getLongitude(), loc.getZone());
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

    private String determineZone(Double latitude, Double longitude, String existingZone) {
        if (existingZone != null && !existingZone.isEmpty()) {
            return existingZone;
        }
        if (latitude == null || longitude == null) {
            return "Sin asignar";
        }

        double lat = latitude;
        double lon = longitude;

        if (lat >= -33.55 && lat <= -33.35 && lon >= -70.85 && lon <= -70.50) {
            if (lat >= -33.50 && lat <= -33.42 && lon >= -70.70 && lon <= -70.60) {
                return "Santiago Centro";
            } else if (lon >= -70.70 && lon <= -70.55) {
                return "Las Condes";
            } else if (lon >= -70.65 && lon <= -70.55 && lat >= -33.45 && lat <= -33.38) {
                return "Providencia";
            } else if (lat <= -33.48 && lon >= -70.75 && lon <= -70.60) {
                return "Maipú";
            } else if (lat >= -33.45 && lat <= -33.38 && lon >= -70.60 && lon <= -70.50) {
                return "Ñuñoa";
            } else if (lat >= -33.55 && lat <= -33.48 && lon >= -70.80 && lon <= -70.70) {
                return "Puente Alto";
            } else if (lon >= -70.58 && lon <= -70.50 && lat >= -33.42 && lat <= -33.35) {
                return "Vitacura";
            } else if (lat <= -33.50 && lon >= -70.70 && lon <= -70.58) {
                return "La Florida";
            } else if (lat >= -33.42 && lat <= -33.35 && lon >= -70.65 && lon <= -70.55) {
                return "Santiago Centro";
            }
            return "Santiago";
        } else if (lat >= -34.0 && lat <= -33.0 && lon >= -72.0 && lon <= -70.0) {
            return "Región Metropolitana";
        }
        return "Sin coordenadas válidas";
    }
}
