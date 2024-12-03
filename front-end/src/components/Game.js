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

    useEffect(() => {
        startNewGame();
    }, []);

    useEffect(() => {
        if (gameState) {
            const { currentPlayer, workers } = gameState;
            const playerWorkers = workers.filter((w) => w.player === currentPlayer);
            setCurrentPlayerWorkers(playerWorkers);
        }
    }, [gameState]);

    const startNewGame = async () => {
      
            const response = await axios.post('/start-game');
            setGameState(response.data);
            setSelectableCells([]);
            setSelectedWorker(null);
            setErrorMessage('');
      
    };

    const handleCellClick = async (x, y) => {
        if (!gameState) return;
        setErrorMessage('');
        const { gamePhase, currentPlayer } = gameState;

        try {
            if (gamePhase === 'PLACEMENT') {
                // Place worker action
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
                    // Move the worker
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
                    // Select a worker
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
                            setSelectableCells(cells);
                        } else {
                            setErrorMessage('Worker index not found.');
                        }
                    } else {
                        setErrorMessage('Select your own worker to move.');
                    }
                }
            } else if (gamePhase === 'BUILD') {
                // Build action
                const response = await axios.post('/action', {
                    actionType: 'build',
                    x,
                    y,
                });
                setGameState(response.data);
                setSelectableCells([]);
                setSelectedWorker(null);
            }
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Action failed.');
        }
    };

    const getSelectableCellsForMove = (worker) => {
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
        const currentHeight = gameState.grid[x][y].height;

        return directions
            .map(([dx, dy]) => ({ x: x + dx, y: y + dy }))
            .filter(
                ({ x, y }) =>
                    x >= 0 &&
                    x < 5 &&
                    y >= 0 &&
                    y < 5 &&
                    !gameState.grid[x][y].worker &&
                    gameState.grid[x][y].height < 4 && // No dome
                    gameState.grid[x][y].height - currentHeight <= 1 // Height difference at most +1
            );
    };

    return (
        <div className="game-container">
            <button className="btn-restart" onClick={startNewGame}>
                Restart Game
            </button>
            {errorMessage && <div className="error-message">{errorMessage}</div>}
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
