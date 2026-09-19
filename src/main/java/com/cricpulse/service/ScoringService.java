package com.cricpulse.service;

import com.cricpulse.dto.ScoreEventRequest;
import com.cricpulse.dto.ScoreEventResponse;
import com.cricpulse.model.CommentaryEvent;
import com.cricpulse.model.Innings;
import com.cricpulse.model.Match;
import com.cricpulse.model.PlayerStat;
import com.cricpulse.repository.CommentaryEventRepository;
import com.cricpulse.repository.InningsRepository;
import com.cricpulse.repository.MatchRepository;
import com.cricpulse.repository.PlayerStatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ScoringService {

    private static final Logger log = LoggerFactory.getLogger(ScoringService.class);

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final PlayerStatRepository playerStatRepository;
    private final CommentaryEventRepository commentaryEventRepository;
    private final SseService sseService;

    public ScoringService(MatchRepository matchRepository,
                          InningsRepository inningsRepository,
                          PlayerStatRepository playerStatRepository,
                          CommentaryEventRepository commentaryEventRepository,
                          SseService sseService) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.playerStatRepository = playerStatRepository;
        this.commentaryEventRepository = commentaryEventRepository;
        this.sseService = sseService;
    }

    @Transactional
    public ScoreEventResponse recordEvent(Long matchId, ScoreEventRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Innings currentInnings = match.getInnings().stream()
                .filter(i -> i.getNumberInMatch().equals(match.getCurrentInnings()))
                .findFirst()
                .orElseGet(() -> match.getInnings().isEmpty() ? null : match.getInnings().get(0));

        if (currentInnings == null) {
            throw new IllegalStateException("No active innings found for match: " + matchId);
        }

        int runs = request.getRuns() != null ? request.getRuns() : 0;
        int extras = request.getExtras() != null ? request.getExtras() : 0;
        int totalRuns = runs + extras;
        boolean isWicket = Boolean.TRUE.equals(request.getWicket());
        boolean isLegal = request.getLegalDelivery() == null || request.getLegalDelivery();

        // 1. Update Innings Score
        currentInnings.setRuns(currentInnings.getRuns() + totalRuns);
        if (isWicket) {
            currentInnings.setWickets(currentInnings.getWickets() + 1);
        }
        if (isLegal) {
            currentInnings.setLegalBalls(currentInnings.getLegalBalls() + 1);
        }

        int totalLegalBalls = currentInnings.getLegalBalls();
        int overNumber = totalLegalBalls > 0 ? (totalLegalBalls - 1) / 6 : 0;
        int ballNumber = totalLegalBalls > 0 ? ((totalLegalBalls - 1) % 6) + 1 : 1;
        String oversFormatted = (totalLegalBalls / 6) + "." + (totalLegalBalls % 6);

        // 2. Update Batter Statistics
        String striker = request.getStriker() != null ? request.getStriker() : "Batter";
        String battingTeam = currentInnings.getBattingTeam();
        PlayerStat strikerStat = playerStatRepository.findByInningsIdAndPlayer(currentInnings.getId(), striker)
                .orElseGet(() -> new PlayerStat(matchId, currentInnings.getId(), striker, battingTeam));

        strikerStat.setRuns(strikerStat.getRuns() + runs);
        if (isLegal) {
            strikerStat.setBalls(strikerStat.getBalls() + 1);
        }
        if (runs == 4) {
            strikerStat.setFours(strikerStat.getFours() + 1);
        } else if (runs == 6) {
            strikerStat.setSixes(strikerStat.getSixes() + 1);
        }
        if (isWicket) {
            strikerStat.setOut(true);
        }
        playerStatRepository.save(strikerStat);

        // 3. Update Bowler Statistics
        String bowler = request.getBowler() != null ? request.getBowler() : "Bowler";
        String bowlingTeam = battingTeam != null && battingTeam.equalsIgnoreCase(match.getTeamA())
                ? match.getTeamB() : match.getTeamA();
        PlayerStat bowlerStat = playerStatRepository.findByInningsIdAndPlayer(currentInnings.getId(), bowler)
                .orElseGet(() -> new PlayerStat(matchId, currentInnings.getId(), bowler, bowlingTeam));

        if (isLegal) {
            bowlerStat.setBallsBowled(bowlerStat.getBallsBowled() + 1);
        }
        bowlerStat.setRunsConceded(bowlerStat.getRunsConceded() + totalRuns);
        if (isWicket) {
            bowlerStat.setWickets(bowlerStat.getWickets() + 1);
        }
        playerStatRepository.save(bowlerStat);

        // 4. Save Commentary Event
        String commentaryText = request.getCommentary();
        if (commentaryText == null || commentaryText.isBlank()) {
            if (isWicket) {
                commentaryText = "OUT! Breakthrough for the bowling side as " + striker + " departs!";
            } else if (runs == 6) {
                commentaryText = "SIX! Colossal strike cleared the ropes with effortless timing!";
            } else if (runs == 4) {
                commentaryText = "FOUR! Pierces the gap brilliantly and beats the boundary rider!";
            } else if (runs == 0) {
                commentaryText = "Dot ball. Good tight line and length by " + bowler + ".";
            } else {
                commentaryText = runs + " run" + (runs > 1 ? "s" : "") + " taken. Worked into the outfield.";
            }
        }

        CommentaryEvent event = new CommentaryEvent();
        event.setMatchId(matchId);
        event.setInningsId(currentInnings.getId());
        event.setOverNumber(overNumber);
        event.setBallNumber(ballNumber);
        event.setRunsOffBat(runs);
        event.setExtras(extras);
        event.setWicket(isWicket);
        event.setWicketType(request.getWicketType());
        event.setCommentary(commentaryText);
        event.setStriker(striker);
        event.setNonStriker(request.getNonStriker());
        event.setBowler(bowler);
        event.setLegalDelivery(isLegal);
        event.setTimestamp(Instant.now());
        commentaryEventRepository.save(event);

        // 5. Check match status and innings completion
        checkMatchProgression(match, currentInnings);

        match.setUpdatedAt(Instant.now());
        inningsRepository.save(currentInnings);
        matchRepository.save(match);

        // 6. Broadcast Real-Time SSE Event
        sseService.broadcastScoreUpdate(matchId);

        log.info("Ball recorded for match {}: {} overs, {}/{}", matchId, oversFormatted,
                currentInnings.getRuns(), currentInnings.getWickets());

        return new ScoreEventResponse(
                "SUCCESS",
                oversFormatted,
                currentInnings.getRuns(),
                currentInnings.getWickets(),
                commentaryText
        );
    }

    private void checkMatchProgression(Match match, Innings innings) {
        Integer target = innings.getTarget();

        // 2nd innings chase target check
        if (target != null && target > 0) {
            if (innings.getRuns() >= target) {
                match.setStatus("COMPLETED");
                return;
            }
        }

        // Check if all out or max overs reached in T20 (120 balls = 20 overs)
        boolean isAllOut = innings.getWickets() >= 10;
        boolean maxBallsReached = "T20".equalsIgnoreCase(match.getFormat()) && innings.getLegalBalls() >= 120;

        if (isAllOut || maxBallsReached) {
            if (match.getCurrentInnings() == 1) {
                // End of 1st innings, setup 2nd innings if not already present
                if (match.getInnings().size() < 2) {
                    String nextBattingTeam = innings.getBattingTeam().equalsIgnoreCase(match.getTeamA())
                            ? match.getTeamB() : match.getTeamA();
                    Innings secondInnings = new Innings(2, nextBattingTeam, 0, 0, 0, innings.getRuns() + 1);
                    match.addInnings(secondInnings);
                    inningsRepository.save(secondInnings);
                }
                match.setCurrentInnings(2);
            } else {
                // 2nd innings finished
                match.setStatus("COMPLETED");
            }
        }
    }
}
