use markdown code: # Santorini Game - README

## Table of Contents

- [Introduction](#introduction)
- [How to Start](#how-to-start)
- [Game Rules](#game-rules)
- [How to Play](#how-to-play)
  - [Placement Phase](#placement-phase)
  - [Move Phase](#move-phase)
  - [Build Phase](#build-phase)
- [Restarting the Game](#restarting-the-game)
- [Tips for Playing](#tips-for-playing)
- [Game Architecture](#game-architecture)

---

## Introduction

Welcome to the **Santorini Game**, a strategic board game where players build towers on a 5x5 grid while trying to outmaneuver their opponent. The first player to move their worker to the top of a level-3 tower wins!

This README will guide you through starting the game, understanding the rules, and enjoying this exciting competition.

## How to Start

1. Clone or download the repository containing the Santorini game code.
2. Ensure you have Java installed to run the backend logic.
3. Start the backend server using the provided Java code:
   ```
   Navigate to the `backend` directory.
   mvn clean install
   mvn exec:java
   ```
4. Launch the frontend application to interact with the game via the user interface:
   - Navigate to the `front` directory.
   - Start the React application by running:
     ```
     npm install
     npm start
     ```
5. Open your browser and navigate to `http://localhost:3000` to play the game.

## Game Rules

1. **Players:** The game has two players: Player A and Player B.
2. **Objective:** Move one of your workers to the top of a level-3 tower to win.
3. **Phases:** Each turn has three phases:
   - Placement
   - Movement
   - Building
4. **Board:** The board is a 5x5 grid. Players can build towers up to level 4.
5. **Workers:** Each player has two workers to control and move.

## How to Play

### Placement Phase

- Each player places two workers on the board at unoccupied locations.
- Workers must be placed one at a time, alternating between players.

#### Steps:

1. Click on an empty cell to place your worker.
2. Wait for your opponent to place their worker.
3. Continue until all four workers are placed.

### Move Phase

- Select one of your workers to move to an adjacent cell.
- Workers can move up one level, down any number of levels, or on the same level.
- Workers cannot move to cells with domes or cells occupied by another worker.

#### Steps:

1. Click on your worker.
2. Select a highlighted cell to move.
3. If no valid moves are available, select another worker.

### Build Phase

- After moving, you must build a level on an adjacent cell.
- Buildings can be built up to level 4. The fourth level is capped with a dome.

#### Steps:

1. Click on your worker.
2. Select a highlighted cell to build.

### Victory Condition

- If your worker moves to a level-3 tower, you win!

## Restarting the Game

- To restart the game, click the **Restart Game** button in the UI.
- Alternatively, restart the backend server for a fresh start.

## Tips for Playing

- Plan your moves and builds strategically to block your opponent.
- Use the height advantage to corner your opponent’s workers.
- Build domes to prevent level-3 access for your opponent.

## Game Architecture

1. **Backend:**

   - Core game logic is implemented in Java.
   - Manages the board state, player turns, and game phases.
   - Exposes endpoints for the frontend to interact with.

2. **Frontend:**

   - Built using React for an interactive UI.
   - Displays the board and provides visual cues for valid actions.
   - Communicates with the backend via API calls.

3. **Data Flow:**

   - Player actions (e.g., move, build) are sent to the backend.
   - The backend validates the actions, updates the game state, and returns responses.
   - The frontend updates the display based on the backend responses.

---

