package com.sanosysalvos.bff.model;

import java.time.LocalDateTime;
import java.util.List;

public class MatchDto {
    private Long id;
    private Long petLostId;
    private Long petFoundId;
    private Integer matchPercentage;
    private String status;
    private LocalDateTime createdAt;
    private PetDto petLost;
    private PetDto petFound;
    private List<MatchCriteriaDto> criteria;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPetLostId() { return petLostId; }
    public void setPetLostId(Long petLostId) { this.petLostId = petLostId; }

    public Long getPetFoundId() { return petFoundId; }
    public void setPetFoundId(Long petFoundId) { this.petFoundId = petFoundId; }

    public Integer getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public PetDto getPetLost() { return petLost; }
    public void setPetLost(PetDto petLost) { this.petLost = petLost; }

    public PetDto getPetFound() { return petFound; }
    public void setPetFound(PetDto petFound) { this.petFound = petFound; }

    public List<MatchCriteriaDto> getCriteria() { return criteria; }
    public void setCriteria(List<MatchCriteriaDto> criteria) { this.criteria = criteria; }

    public static class MatchCriteriaDto {
        private String criteriaName;
        private Integer score;

        public String getCriteriaName() { return criteriaName; }
        public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }
}