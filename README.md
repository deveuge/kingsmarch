# Kingsmarch
![Kingsmarch Banner](preview.png)
_Spring Boot App  - Single and multiplayer chess web game_

[![View live project](https://img.shields.io/badge/View%20live%20project-430098?style=for-the-badge&logo=render&logoColor=white)](https://kingsmarch.onrender.com/)

[![View Full Documentation](https://img.shields.io/badge/View%20Full%20Documentation-181717?style=for-the-badge&logo=gitbook&logoColor=white)](https://deveuge.gitbook.io/projects/kingsmarch/project-overview)

<sup>Please note that because Render is a free Cloud Application Platform, load times can be somewhat high and have nothing to do with the application itself.</sup>

---

## 🧩 What It Does

- Supports **singleplayer mode** (vs AI) and **multiplayer mode** (vs human).
- Implements all standard chess rules: legal move validation, castling, en passant, pawn promotion, and check/checkmate detection.
- Offers a clean, server-rendered UI with interactive board and session management.
- AI uses Minimax with Alpha-Beta pruning and an Opening Book.
- Maintains game state per session for single-player mode.

## 🧠 Tech Stack

- **Java 17** + **Spring Boot 3**
- **Maven** with modular structure: `domain`, `app`, `infra`, `api`
- **Thymeleaf** server-side templates (HTML + JavaScript)
- **WebSocket / STOMP** support for multiplayer
- **Custom chess engine** with minimax-based AI and opening-book support
- **Session-based in-memory** game state via Spring `HttpSession`
- **Docker support** with a multi-stage build

## ⚙️ Technical Aspects

This project is primarily designed to showcase object-oriented programming (OOP) principles applied to a real-world domain — chess.

As a result, performance was not the main priority in the design of the single-player AI. The response time for move generation in single-player mode may be slower, partly due to the limitations of the Render.com server, and partly because the chess engine favors readability and structure over raw efficiency.

It is acknowledged that more performant approaches exist — for example, representing moves as integers and using bitwise operations for encoding move-from, move-to, and move-type data. In Java, object creation comes with overhead, but was chosen here for its alignment with clean OOP design. 
This was a deliberate design tradeoff to highlight software engineering principles over low-level optimizations.


## 📦 Modules Breakdown

The project is organized into Maven submodules according to a hexagonal layering pattern:

### `domain`
Pure domain logic with no framework dependencies.
- Chess board, pieces, moves, and game state.
- Legal move checking, game status (checkmate, stalemate, etc.)

### `app`
Application layer (use cases) orchestrating domain and session management.
- Services like: `MakePlayerMoveUseCase`, `PromotePawnUseCase`, `MakeAIMoveUseCase`, `StartSingleplayerGameUseCase`

### `infra`
Infrastructure adapters and Spring wiring.
- WebSocket setup, session game repository
- Opening-book loader (`FileBasedOpeningBookFactory`)
- `HttpSessionGameAdapter` for session-scoped Game handling

### `api`
Spring MVC + Thymeleaf layer.
- HTTP and WebSocket controllers for game routes (e.g. `/sp`, `/mp`)
- View templates under `src/main/resources/templates`
- Endpoints for moves, promotion, auto-move, and chat

---

## 🚀 How to Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/deveuge/kingsmarch.git
cd kingsmarch
```
### 2. Build the project
```bash
cd api
mvn spring-boot:run
```
### 3. Run the application
```bash
cd kingsmarch-api
mvn spring-boot:run
```
Then open: [http://localhost:8080](http://localhost:8080)

## 🐳 Docker Support
Build and run using Docker:

```bash
docker build -t kingsmarch .
docker run -p 8080:8080 kingsmarch
```
---

## 🔭 Future Improvements

While Kingsmarch is functionally complete, there are several areas identified for potential enhancement:

- **End-of-game rule support**:
  - Implement threefold repetition detection.
  - Enforce the fifty-move rule.

- **Full FEN notation support**:
  - Currently, both singleplayer and multiplayer games allow starting from a partial FEN by appending `?fen={FEN_NOTATION}` to the URL.
  - However, only the piece placement field is interpreted. Additional FEN data such as active color, castling rights, en passant targets, and halfmove/fullmove counters are currently ignored.

- **Persistent storage**:
  - Once full FEN support is available, games could be saved and retrieved using a NoSQL database instead of relying solely on in-memory or session-based storage.

- **External API exposure**:
  - Provide an API endpoint to retrieve all legal moves given a FEN. This would allow external applications to leverage Kingsmarch's rules engine.

- **AI enhancements**:
  - Enhance the move evaluation and decision tree with advanced search optimizations.
  - Expand the opening book, which currently contains 77 predefined lines.
  - Make AI pawn promotion context-aware (currently always promotes to queen).
