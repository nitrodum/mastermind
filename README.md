# Mastermind (Spring Boot)

A full-stack implementation of the classic Mastermind code-breaking game using Spring Boot, Thymeleaf, and JPA. It supports two modes:
- Daily mode: a single shared puzzle per UTC day with submissions, percentile stats, and streaks (requires login).
- Infinite mode: endless single-player games with local stats.

The app uses Random.org (with retry and local fallback) to generate daily and infinite codes, and normalizes the daily game date to midnight UTC. A scheduled task creates the daily game at 00:00 UTC.


## Table of contents
- Features
- Tech stack
- Prerequisites
- Quickstart (no DB install: H2 in-memory)
- MySQL setup (production-like)
- Build and run
- Run tests
- How to play
- API reference
- Architecture and code structure
- Design notes and creative extensions
- Troubleshooting


## Features
- Web UI with Thymeleaf for making guesses and viewing feedback.
- Authentication (register/login) to enable daily mode submissions and stats.
- Daily Game created at 00:00 UTC; prevents duplicate submissions per user per day.
- Percentile stats comparing your attempts to other players for that day.
- Infinite mode for quick play without login.
- Unit tests for services and core guess-checking utility.


## Tech stack
- Java 17
- Spring Boot 3.5.x (Web, Thymeleaf, Data JPA, Security)
- JPA with MySQL (default) or H2 (in-memory) at runtime
- Maven (wrapper included)


## Prerequisites
- Java 17 (JDK). Verify with:
  - Windows (PowerShell): `java -version`
  - macOS/Linux: `java -version`
- One of:
  - Option A (recommended for interview): No DB install required. Use H2 in-memory via JVM property overrides.
  - Option B: MySQL 8.x installed locally and accessible.
- You may use the Maven Wrapper included in the repo (no Maven install needed).


## Quickstart (H2 in-memory; no DB install)
This project is configured for MySQL by default, but you can override Spring properties at runtime to use H2 in-memory. These commands start the app with H2 and auto-create tables.

- Windows (PowerShell):
```powershell
# From the project root
.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:h2:mem:mastermind -Dspring.datasource.driver-class-name=org.h2.Driver -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect -Dspring.jpa.hibernate.ddl-auto=update"
```

- macOS/Linux:
```bash
# From the project root
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=jdbc:h2:mem:mastermind -Dspring.datasource.driver-class-name=org.h2.Driver -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect -Dspring.jpa.hibernate.ddl-auto=update"
```

Then open http://localhost:8080 in your browser.

Optional: H2 Console is not enabled by default; if you need it, you can temporarily add `spring.h2.console.enabled=true` and `spring.h2.console.path=/h2` to `application.properties`, restart, and visit `/h2`.


## MySQL setup (production-like)
By default, `src/main/resources/application.properties` points to a local MySQL database:
```
spring.datasource.url=jdbc:mysql://localhost:3306/mastermind_db
spring.datasource.username=root
spring.datasource.password=yearup
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```
You can either:
- Change these credentials to match your environment, or
- Create a matching database/user:

1) Start MySQL, then run:
```sql
CREATE DATABASE IF NOT EXISTS mastermind_db;
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'yearup';
GRANT ALL PRIVILEGES ON mastermind_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

2) Build and run (see next section). Spring will auto-create tables with `ddl-auto=update`.


## Build and run
- Windows (PowerShell):
```powershell
# Build (runs tests)
.\mvnw.cmd clean package

# Run
.\mvnw.cmd spring-boot:run
```

- macOS/Linux:
```bash
# Build (runs tests)
./mvnw clean package

