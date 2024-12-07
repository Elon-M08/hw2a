# Santorini Game - User Guide

## Introduction
This Santorini game implementation provides a web-based interface and a backend server to simulate the logic of the popular board game *Santorini*. You can select special god powers for each player, place workers, move them, build structures, and attempt to achieve victory conditions defined by your chosen gods.

## Getting Started
**Prerequisites:**
- The backend server should be running on `http://localhost:8080`.
- The frontend React application should be running (commonly on `http://localhost:3000`) after running `npm start` or `yarn start`.

If you have changed any default ports or addresses, ensure `axios.defaults.baseURL` in `Game.js` matches the backend's base URL.

## Selecting Gods and Starting the Game
1. **Open the Web Interface:**  
   Navigate to the frontend in your browser, typically at `http://localhost:3000`.

2. **Choose God Cards:**
   - On page load, you will see a menu where you must select a god power for **Player A** and **Player B**.
   - Use the dropdown menus to select the desired gods.
   - Once both gods are chosen, click the **"Start Game"** button.
   
   Supported gods may include: **Apollo, Artemis, Athena, Atlas, Demeter, Hephaestus, Minotaur, Pan, Prometheus, Hermes.**

3. **View God Powers (Optional):**  
   Once the game starts, you can click **"View God Cards"** to open a reference guide explaining each god's abilities.

## Game Phases and Actions
The game proceeds in distinct phases, which are displayed on the Status Bar at the top:

1. **Placement Phase (`PLACEMENT`):**  
   - Each player alternates placing their two workers on any empty board cells.
   - Click on an empty cell to place your current player's worker.
   - After both players have placed two workers, the game enters the `MOVE` phase.

2. **Move Phase (`MOVE`):**  
   - Click on one of your own workers to select it. Valid moves will highlight on the board.
   - Click on a highlighted cell to move your selected worker there.
   - After moving, you may proceed to a build phase, end your turn, or have special abilities (e.g., Artemis’s extra move), depending on the chosen god.

3. **Build Phase (`BUILD` or `BUILD_AFTER_MOVE`):**  
   - Click on one of your own workers to initiate a build action.
   - Valid build cells will highlight on the board.
   - Click a highlighted cell to build a level of a tower.
   - Certain gods will prompt special choices:
     - **Atlas:** Option to build a dome instead of a block.
     - **Demeter / Hephaestus:** Options to build an additional time under certain conditions.

4. **Special Prompts and Decisions:**
   Depending on your chosen god, you may see prompts after moving or building:
   - **Artemis:** After your first move, a modal asks if you want to move again.
   - **Demeter / Hephaestus:** After building, a modal asks if you want to build again.
   - **Atlas:** A modal asks if you want to build a dome.
   - **Prometheus:** May ask if you want to build before moving.
   - **Hermes:** You may have extended movement options and can choose to end your move at will.

   Respond to these prompts (Yes/No) as desired to continue.

5. **Ending Your Turn:**
   - Once your moves and builds are done (or if you decline extra actions), your turn ends automatically.
   - The game then switches to the other player.

## Victory Condition
The game checks for victory after certain actions:
- Standard victory: reach a tower of height 3.
- God-specific conditions: For example, Pan wins if a worker moves down two or more levels.

When a victory is detected:
- A victory modal appears announcing the winner.
- Click **"Play Again"** to reset and start a new game, possibly with new gods.

## Restarting the Game Mid-Play
If you want to start over before the game ends, click the **"Restart Game"** button at the top.
This resets the game state, allowing you to select new gods and begin again.

## Troubleshooting Common Issues
- **No Valid Moves or Builds:**  
  If you see "No valid moves for this worker" or "Invalid build location," try selecting another worker or cell. Make sure you only move/build on highlighted cells.
  
- **Invalid Move Errors:**  
  If you encounter "Invalid move. Try again.," ensure:
  - You selected your own worker.
  - The selected cell is adjacent and meets the height/build restrictions.
  - It's the correct phase for the action you are attempting.

- **Server/Network Issues:**  
  If actions fail due to network issues, ensure the backend server is running at `http://localhost:8080`. Adjust `axios.defaults.baseURL` in `Game.js` if needed.

## Additional Information
- **God Cards:**  
  Each god modifies the standard rules. Use the "View God Cards" option to understand each one's ability.
  
- **Development and Debugging:**  
  Open the browser's developer console for debugging. Check network requests and backend logs if actions don't behave as expected.

---

**Enjoy playing Santorini with your chosen gods!**
