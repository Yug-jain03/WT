# WTASS8 — Real-Time Cricket Score Management System

**Web Technology Assignment 8**<br>
**Student Name:** Yug Jain<br>
**Class / Division:** CS-L<br>
**Roll No:** 70<br>
**GitHub Repository:** [https://github.com/Yug-jain03/WT](https://github.com/Yug-jain03/WT)

---

## Project Overview

**CricPulse (WTASS8)** is a scalable, real-time cricket score management system and interactive operations dashboard built using **Spring Boot 3**, **Spring Data JPA**, **H2 Database**, and **Server-Sent Events (SSE)**.

The frontend is crafted with a high-end, modern whitish design inspired by **gr-connect.org**, featuring clean typography, crisp frosted cards, real-time delivery timelines, player statistics, and quick scoring entry controls.

---

## Key Features

1. **Live Match Operations Centre**:
   - Multi-match fixtures browser with search and live status badges.
   - Dynamic match switching (T20, ODI, Test formats).
2. **Real-Time Score Hero**:
   - Live runs, wickets, overs, current run rate (CRR), target, and chase status calculations.
   - Toss winner, balls bowled, and milestone indicators.
3. **Batting Card & Bowling Statistics**:
   - Live batting card with runs, balls, 4s, 6s, strike rate, and dismissal status.
   - Leading bowler analysis with wickets and economy tracking.
4. **Delivery Timeline & Momentum Grid**:
   - Ball-by-ball momentum grid showing the last 12 deliveries with distinct run and wicket styles.
   - Detailed live commentary timeline with delivery context (bowler, striker, runs, extras, wickets).
5. **Quick Score Entry & Simulation**:
   - Interactive operations buttons (`DOT`, `1`, `2`, `3`, `4`, `6`, and `Record Wicket`).
   - One-click `Simulate Ball` button for automated delivery simulation.
6. **Real-Time Reactivity via Server-Sent Events (SSE)**:
   - Zero-latency broadcast updates over `GET /api/live`.
   - Automatic dashboard refresh without full page reloads.
7. **Whitish Theme Inspired by gr-connect.org**:
   - Off-white canvas (`#fafafa`), deep navy (`#243a5e`) accents, Newsreader & Hanken Grotesk typography.

---

## Technology Stack

- **Backend Framework**: Spring Boot 3.3.4 (Java 21 / 25)
- **Data & Persistence**: Spring Data JPA, Hibernate, In-Memory H2 Database (`jdbc:h2:mem:cricpulsedb`)
- **Real-Time Protocol**: Server-Sent Events (`SseEmitter` streaming over `/api/live`)
- **Frontend Architecture**: Vanilla HTML5, Modern CSS (Glassmorphism & Whitish Theme), Native ES6 JavaScript
- **Build & Dependency Management**: Apache Maven 3.9+

---

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/matches` | Retrieve all fixtures (active and past matches) |
| `GET` | `/api/matches/{id}` | Detailed match record including innings scores |
| `GET` | `/api/matches/{id}/commentary` | Chronological ball-by-ball commentary feed |
| `GET` | `/api/matches/{id}/innings/{inningsId}/stats` | Batting scorecard and bowling figures |
| `POST` | `/api/matches/{id}/events` | Ingest a new ball delivery, boundary, or wicket |
| `GET` | `/api/live` | Server-Sent Events (SSE) stream for live updates |
| `GET` | `/h2-console` | In-memory database web console |

---

## How to Run Locally

```bash
# Clone the repository
git clone https://github.com/Yug-jain03/WT.git
cd WT

# Build and run with Maven
mvn clean test
mvn spring-boot:run
```

Once started, open your browser and navigate to:
```
http://localhost:8080/
```
