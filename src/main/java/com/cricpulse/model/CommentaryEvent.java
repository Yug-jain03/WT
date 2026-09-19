package com.cricpulse.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "commentary_events")
public class CommentaryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long matchId;
    private Long inningsId;

    private Integer overNumber = 0;
    private Integer ballNumber = 0;

    private Integer runsOffBat = 0;
    private Integer extras = 0;

    private Boolean wicket = false;
    private String wicketType;

    @Column(length = 1000)
    private String commentary;

    private String striker;
    private String nonStriker;
    private String bowler;

    private Boolean legalDelivery = true;
    private Instant timestamp;

    public CommentaryEvent() {
        this.timestamp = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public Long getInningsId() {
        return inningsId;
    }

    public void setInningsId(Long inningsId) {
        this.inningsId = inningsId;
    }

    public Integer getOverNumber() {
        return overNumber;
    }

    public void setOverNumber(Integer overNumber) {
        this.overNumber = overNumber;
    }

    public Integer getBallNumber() {
        return ballNumber;
    }

    public void setBallNumber(Integer ballNumber) {
        this.ballNumber = ballNumber;
    }

    public Integer getRunsOffBat() {
        return runsOffBat;
    }

    public void setRunsOffBat(Integer runsOffBat) {
        this.runsOffBat = runsOffBat;
    }

    public Integer getExtras() {
        return extras;
    }

    public void setExtras(Integer extras) {
        this.extras = extras;
    }

    public Boolean getWicket() {
        return wicket;
    }

    public void setWicket(Boolean wicket) {
        this.wicket = wicket;
    }

    public String getWicketType() {
        return wicketType;
    }

    public void setWicketType(String wicketType) {
        this.wicketType = wicketType;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getStriker() {
        return striker;
    }

    public void setStriker(String striker) {
        this.striker = striker;
    }

    public String getNonStriker() {
        return nonStriker;
    }

    public void setNonStriker(String nonStriker) {
        this.nonStriker = nonStriker;
    }

    public String getBowler() {
        return bowler;
    }

    public void setBowler(String bowler) {
        this.bowler = bowler;
    }

    public Boolean getLegalDelivery() {
        return legalDelivery;
    }

    public void setLegalDelivery(Boolean legalDelivery) {
        this.legalDelivery = legalDelivery;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
