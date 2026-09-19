package com.cricpulse.repository;

import com.cricpulse.model.CommentaryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaryEventRepository extends JpaRepository<CommentaryEvent, Long> {
    List<CommentaryEvent> findByMatchIdOrderByIdAsc(Long matchId);
}
