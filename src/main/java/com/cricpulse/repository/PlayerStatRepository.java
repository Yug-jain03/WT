package com.cricpulse.repository;

import com.cricpulse.model.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {
    List<PlayerStat> findByInningsId(Long inningsId);
    Optional<PlayerStat> findByInningsIdAndPlayer(Long inningsId, String player);
    List<PlayerStat> findByMatchId(Long matchId);
}
