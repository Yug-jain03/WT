package com.cricpulse.service;

import com.cricpulse.dto.ScoreEventRequest;
import com.cricpulse.dto.ScoreEventResponse;
import com.cricpulse.model.Innings;
import com.cricpulse.model.Match;
import com.cricpulse.model.PlayerStat;
import com.cricpulse.repository.InningsRepository;
import com.cricpulse.repository.MatchRepository;
import com.cricpulse.repository.PlayerStatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ScoringServiceTest {

    @Autowired
    private ScoringService scoringService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Autowired
    private PlayerStatRepository playerStatRepository;

    @Test
    void testRecordBoundaryEvent() {
        // Create a test match
        Match match = new Match("India", "Australia", "Eden Gardens", "T20", "LIVE", Instant.now());
        Innings innings = new Innings(1, "India", 100, 2, 60, null);
        match.addInnings(innings);
        match = matchRepository.save(match);

        ScoreEventRequest request = new ScoreEventRequest();
        request.setRuns(4);
        request.setWicket(false);
        request.setStriker("Virat Kohli");
        request.setBowler("Mitchell Starc");
        request.setLegalDelivery(true);
        request.setCommentary("FOUR! Glorious cover drive!");

        ScoreEventResponse response = scoringService.recordEvent(match.getId(), request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(104, response.getRuns());
        assertEquals(2, response.getWickets());
        assertEquals("10.1", response.getOvers());

        // Verify striker stat
        Optional<PlayerStat> strikerStat = playerStatRepository.findByInningsIdAndPlayer(innings.getId(), "Virat Kohli");
        assertTrue(strikerStat.isPresent());
        assertEquals(4, strikerStat.get().getRuns());
        assertEquals(1, strikerStat.get().getBalls());
        assertEquals(1, strikerStat.get().getFours());
    }

    @Test
    void testRecordWicketEvent() {
        Match match = new Match("India", "Australia", "Eden Gardens", "T20", "LIVE", Instant.now());
        Innings innings = new Innings(1, "India", 50, 1, 30, null);
        match.addInnings(innings);
        match = matchRepository.save(match);

        ScoreEventRequest request = new ScoreEventRequest();
        request.setRuns(0);
        request.setWicket(true);
        request.setWicketType("bowled");
        request.setStriker("Rohit Sharma");
        request.setBowler("Pat Cummins");
        request.setLegalDelivery(true);

        ScoreEventResponse response = scoringService.recordEvent(match.getId(), request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(50, response.getRuns());
        assertEquals(2, response.getWickets());
        assertEquals("5.1", response.getOvers());

        Optional<PlayerStat> strikerStat = playerStatRepository.findByInningsIdAndPlayer(innings.getId(), "Rohit Sharma");
        assertTrue(strikerStat.isPresent());
        assertTrue(strikerStat.get().getOut());

        Optional<PlayerStat> bowlerStat = playerStatRepository.findByInningsIdAndPlayer(innings.getId(), "Pat Cummins");
        assertTrue(bowlerStat.isPresent());
        assertEquals(1, bowlerStat.get().getWickets());
    }
}
