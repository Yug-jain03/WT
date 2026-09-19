package com.cricpulse.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamA;

    @Column(nullable = false)
    private String teamB;

    private String venue;
    private String format; // T20, ODI, TEST
    private String status; // LIVE, COMPLETED, UPCOMING

    private Instant startTime;
    private Instant updatedAt;

    private String tossWinner;
    private String tossDecision;

    private Integer currentInnings = 1;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("numberInMatch ASC")
    @JsonManagedReference
    private List<Innings> innings = new ArrayList<>();

    public Match() {
        this.updatedAt = Instant.now();
    }

    public Match(String teamA, String teamB, String venue, String format, String status, Instant startTime) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.venue = venue;
        this.format = format;
        this.status = status;
        this.startTime = startTime;
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTossWinner() {
        return tossWinner;
    }

    public void setTossWinner(String tossWinner) {
        this.tossWinner = tossWinner;
    }

    public String getTossDecision() {
        return tossDecision;
    }

    public void setTossDecision(String tossDecision) {
        this.tossDecision = tossDecision;
    }

    public Integer getCurrentInnings() {
        return currentInnings;
    }

    public void setCurrentInnings(Integer currentInnings) {
        this.currentInnings = currentInnings;
    }

    public List<Innings> getInnings() {
        return innings;
    }

    public void setInnings(List<Innings> innings) {
        this.innings = innings;
    }

    public void addInnings(Innings inning) {
        innings.add(inning);
        inning.setMatch(this);
    }
}
