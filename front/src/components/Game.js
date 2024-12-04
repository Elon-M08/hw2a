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

    const [playerAGod, setPlayerAGod] = useState('');
    const [playerBGod, setPlayerBGod] = useState('');
    const [isGameStarted, setIsGameStarted] = useState(false);

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
            const response = await axios.post('/start-game', {
                playerAGod,
                playerBGod,
            });
            console.log('Game state after restart:', response.data);
            setGameState(response.data);
            setSelectableCells([]);
            setSelectedWorker(null);
            setErrorMessage('');
            setGameEnded(false);
            setWinner(null);
            setIsGameStarted(true);
        } catch (error) {
            setErrorMessage('Failed to start the game.');
        } finally {
            setLoading(false);
        }
    };

    const resetGame = () => {
        setIsGameStarted(false);
        setPlayerAGod('');
        setPlayerBGod('');
        setGameState(null);
        setSelectableCells([]);
        setSelectedWorker(null);
        setErrorMessage('');
        setGameEnded(false);
        setWinner(null);
    };

    const handleCellClick = async (x, y) => {
        console.log(`Cell clicked: x=${x}, y=${y}`);
        if (!gameState || gameEnded) return;
        setErrorMessage('');
        const { gamePhase, currentPlayer } = gameState;
    
        try {
            if (gamePhase === 'PLACEMENT') {
                // Place a worker
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
                    // Attempt to move to the selected cell
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
                        (w) => w.position.x === x && w.position.y === y && w.player === currentPlayer
                    );
                    if (worker) {
                        const workerIndex = currentPlayerWorkers.findIndex(
                            (w) => w.position.x === x && w.position.y === y
                        );
                        if (workerIndex !== -1) {
                            setSelectedWorker({ ...worker, index: workerIndex });
                            // Fetch selectable cells from the backend
                            const response = await axios.get('/selectable-move-cells', {
                                params: { workerIndex: workerIndex },
                            });
                            const cells = response.data.selectableCells;
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
                if (selectedWorker) {
                    // Attempt to build at the selected cell
                    if (selectableCells.some((cell) => cell.x === x && cell.y === y)) {
                        const response = await axios.post('/action', {
                            actionType: 'build',
                            workerIndex: selectedWorker.index,
                            x,
                            y,
                        });
                        setGameState(response.data);
                        setSelectableCells([]);
                        setSelectedWorker(null);
                    } else {
                        setErrorMessage('Invalid build location. Please select a highlighted cell.');
                    }
                } else {
                    // Select a worker to build with
                    const worker = gameState.workers.find(
                        (w) => w.position.x === x && w.position.y === y && w.player === currentPlayer
                    );
                    if (worker) {
                        const workerIndex = currentPlayerWorkers.findIndex(
                            (w) => w.position.x === x && w.position.y === y
                        );
                        if (workerIndex !== -1) {
                            setSelectedWorker({ ...worker, index: workerIndex });
                            // Fetch selectable cells from the backend
                            const response = await axios.get('/selectable-build-cells', {
                                params: { workerIndex: workerIndex },
                            });
                            const cells = response.data.selectableCells;
                            if (cells.length === 0) {
                                setErrorMessage('No valid build locations for this worker.');
                                setSelectableCells([]);
                            } else {
                                setSelectableCells(cells);
                            }
                        } else {
                            setErrorMessage('Worker index not found.');
                        }
                    } else {
                        setErrorMessage('Please select a worker to build.');
                    }
                }
            }
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Action failed.');
        }
    };
    
    

    return (
        <div className="game-container">
            {loading && <div>Loading...</div>}
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            {gameEnded && (
                <div className="victory-modal">
                    <div className="modal-content">
                        <h2>Congratulations!</h2>
                        <p>{winner} has won the game!</p>
                        <button onClick={resetGame}>Play Again</button>
                    </div>
                </div>
            )}
            {isGameStarted ? (
                <>
                    <button className="btn-restart" onClick={resetGame}>
                        Restart Game
                    </button>
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
                </>
            ) : (
                <div className="god-selection-container">
                    <h2>Select God Cards</h2>
                    <div>
                        <label>Player A God:</label>
                        <select value={playerAGod} onChange={(e) => setPlayerAGod(e.target.value)}>
                            <option value="">Select God</option>
                            <option value="Demeter">Demeter</option>
                            <option value="Hephaestus">Hephaestus</option>
                            <option value="Minotaur">Minotaur</option>
                            <option value="Pan">Pan</option>
                        </select>
                    </div>
                    <div>
                        <label>Player B God:</label>
                        <select value={playerBGod} onChange={(e) => setPlayerBGod(e.target.value)}>
                            <option value="">Select God</option>
                            <option value="Demeter">Demeter</option>
                            <option value="Hephaestus">Hephaestus</option>
                            <option value="Minotaur">Minotaur</option>
                            <option value="Pan">Pan</option>
                        </select>
                    </div>
                    <button onClick={startNewGame} disabled={!playerAGod || !playerBGod}>
                        Start Game
                    </button>
                </div>
            )}
        </div>
    );
}

export default Game;