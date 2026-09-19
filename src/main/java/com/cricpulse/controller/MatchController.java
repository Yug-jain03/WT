package com.cricpulse.controller;

import com.cricpulse.dto.ScoreEventRequest;
import com.cricpulse.dto.ScoreEventResponse;
import com.cricpulse.model.CommentaryEvent;
import com.cricpulse.model.Match;
import com.cricpulse.model.PlayerStat;
import com.cricpulse.service.MatchService;
import com.cricpulse.service.ScoringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;
    private final ScoringService scoringService;

    public MatchController(MatchService matchService, ScoringService scoringService) {
        this.matchService = matchService;
        this.scoringService = scoringService;
    }

    /**
     * Get list of all fixtures/matches for the dashboard sidebar.
     */
    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    /**
     * Get comprehensive details for a specific match including its innings.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get chronological delivery feed and commentary for a match.
     */
    @GetMapping("/{id}/commentary")
    public List<CommentaryEvent> getCommentary(@PathVariable Long id) {
        return matchService.getCommentaryForMatch(id);
    }

    /**
     * Get player statistics (batting and bowling cards) for a specific innings.
     */
    @GetMapping("/{id}/innings/{inningsId}/stats")
    public List<PlayerStat> getInningsStats(@PathVariable Long id, @PathVariable Long inningsId) {
        return matchService.getStatsForInnings(inningsId);
    }

    /**
     * Ingest a ball-by-ball score event or wicket from quick controls or simulation.
     */
    @PostMapping("/{id}/events")
    public ResponseEntity<ScoreEventResponse> recordEvent(@PathVariable Long id,
                                                         @RequestBody ScoreEventRequest request) {
        try {
            ScoreEventResponse response = scoringService.recordEvent(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
