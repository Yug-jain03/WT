package com.cricpulse.dto;

public class ScoreEventRequest {

    private Integer runs = 0;
    private Boolean wicket = false;
    private String wicketType = "";
    private String commentary = "";
    private String striker;
    private String nonStriker;
    private String bowler;
    private Boolean legalDelivery = true;
    private Integer extras = 0;

    public ScoreEventRequest() {}

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
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

    public Integer getExtras() {
        return extras;
    }

    public void setExtras(Integer extras) {
        this.extras = extras;
    }
}
