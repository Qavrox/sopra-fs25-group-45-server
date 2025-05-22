# PokerMaster Arena - Server

## Introduction

PokerMaster Arena is a comprehensive online Texas Hold'em poker platform that combines traditional gameplay with modern AI-powered assistance. This repository contains the backend server implementation that powers the game logic, user management, and integrations with external services.

**Goal**: Create an accessible, feature-rich poker platform that helps players of all skill levels enjoy and improve their game through intelligent assistance and detailed analytics.

**Motivation**: Bridge the gap between casual poker games and professional platforms by providing real-time AI advice, comprehensive statistics tracking, and a smooth multiplayer experience.

## Technologies Used

- **Java 17** - Core programming language
- **Spring Boot 2.7** - Application framework
- **Spring Data JPA** - Database ORM
- **H2 Database** - In-memory database
- **Google Gemini API** - AI poker advisor
- **Maven** - Build management
- **JUnit & Mockito** - Testing framework

## High-level Components

### 1. Game Service ([GameService.java](src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java))
The core game engine that manages:
- Game state transitions (WAITING → READY → PREFLOP → FLOP → TURN → RIVER → SHOWDOWN)
- Player actions processing (fold, check, call, raise, bet)
- Winner determination using poker hand evaluation
- Integration with AI advisor via Gemini API

### 2. User Service ([UserService.java](src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java))
Handles all user-related operations:
- User registration and authentication
- Token-based session management
- Profile management and updates
- Friend system coordination

### 3. Game History Service ([GameHistoryService.java](src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameHistoryService.java))
Tracks and analyzes game performance:
- Records game results for all players
- Calculates user statistics (win rate, total winnings)
- Generates leaderboards (global and friends)
- Provides time-filtered analytics

### 4. REST Controllers
- [GameRoomController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameRoomController.java) - Game creation and management
- [GameActionController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameActionController.java) - In-game actions and AI advice
- [UserController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/UserController.java) - User operations
- [GameHistoryController](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/GameHistoryController.java) - Statistics and leaderboards

### 5. Entity Models
- [Game](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Game.java) - Game state and player management
- [Player](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/Player.java) - In-game player representation
- [User](src/main/java/ch/uzh/ifi/hase/soprafs24/entity/User.java) - User account information

## Launch & Deployment

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Google Cloud SDK (for deployment)
- Gemini API key (set as environment variable)

### Local Development

1. Clone the repository:
```bash
git clone https://github.com/sopra-fs25-group-45/sopra-fs25-group-45-server.git
cd sopra-fs25-group-45-server
```

2. Set environment variables:
```bash
export GEMINI_API_KEY=your_gemini_api_key_her
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```
The server will start on http://localhost:8080

### Running Tests
Execute all tests with coverage report:
```bash
mvn test
```

### Deployment to Google App Engine
Configure app.yaml (already included)

Deploy to GAE::
```bash
gcloud app deploy
```

## API Documentation
### Key Endpoints
```http
POST /auth/register         - User registration
POST /auth/login            - User login
POST /games                 - Create new game
GET  /games                 - List public games
POST /games/{id}/join       - Join game
POST /games/{id}/start-betting - Start game
POST /games/{id}/action     - Perform game action
GET  /games/{id}/advice     - Get AI poker advice
GET  /users/{id}/statistics - Get user statistics
GET  /leaderboard/winnings  - Get leaderboard
```

## Roadmap
### Tournament Mode
Implement multi-table tournament support with:

- Bracket system
- Blind level progression
- Prize pool distribution
- Tournament statistics

### Advanced AI Features
Enhance the AI advisor with:
- Opponent modeling based on play history
- Bluff detection algorithms
- Personalized learning recommendations

## Authors & Acknowledgments
- Yunyi (Aaron) Zhang - @TauSigma
- Lydia Gattiker - @lydia-milena
- Guanqiao Li - @unscttp
- Maorong Lin  - @Qavrox
- Yutian Lei - @IsSaudade

Special thanks to:
- SoPra teaching team for guidance
- Google Gemini team for AI API access

## License
This project is licensed under the MIT License - see below:

MIT License

Copyright (c) 2025 SoPra Group 45

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
