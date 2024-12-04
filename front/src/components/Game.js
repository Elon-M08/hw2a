
// src/components/Game.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Board from './Board';
import StatusBar from './StatusBar';
import '../assets/styles/Game.css';

axios.defaults.baseURL = 'http://localhost:8080';

function Game() {
  const [gameState, setGameState] = useState(null);
  const [selectableCells, setSelectableCells] = useState([]);
  const [selectedWorker, setSelectedWorker] = useState(null);
  const [currentPlayerWorkers, setCurrentPlayerWorkers] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');
  const [gameEnded, setGameEnded] = useState(false);
  const [winner, setWinner] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    startNewGame();
  }, []);

  useEffect(() => {
    if (gameState) {
      const { currentPlayer, workers, gameEnded } = gameState;
      const playerWorkers = workers.filter((w) => w.player === currentPlayer);
      setCurrentPlayerWorkers(playerWorkers);

      if (gameEnded) {
        setGameEnded(true);
        setWinner(currentPlayer);
      } else {
        setGameEnded(false);
        setWinner(null);
      }

      // Reset selections when the game state updates
      setSelectableCells([]);
      setSelectedWorker(null);
    }
  }, [gameState]);

  const startNewGame = async () => {
    setLoading(true);
    try {
      const response = await axios.post('/start-game');
      console.log('Game state after restart:', response.data);
      setGameState(response.data);
      setSelectableCells([]);
      setSelectedWorker(null);
      setErrorMessage('');
      setGameEnded(false);
      setWinner(null);
    } catch (error) {
      setErrorMessage('Failed to restart the game.');
    } finally {
      setLoading(false);
    }
  };

  const handleCellClick = async (x, y) => {
    console.log(`Cell clicked: x=${x}, y=${y}`);
    if (!gameState || gameEnded) return;
    setErrorMessage('');
    const { gamePhase, currentPlayer } = gameState;

    try {
      if (gamePhase === 'PLACEMENT') {
        const response = await axios.post('/action', {
          actionType: 'placeWorker',
          x,
          y,
        });
        setGameState(response.data);
        setSelectableCells([]);
        setSelectedWorker(null);
      } else if (gamePhase === 'MOVE') {
        if (selectedWorker) {
          // Check if the clicked cell is a valid move
          if (selectableCells.some((cell) => cell.x === x && cell.y === y)) {
            const response = await axios.post('/action', {
              actionType: 'move',
              workerIndex: selectedWorker.index,
              x,
              y,
            });
            setGameState(response.data);
            setSelectableCells([]);
            setSelectedWorker(null);
          } else {
            setErrorMessage('Invalid move. Please select a highlighted cell.');
          }
        } else {
          // Select a worker to move
          const worker = gameState.workers.find(
            (w) =>
              w.position.x === x &&
              w.position.y === y &&
              w.player === currentPlayer
          );
          if (worker) {
            const workerIndex = currentPlayerWorkers.findIndex(
              (w) => w.position.x === x && w.position.y === y
            );
            if (workerIndex !== -1) {
              setSelectedWorker({ ...worker, index: workerIndex });
              const cells = getSelectableCellsForMove(worker);
              if (cells.length === 0) {
                setErrorMessage('No valid moves for this worker.');
                setSelectedWorker(null);
              } else {
                setSelectableCells(cells);
              }
            } else {
              setErrorMessage('Worker index not found.');
            }
          } else {
            setErrorMessage('Please select one of your own workers.');
          }
        }
      } else if (gamePhase === 'BUILD') {
        if (selectableCells.length === 0) {
          // Determine where the worker can build
          const worker = selectedWorker || currentPlayerWorkers[0];
          const cells = getSelectableCellsForBuild(worker);
          if (cells.length === 0) {
            setErrorMessage('No valid build locations.');
            setSelectableCells([]);
            setSelectedWorker(null);
          } else {
            setSelectedWorker(worker);
            setSelectableCells(cells);
          }
        } else {
          // Attempt to build at the selected cell
          if (selectableCells.some((cell) => cell.x === x && cell.y === y)) {
            const response = await axios.post('/action', {
              actionType: 'build',
              x,
              y,
            });
            setGameState(response.data);
            setSelectableCells([]);
            setSelectedWorker(null);
          } else {
            setErrorMessage('Invalid build location. Please select a highlighted cell.');
          }
        }
      }
    } catch (error) {
      setErrorMessage(error.response?.data?.error || 'Action failed.');
    }
  };
    const getSelectableCellsForMove = (worker) => {
        const directions = [
            [-1, -1], [-1, 0], [-1, 1],
            [0, -1],         [0, 1],
            [1, -1], [1, 0], [1, 1]
        ];
        const { x, y } = worker.position;
        const currentHeight = gameState.grid[x][y].height; // Ensure correct access

        return directions
            .map(([dx, dy]) => ({ x: x + dx, y: y + dy }))
            .filter(
                ({ x, y }) => {
                    // Boundary checks
                    if (x < 0 || x >= 5 || y < 0 || y >= 5) return false;

                    const targetCell = gameState.grid[x][y];

                    // Check if the cell is unoccupied and meets height requirements
                    return (
                        !targetCell.worker &&
                        targetCell.height - currentHeight <= 1 &&
                        targetCell.height < 4
                    );
                }
            );
    };


  const getSelectableCellsForBuild = (worker) => {
    const directions = [
      [-1, -1],
      [-1, 0],
      [-1, 1],
      [0, -1],
      [0, 1],
      [1, -1],
      [1, 0],
      [1, 1],
    ];
    const { x, y } = worker.position;

    return directions
      .map(([dx, dy]) => ({ x: x + dx, y: y + dy }))
      .filter(
        ({ x, y }) => {
          // Boundary checks
          if (x < 0 || x >= 5 || y < 0 || y >= 5) return false;

          const targetCell = gameState.grid[x][y];

          // Check if the cell is unoccupied and meets height requirements
          return (
              !targetCell.worker &&
              targetCell.height < 4
          );
      }
    );
          
  };

  return (
    <div className="game-container">
      {loading && <div>Loading...</div>}
      <button className="btn-restart" onClick={startNewGame}>
        Restart Game
      </button>
      {errorMessage && <div className="error-message">{errorMessage}</div>}
      {gameEnded && (
        <div className="victory-modal">
          <div className="modal-content">
            <h2>Congratulations!</h2>
            <p>{winner} has won the game!</p>
            <button onClick={startNewGame}>Play Again</button>
          </div>
        </div>
      )}
      {gameState && (
        <>
          <StatusBar gameState={gameState} />
          <Board
            grid={gameState.grid}
            onCellClick={handleCellClick}
            selectableCells={selectableCells}
            selectedWorker={selectedWorker}
          />
        </>
      )}
    </div>
  );
}

export default Game;