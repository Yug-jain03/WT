package com.cricpulse.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllMatches() throws Exception {
        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].teamA", notNullValue()))
                .andExpect(jsonPath("$[0].teamB", notNullValue()))
                .andExpect(jsonPath("$[0].status", notNullValue()));
    }

    @Test
    void testGetMatchDetails() throws Exception {
        mockMvc.perform(get("/api/matches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.teamA", is("India")))
                .andExpect(jsonPath("$.teamB", is("Australia")))
                .andExpect(jsonPath("$.innings", hasSize(2)));
    }

    @Test
    void testGetCommentary() throws Exception {
        mockMvc.perform(get("/api/matches/1/commentary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))));
    }

    @Test
    void testGetStatsForInnings() throws Exception {
        // Innings 2 is India's batting innings in seed data
        mockMvc.perform(get("/api/matches/1/innings/2/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].player", notNullValue()));
    }

    @Test
    void testRecordEvent() throws Exception {
        String payload = """
            {
                "runs": 6,
                "wicket": false,
                "commentary": "SIX! Launched into the second tier!",
                "striker": "Virat Kohli",
                "bowler": "Mitchell Starc",
                "legalDelivery": true,
                "extras": 0
            }
        """;

        mockMvc.perform(post("/api/matches/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.overs", notNullValue()))
                .andExpect(jsonPath("$.runs", greaterThanOrEqualTo(148)));
    }
}
