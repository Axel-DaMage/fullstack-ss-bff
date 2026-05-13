package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.model.LocationDto;
import com.sanosysalvos.bff.model.MatchDto;
import com.sanosysalvos.bff.model.PetDto;
import com.sanosysalvos.bff.service.AggregationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BffController {

    private final AggregationService aggregationService;

    public BffController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/pets")
    public ResponseEntity<List<PetDto>> getAllPets() {
        return ResponseEntity.ok(aggregationService.getAllPets());
    }

    @GetMapping("/pets/{id}")
    public ResponseEntity<PetDto> getPetById(@PathVariable Long id) {
        PetDto pet = aggregationService.getPetById(id);
        return pet != null ? ResponseEntity.ok(pet) : ResponseEntity.notFound().build();
    }

    @PostMapping("/pets")
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto pet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aggregationService.createPet(pet));
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<PetDto> updatePet(@PathVariable Long id, @RequestBody PetDto pet) {
        return ResponseEntity.ok(aggregationService.updatePet(id, pet));
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        aggregationService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(aggregationService.getAllLocations());
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        LocationDto location = aggregationService.getLocationById(id);
        return location != null ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    @GetMapping("/locations/zone/{zone}")
    public ResponseEntity<List<LocationDto>> getLocationsByZone(@PathVariable String zone) {
        return ResponseEntity.ok(aggregationService.getLocationsByZone(zone));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchDto>> getAllMatches() {
        return ResponseEntity.ok(aggregationService.getAllMatches());
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchDto> getMatchById(@PathVariable Long id) {
        MatchDto match = aggregationService.getMatchById(id);
        return match != null ? ResponseEntity.ok(match) : ResponseEntity.notFound().build();
    }

    @PostMapping("/matches")
    public ResponseEntity<MatchDto> createMatch(@RequestBody Map<String, Long> request) {
        Long petLostId = request.get("petLostId");
        Long petFoundId = request.get("petFoundId");
        if (petLostId == null || petFoundId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(aggregationService.createMatch(petLostId, petFoundId));
    }

    @PutMapping("/matches/{id}/confirm")
    public ResponseEntity<MatchDto> confirmMatch(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationService.confirmMatch(id));
    }

    @PutMapping("/matches/{id}/reject")
    public ResponseEntity<MatchDto> rejectMatch(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationService.rejectMatch(id));
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        aggregationService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(aggregationService.getDashboard());
    }

    @GetMapping("/pets/{id}/with-location")
    public ResponseEntity<Map<String, Object>> getPetWithLocation(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationService.getPetWithLocation(id));
    }

    @PostMapping("/matching/run-automatic")
    public ResponseEntity<String> runAutomaticMatching() {
        aggregationService.runAutomaticMatching();
        return ResponseEntity.ok("Automatic matching triggered");
    }
}