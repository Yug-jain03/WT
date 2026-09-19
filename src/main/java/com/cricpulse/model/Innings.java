package com.cricpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "innings")
public class Innings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @JsonBackReference
    private Match match;

    private Integer numberInMatch; // 1, 2
    private String battingTeam;
    private Integer runs = 0;
    private Integer wickets = 0;
    private Integer legalBalls = 0;
    private Integer target;

    public Innings() {}

    public Innings(Integer numberInMatch, String battingTeam, Integer runs, Integer wickets, Integer legalBalls, Integer target) {
        this.numberInMatch = numberInMatch;
        this.battingTeam = battingTeam;
        this.runs = runs;
        this.wickets = wickets;
        this.legalBalls = legalBalls;
        this.target = target;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Integer getNumberInMatch() {
        return numberInMatch;
    }

    public void setNumberInMatch(Integer numberInMatch) {
        this.numberInMatch = numberInMatch;
    }

    public String getBattingTeam() {
        return battingTeam;
    }

    public void setBattingTeam(String battingTeam) {
        this.battingTeam = battingTeam;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
    }

    public Integer getWickets() {
        return wickets;
    }

    public void setWickets(Integer wickets) {
        this.wickets = wickets;
    }

    public Integer getLegalBalls() {
        return legalBalls;
    }

    public void setLegalBalls(Integer legalBalls) {
        this.legalBalls = legalBalls;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }
}
