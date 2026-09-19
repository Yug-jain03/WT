package com.cricpulse.config;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final PlayerStatRepository playerStatRepository;
    private final CommentaryEventRepository commentaryEventRepository;

    public DataInitializer(MatchRepository matchRepository,
                           InningsRepository inningsRepository,
                           PlayerStatRepository playerStatRepository,
                           CommentaryEventRepository commentaryEventRepository) {
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.playerStatRepository = playerStatRepository;
        this.commentaryEventRepository = commentaryEventRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (matchRepository.count() > 0) {
            return;
        }

        log.info("Initializing CricPulse cricket match database with seed fixtures...");

        // ==========================================
        // MATCH 1: India vs Australia (T20 Thriller)
        // ==========================================
        Match indVsAus = new Match(
                "India",
                "Australia",
                "Melbourne Cricket Ground",
                "T20",
                "LIVE",
                Instant.now().minus(Duration.ofMinutes(110))
        );
        indVsAus.setTossWinner("Australia");
        indVsAus.setTossDecision("chose to bat");
        indVsAus.setCurrentInnings(2);

        // 1st Innings: Australia 186/6 (20 overs = 120 balls)
        Innings ausInnings = new Innings(1, "Australia", 186, 6, 120, null);
        indVsAus.addInnings(ausInnings);

        // 2nd Innings: India 142/3 in 15.4 overs (94 balls) chasing 187
        Innings indInnings = new Innings(2, "India", 142, 3, 94, 187);
        indVsAus.addInnings(indInnings);

        matchRepository.save(indVsAus);

        Long match1Id = indVsAus.getId();
        Long indInningsId = indInnings.getId();
        Long ausInningsId = ausInnings.getId();

        // 2nd Innings India Batters
        playerStatRepository.save(createBatter(match1Id, indInningsId, "Virat Kohli", "India", 58, 38, 5, 2, false));
        playerStatRepository.save(createBatter(match1Id, indInningsId, "Suryakumar Yadav", "India", 34, 18, 3, 2, false));
        playerStatRepository.save(createBatter(match1Id, indInningsId, "Rohit Sharma", "India", 27, 19, 3, 1, true));
        playerStatRepository.save(createBatter(match1Id, indInningsId, "Shubman Gill", "India", 14, 11, 2, 0, true));
        playerStatRepository.save(createBatter(match1Id, indInningsId, "Rishabh Pant", "India", 6, 8, 0, 0, true));

        // 2nd Innings Australia Bowlers
        playerStatRepository.save(createBowler(match1Id, indInningsId, "Mitchell Starc", "Australia", 22, 32, 2));
        playerStatRepository.save(createBowler(match1Id, indInningsId, "Pat Cummins", "Australia", 24, 28, 1));
        playerStatRepository.save(createBowler(match1Id, indInningsId, "Adam Zampa", "Australia", 24, 36, 0));
        playerStatRepository.save(createBowler(match1Id, indInningsId, "Josh Hazlewood", "Australia", 24, 44, 0));

        // 1st Innings stats for Australia
        playerStatRepository.save(createBatter(match1Id, ausInningsId, "Travis Head", "Australia", 64, 35, 7, 3, true));
        playerStatRepository.save(createBatter(match1Id, ausInningsId, "Glenn Maxwell", "Australia", 48, 26, 4, 3, true));
        playerStatRepository.save(createBowler(match1Id, ausInningsId, "Jasprit Bumrah", "India", 24, 24, 3));
        playerStatRepository.save(createBowler(match1Id, ausInningsId, "Arshdeep Singh", "India", 24, 38, 2));

        // Seed Commentary for last 14 deliveries
        seedCommentary(match1Id, indInningsId);

        // ==========================================
        // MATCH 2: England vs South Africa (ODI)
        // ==========================================
        Match engVsSa = new Match(
                "England",
                "South Africa",
                "Lord's Cricket Ground, London",
                "ODI",
                "LIVE",
                Instant.now().minus(Duration.ofHours(3))
        );
        engVsSa.setTossWinner("England");
        engVsSa.setTossDecision("chose to bat");
        engVsSa.setCurrentInnings(1);

        Innings engInnings = new Innings(1, "England", 214, 4, 230, null);
        engVsSa.addInnings(engInnings);
        matchRepository.save(engVsSa);

        playerStatRepository.save(createBatter(engVsSa.getId(), engInnings.getId(), "Joe Root", "England", 82, 94, 6, 0, false));
        playerStatRepository.save(createBatter(engVsSa.getId(), engInnings.getId(), "Jos Buttler", "England", 45, 38, 4, 1, false));
        playerStatRepository.save(createBatter(engVsSa.getId(), engInnings.getId(), "Ben Stokes", "England", 33, 40, 3, 0, true));
        playerStatRepository.save(createBowler(engVsSa.getId(), engInnings.getId(), "Kagiso Rabada", "South Africa", 48, 42, 2));
        playerStatRepository.save(createBowler(engVsSa.getId(), engInnings.getId(), "Marco Jansen", "South Africa", 42, 38, 2));

        // ==========================================
        // MATCH 3: Pakistan vs New Zealand (T20 - COMPLETED)
        // ==========================================
        Match pakVsNz = new Match(
                "Pakistan",
                "New Zealand",
                "Gaddafi Stadium, Lahore",
                "T20",
                "COMPLETED",
                Instant.now().minus(Duration.ofHours(6))
        );
        pakVsNz.setTossWinner("Pakistan");
        pakVsNz.setTossDecision("chose to bat");
        pakVsNz.setCurrentInnings(2);

        Innings pakInnings = new Innings(1, "Pakistan", 175, 7, 120, null);
        Innings nzInnings = new Innings(2, "New Zealand", 178, 4, 113, 176);
        pakVsNz.addInnings(pakInnings);
        pakVsNz.addInnings(nzInnings);
        matchRepository.save(pakVsNz);

        log.info("Initialized {} matches with real-time scoreboards.", matchRepository.count());
    }

    private PlayerStat createBatter(Long matchId, Long inningsId, String name, String team,
                                     int runs, int balls, int fours, int sixes, boolean out) {
        PlayerStat stat = new PlayerStat(matchId, inningsId, name, team);
        stat.setRuns(runs);
        stat.setBalls(balls);
        stat.setFours(fours);
        stat.setSixes(sixes);
        stat.setOut(out);
        return stat;
    }

    private PlayerStat createBowler(Long matchId, Long inningsId, String name, String team,
                                     int ballsBowled, int runsConceded, int wickets) {
        PlayerStat stat = new PlayerStat(matchId, inningsId, name, team);
        stat.setBallsBowled(ballsBowled);
        stat.setRunsConceded(runsConceded);
        stat.setWickets(wickets);
        return stat;
    }

    private void seedCommentary(Long matchId, Long inningsId) {
        // Feed recent deliveries (Overs 13 to 15.4)
        Object[][] events = new Object[][] {
                {13, 5, 1, 0, false, "Pat Cummins bowls on off stump, tapped toward cover for a single.", "Pat Cummins", "Virat Kohli"},
                {13, 6, 4, 0, false, "FOUR! Suryakumar Yadav opens the blade and guides it past backward point!", "Pat Cummins", "Suryakumar Yadav"},
                {14, 1, 1, 0, false, "Adam Zampa tosses it up on middle, worked gently down to long-on.", "Adam Zampa", "Suryakumar Yadav"},
                {14, 2, 2, 0, false, "Driven firmly into the deep extra cover pocket, good running between the wickets.", "Adam Zampa", "Virat Kohli"},
                {14, 3, 0, 0, false, "Beaten! Slight turn outside off, Kohli plays and misses.", "Adam Zampa", "Virat Kohli"},
                {14, 4, 6, 0, false, "SIX! Stupendous strike! Dances down the pitch and launches it 88m over long-off!", "Adam Zampa", "Virat Kohli"},
                {14, 5, 1, 0, false, "Flatter trajectory on the pads, clipped off the hips toward deep square leg.", "Adam Zampa", "Virat Kohli"},
                {14, 6, 1, 0, false, "Full delivery on leg stump, punched down to long-on to retain strike.", "Adam Zampa", "Suryakumar Yadav"},
                {15, 1, 0, 0, false, "Mitchell Starc returns to the attack. Steaming yorker dug out into the crease.", "Mitchell Starc", "Suryakumar Yadav"},
                {15, 2, 4, 0, false, "FOUR! Trademark SKY! Bending on one knee and sweeping over short fine leg!", "Mitchell Starc", "Suryakumar Yadav"},
                {15, 3, 1, 0, false, "Short of a length delivery, guided down to third man for an easy single.", "Mitchell Starc", "Suryakumar Yadav"},
                {15, 4, 0, 0, false, "Good length angled across off, Kohli leaves it alone to the keeper.", "Mitchell Starc", "Virat Kohli"}
        };

        for (Object[] d : events) {
            CommentaryEvent c = new CommentaryEvent();
            c.setMatchId(matchId);
            c.setInningsId(inningsId);
            c.setOverNumber((Integer) d[0]);
            c.setBallNumber((Integer) d[1]);
            c.setRunsOffBat((Integer) d[2]);
            c.setExtras((Integer) d[3]);
            c.setWicket((Boolean) d[4]);
            c.setCommentary((String) d[5]);
            c.setBowler((String) d[6]);
            c.setStriker((String) d[7]);
            c.setLegalDelivery(true);
            c.setTimestamp(Instant.now().minus(Duration.ofSeconds(120 - ((Integer) d[0] * 6 + (Integer) d[1]))));
            commentaryEventRepository.save(c);
        }
    }
}