# Run
./mvnw spring-boot:run
```

Once running, visit http://localhost:8080.


## Run tests
- Windows:
```powershell
.\mvnw.cmd -DskipTests=false test
```
- macOS/Linux:
```bash
./mvnw -DskipTests=false test
```

Tests cover service logic (daily game lifecycle, submissions, percentile stats) and the guess-checking utility.


## How to play
- Launch the app and visit http://localhost:8080
- You’ll see the main game screen with four inputs for digits (0–7) and a submit button.
- Each guess returns feedback: total correct numbers and how many are in the correct positions.
- You have 10 attempts per game.

Game modes:
- Daily mode (requires login):
  - Go to http://localhost:8080/auth to register or log in.
  - After login, the app sets game mode to daily automatically.
  - Only one submission per user per UTC day is allowed. If you’ve already submitted, you’ll see a completion message.
  - On win/loss, the app stores your submission and shows percentile stats vs. other players for that day.
- Infinite mode:
  - Switch with http://localhost:8080/mode/infinite or the UI if provided.
  - No login required; play endlessly with local stats.


## API reference (selected)
- GET `/api/daily-game`
  - Returns the daily game for today (creates it on first access).
- GET `/api/daily-submission/today`
  - Returns list of today’s daily submissions (admin/debug use).
- POST `/api/daily-submission`
  - Body: `{ "attempts": <int>, "gameDate": "<ISO date or epoch millis>" }`
  - Requires session with logged-in user (created by the web login flow).

Note: Most gameplay happens through the web UI and session-managed endpoints. Programmatic play is not a primary target for this project.


## Architecture and code structure
High-level layering:
- Controller layer (`controller/*`)
  - GameController: routes for main gameplay, mode switching, handling guesses, and session orchestration.
  - DailyGameController: REST endpoint to fetch the daily game.
  - DailySubmissionController: REST endpoint to create a daily submission and get today’s submissions.
  - StatsController, UserController: stats view and auth flows.
- Service layer (`service/*`)
  - DailyGameService: creates/fetches daily games; scheduled job at 00:00 UTC; UTC date normalization.
  - DailySubmissionService: persistence and percentile calculations.
  - DailyStatsService, UserStatsService: persistence of stats; helper methods to mutate stats.
  - GameService: creates a Game with a freshly generated code.
  - RandomOrgService: fetches 4 digits (0–7) from Random.org with retry; falls back to local random.
- Repository layer (`repository/*`): Spring Data JPA repos for entities.
- Model (`model/*`): Entities like User, DailyGame, Game, Guess, Feedback, DailySubmission, UserStats, DailyStats.
- Utility (`util/*`)
  - GuessChecker: core two-pass algorithm to compute feedback (correct numbers vs. correct positions) with duplicates handled by consumption.

Game logic overview:
- GuessChecker runs in two passes: first counts exact matches and consumes them; second counts present-but-wrong-position values, consuming one occurrence per match to avoid overcounting duplicates.
- Daily mode is UTC-based. A scheduled method `@Scheduled(cron = "0 0 0 * * *", zone = "UTC")` creates the daily game if missing.


## Design notes and creative extensions
- Daily percentile stats: `DailySubmissionService.calculatePercentileStats(date, attempts)` returns a map with `totalPlayers`, `betterThan`, `sameScore`, and `percentile` (lower is better). Edge cases handled: no submissions, all worse, all same-score.
- UTC normalization: `DailyGameService.getTodayDate()` returns midnight UTC to ensure the same daily game globally.
- Random.org resilience: 3 retries with exponential backoff; logs; then a robust local fallback.
- Session-driven UX: The app stores the `Game` in the session; on each guess, it appends a `Guess` with `Feedback`. Daily win/loss persists stats and shows a modal with percentile.

Potential next steps:
- Move game-completion logic (win/loss, stats persistence) from GameController into a small utility/service for easier testing and reuse.
- Add controller-layer tests with WebMvcTest.
- Add profile-specific properties (e.g., `application-h2.properties`) for easier DB switching without command-line overrides.


## Troubleshooting
- Cannot connect to MySQL / Authentication issue
  - Ensure MySQL is running and credentials in `application.properties` are correct.
  - Or use the H2 quickstart command to avoid needing MySQL.
- Port 8080 already in use
  - Run with `-Dserver.port=8081` (e.g., `spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081 ..."`).
- Tests fail intermittently due to time/date
  - Daily features use UTC; if you rely on system time, ensure the expected date is UTC midnight.
- Random.org network issues
  - Logs will show retries; local fallback kicks in automatically.


## License
For interview use; no specific license declared.

