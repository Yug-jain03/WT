package com.cricpulse.dto;

public class ScoreEventResponse {

    private String status;
    private String overs;
    private Integer runs;
    private Integer wickets;
    private String commentary;

    public ScoreEventResponse() {}

    public ScoreEventResponse(String status, String overs, Integer runs, Integer wickets, String commentary) {
        this.status = status;
        this.overs = overs;
        this.runs = runs;
        this.wickets = wickets;
        this.commentary = commentary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOvers() {
        return overs;
    }

    public void setOvers(String overs) {
        this.overs = overs;
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

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }
}
