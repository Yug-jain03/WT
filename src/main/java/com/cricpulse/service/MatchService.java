package com.cricpulse.service;

import com.cricpulse.model.CommentaryEvent;
import com.cricpulse.model.Match;
import com.cricpulse.model.PlayerStat;
import com.cricpulse.repository.CommentaryEventRepository;
import com.cricpulse.repository.MatchRepository;
import com.cricpulse.repository.PlayerStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final CommentaryEventRepository commentaryEventRepository;
    private final PlayerStatRepository playerStatRepository;

    public MatchService(MatchRepository matchRepository,
                        CommentaryEventRepository commentaryEventRepository,
                        PlayerStatRepository playerStatRepository) {
        this.matchRepository = matchRepository;
        this.commentaryEventRepository = commentaryEventRepository;
        this.playerStatRepository = playerStatRepository;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    public List<CommentaryEvent> getCommentaryForMatch(Long matchId) {
        return commentaryEventRepository.findByMatchIdOrderByIdAsc(matchId);
    }

    public List<PlayerStat> getStatsForInnings(Long inningsId) {
        return playerStatRepository.findByInningsId(inningsId);
    }
}
