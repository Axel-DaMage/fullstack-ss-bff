package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.model.PetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class PetServiceClient {

    private final RestTemplate restTemplate;
    private final String petServiceUrl;

    public PetServiceClient(RestTemplate restTemplate,
                           @Value("${pet.service.url:http://pet-service:3001}") String petServiceUrl) {
        this.restTemplate = restTemplate;
        this.petServiceUrl = petServiceUrl;
    }

    public List<PetDto> getAllPets() {
        return restTemplate.exchange(
            petServiceUrl + "/api/pets",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<PetDto>>() {}
        ).getBody();
    }

    public PetDto getPetById(Long id) {
        return restTemplate.getForObject(petServiceUrl + "/api/pets/" + id, PetDto.class);
    }

    public List<PetDto> getPetsByStatus(String status) {
        return restTemplate.exchange(
            petServiceUrl + "/api/pets/search/status/" + status,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<PetDto>>() {}
        ).getBody();
    }

    public List<PetDto> getPetsByRace(String race) {
        return restTemplate.exchange(
            petServiceUrl + "/api/pets/search/race/" + race,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<PetDto>>() {}
        ).getBody();
    }

    public PetDto createPet(PetDto pet) {
        return restTemplate.postForObject(petServiceUrl + "/api/pets", pet, PetDto.class);
    }

    public PetDto updatePet(Long id, PetDto pet) {
        restTemplate.put(petServiceUrl + "/api/pets/" + id, pet);
        return getPetById(id);
    }

    public void deletePet(Long id) {
        restTemplate.delete(petServiceUrl + "/api/pets/" + id);
    }
}