package com.cricpulse.repository;

import com.cricpulse.model.Innings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InningsRepository extends JpaRepository<Innings, Long> {
    List<Innings> findByMatchId(Long matchId);
    Optional<Innings> findByMatchIdAndNumberInMatch(Long matchId, Integer numberInMatch);
}
