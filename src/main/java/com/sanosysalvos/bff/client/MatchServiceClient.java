package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.model.MatchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
public class MatchServiceClient {

    private final RestTemplate restTemplate;
    private final String matchServiceUrl;

    public MatchServiceClient(RestTemplate restTemplate,
                              @Value("${match.service.url:http://match-service:3003}") String matchServiceUrl) {
        this.restTemplate = restTemplate;
        this.matchServiceUrl = matchServiceUrl;
    }

    public List<MatchDto> getAllMatches() {
        return restTemplate.exchange(
            matchServiceUrl + "/api/matching",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MatchDto>>() {}
        ).getBody();
    }

    public MatchDto getMatchById(Long id) {
        return restTemplate.getForObject(matchServiceUrl + "/api/matching/" + id, MatchDto.class);
    }

    public List<MatchDto> getMatchesByStatus(String status) {
        return restTemplate.exchange(
            matchServiceUrl + "/api/matching/search/status/" + status,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MatchDto>>() {}
        ).getBody();
    }

    public List<MatchDto> getMatchesByPercentage(Integer percentage) {
        return restTemplate.exchange(
            matchServiceUrl + "/api/matching/search/percentage/" + percentage,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<MatchDto>>() {}
        ).getBody();
    }

    public MatchDto createMatch(Long petLostId, Long petFoundId) {
        return restTemplate.postForObject(
            matchServiceUrl + "/api/matching",
            Map.of("petLostId", petLostId, "petFoundId", petFoundId),
            MatchDto.class
        );
    }

    public MatchDto updateMatchStatus(Long id, String status) {
        restTemplate.put(matchServiceUrl + "/api/matching/" + id, Map.of("status", status));
        return getMatchById(id);
    }

    public void deleteMatch(Long id) {
        restTemplate.delete(matchServiceUrl + "/api/matching/" + id);
    }

    public void runAutomaticMatching() {
        restTemplate.postForObject(matchServiceUrl + "/api/matching/run-automatic", null, String.class);
    }
}