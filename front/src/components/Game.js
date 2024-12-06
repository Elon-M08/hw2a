// src/components/Game.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Board from './Board';
import StatusBar from './StatusBar';
import '../assets/styles/Game.css';
import GodCardsAppendix from './GodCardsAppendix';

axios.defaults.baseURL = 'http://localhost:8080';

function Game() {
    // State variables
    const [gameState, setGameState] = useState(null);
    const [buildDomeOption, setBuildDomeOption] = useState(false);
    const [awaitingBuildDomeDecision, setAwaitingBuildDomeDecision] = useState(false);

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

    const [showGodCards, setShowGodCards] = useState(false); // New state for modal
    const [strategyState, setStrategyState] = useState({});
    const [awaitingSecondBuildDecision, setAwaitingSecondBuildDecision] = useState(false);
    const [awaitingSecondMoveDecision, setAwaitingSecondMoveDecision] = useState(false);
    const [awaitingBuildBeforeMoveDecision, setAwaitingBuildBeforeMoveDecision] = useState(false);
    const [currentPlayerGod, setCurrentPlayerGod] = useState('');
    const godOptions = [
        'Apollo',
        'Artemis',
        'Athena',
        'Atlas',
        'Demeter',
        'Hephaestus',
        'Minotaur',
        'Pan',
        'Prometheus',
        'Hermes',
    ];

    useEffect(() => {
        if (gameState) {
            // Destructure with default for strategyState
            const { currentPlayer, workers, gameEnded, winner: gameWinner, strategyState: gs = {}, currentPlayerGod: god } = gameState;
            const playerWorkers = workers.filter((w) => w.player === currentPlayer);
            setCurrentPlayerWorkers(playerWorkers);

            if (gameEnded) {
                setGameEnded(true);
                setWinner(gameWinner);
            } else {
                setGameEnded(false);
                setWinner(null);
            }

            // Reset selections when the game state updates
            setSelectableCells([]);
            setSelectedWorker(null);

            // Update strategyState
            setStrategyState(gs);

            // Check if we need to prompt for extra build (Demeter or Hephaestus)
            if (gs.extraBuildAvailable) {
                setAwaitingSecondBuildDecision(true);
            } else {
                setAwaitingSecondBuildDecision(false);
            }

            // Check if Prometheus can build before move
            if (gs.canBuildBeforeMove) {
                setAwaitingBuildBeforeMoveDecision(true);
            } else {
                setAwaitingBuildBeforeMoveDecision(false);
            }

            // Check if Atlas can build a dome
            if (gs.canBuildDome) {
                setAwaitingBuildDomeDecision(true);
            } else {
                setAwaitingBuildDomeDecision(false);
            }

            // Check if Artemis has an extra move
            if (gs.extraMoveAvailable && god === 'Artemis') {
                setAwaitingSecondMoveDecision(true);
            } else {
                setAwaitingSecondMoveDecision(false);
            }

            // Store current player's God
            setCurrentPlayerGod(god);
        }
    }, [gameState]);

    // Start a new game
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
            setErrorMessage(error.response?.data?.error || 'Failed to start the game.');
        } finally {
            setLoading(false);
        }
    };

    // Reset the game to initial state
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

    // Fetch the latest game state from the backend
    const fetchGameState = async () => {
        try {
            const response = await axios.get('/game-state');
            setGameState(response.data);
        } catch (error) {
            setErrorMessage('Failed to fetch game state.');
        }
    };

    // Handle cell clicks based on game phase
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
            } else if (gamePhase === 'BUILD_BEFORE_MOVE') {
                // Handle Prometheus's pre-move build
                if (selectedWorker) {
                    const response = await axios.post('/action', {
                        actionType: 'build',
                        workerIndex: selectedWorker.index,
                        x,
                        y,
                    });
                    setGameState(response.data);
                    setSelectableCells([]);
                    // After building before move, fetch the updated game state
                    fetchGameState();
                } else {
                    setErrorMessage('No worker selected for building.');
                }
            } else if (gamePhase === 'BUILD') {
                if (selectedWorker) {
                    // Attempt to build at the selected cell
                    if (selectableCells.some((cell) => cell.x === x && cell.y === y)) {
                        const buildData = {
                            actionType: 'build',
                            workerIndex: selectedWorker.index,
                            x,
                            y,
                        };
                        // Include buildDome option if Atlas is the current player's god
                        if (currentPlayerGod === 'Atlas') {
                            buildData.buildDome = buildDomeOption;
                        }
                        const response = await axios.post('/action', buildData);
                        setGameState(response.data);

                        // Update strategyState
                        const gs = response.data.strategyState || {};
                        setStrategyState(gs);

                        // Check if the strategy indicates extra builds are available
                        if (gs.extraBuildAvailable) {
                            // The strategy indicates that an extra build is available
                            setAwaitingSecondBuildDecision(true);
                            setSelectableCells([]);
                        } else {
                            // Proceed to next phase
                            setSelectableCells([]);
                            setSelectedWorker(null);
                        }
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

    // Handler for Artemis's extra move (Yes)
    const handleSecondMoveYes = async () => {
        try {
            const response = await axios.get('/selectable-move-cells', {
                params: { workerIndex: selectedWorker.index },
            });
            const cells = response.data.selectableCells;
            if (cells.length === 0) {
                setErrorMessage('No valid moves for this worker.');
                setAwaitingSecondMoveDecision(false);
            } else {
                setSelectableCells(cells);
                setAwaitingSecondMoveDecision(false);
            }
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to get selectable move cells.');
        }
    };

    // Handler for Artemis's extra move (No)
    const handleSecondMoveNo = async () => {
        try {
            await axios.post('/action', { actionType: 'endTurn' });
            const response = await axios.get('/game-state');
            setGameState(response.data);
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to end turn.');
        }
        setAwaitingSecondMoveDecision(false);
    };

    // Handler for Demeter and Hephaestus's extra build (Yes)
    const handleSecondBuildYes = async () => {
        try {
            // Fetch selectable build cells for the second build
            const response = await axios.get('/selectable-build-cells', {
                params: { workerIndex: selectedWorker.index },
            });
            const cells = response.data.selectableCells;
            if (cells.length === 0) {
                setErrorMessage('No valid build locations for second build.');
                // Inform the backend that the player ends their turn
                await axios.post('/action', { actionType: 'endTurn' });
                // Fetch updated game state
                const gameStateResponse = await axios.get('/game-state');
                setGameState(gameStateResponse.data);
            } else {
                setSelectableCells(cells);
            }
            setAwaitingSecondBuildDecision(false);
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to get selectable build cells.');
        }
    };

    // Handler for Demeter and Hephaestus's extra build (No)
    const handleSecondBuildNo = async () => {
        try {
            // Inform the backend that the player ends their turn
            const response = await axios.post('/action', { actionType: 'endTurn' });
            setGameState(response.data);
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to end turn.');
        }
        setAwaitingSecondBuildDecision(false);
    };

    // Handler for Atlas's dome build option
    const handleBuildDomeOption = async (buildDome) => {
        setBuildDomeOption(buildDome);
        setAwaitingBuildDomeDecision(false);
        // Proceed to select build location
        try {
            const response = await axios.get('/selectable-build-cells', {
                params: { workerIndex: selectedWorker.index },
            });
            const cells = response.data.selectableCells;
            if (cells.length === 0) {
                setErrorMessage('No valid build locations.');
            } else {
                setSelectableCells(cells);
            }
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to get selectable build cells.');
        }
    };

    // Handler for ending Hermes's move
    const handleEndMove = async () => {
        try {
            await axios.post('/action', { actionType: 'endTurn' });
            const response = await axios.get('/game-state');
            setGameState(response.data);
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to end turn.');
        }
    };

    // Handler functions for Prometheus's pre-move build
    const handlePreMoveBuildYes = async () => {
        try {
            // Fetch selectable build cells
            const response = await axios.get('/selectable-build-cells', {
                params: { workerIndex: selectedWorker.index },
            });
            const cells = response.data.selectableCells;
            if (cells.length === 0) {
                setErrorMessage('No valid build locations.');
            } else {
                setSelectableCells(cells);
                // You might need to send an action to indicate building before moving
                // For simplicity, assuming the backend handles it after building
            }
            setAwaitingBuildBeforeMoveDecision(false);
        } catch (error) {
            setErrorMessage(error.response?.data?.error || 'Failed to get selectable build cells.');
        }
    };

    const handlePreMoveBuildNo = () => {
        setAwaitingBuildBeforeMoveDecision(false);
        // Proceed to move phase by potentially sending an action to backend
        // For simplicity, assuming the backend expects the player to move next
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
                    <div className="top-bar">
                        <button className="btn-restart" onClick={resetGame}>
                            Restart Game
                        </button>
                        <button className="btn-god-cards" onClick={() => setShowGodCards(true)}>
                            View God Cards
                        </button>
                    </div>
                    {gameState && (
                        <>
                            <StatusBar gameState={gameState} />
                            <Board
                                grid={gameState.grid}
                                onCellClick={handleCellClick}
                                selectableCells={selectableCells}
                                selectedWorker={selectedWorker}
                            />
                            {/* Artemis's Extra Move Decision */}
                            {awaitingSecondMoveDecision && currentPlayerGod === 'Artemis' && (
                                <div className="decision-modal">
                                    <div className="modal-content">
                                        <h2>Artemis's Power</h2>
                                        <p>You may move one additional time, but not back to your initial space. Do you want to move again?</p>
                                        <button onClick={handleSecondMoveYes}>Yes</button>
                                        <button onClick={handleSecondMoveNo}>No</button>
                                    </div>
                                </div>
                            )}
                            {/* Prometheus's Pre-Move Build Decision */}
                            {awaitingBuildBeforeMoveDecision && currentPlayerGod === 'Prometheus' && (
                                <div className="decision-modal">
                                    <div className="modal-content">
                                        <h2>Prometheus's Power</h2>
                                        <p>If your Worker does not move up, it may build both before and after moving. Do you want to build before moving?</p>
                                        <button onClick={handlePreMoveBuildYes}>Yes</button>
                                        <button onClick={handlePreMoveBuildNo}>No</button>
                                    </div>
                                </div>
                            )}
                            {/* Atlas's Dome Build Decision */}
                            {awaitingBuildDomeDecision && currentPlayerGod === 'Atlas' && (
                                <div className="decision-modal">
                                    <div className="modal-content">
                                        <h2>Atlas's Power</h2>
                                        <p>You may build a dome at any level. Do you want to build a dome?</p>
                                        <button onClick={() => handleBuildDomeOption(true)}>Yes</button>
                                        <button onClick={() => handleBuildDomeOption(false)}>No</button>
                                    </div>
                                </div>
                            )}
                            {/* Demeter and Hephaestus's Extra Build Decision */}
                            {awaitingSecondBuildDecision && (currentPlayerGod === 'Demeter' || currentPlayerGod === 'Hephaestus') && (
                                <div className="decision-modal">
                                    <div className="modal-content">
                                        {currentPlayerGod === 'Demeter' && (
                                            <>
                                                <h2>Demeter's Power</h2>
                                                <p>
                                                    You may build one additional time (not on the same space). Do you want to build again?
                                                </p>
                                            </>
                                        )}
                                        {currentPlayerGod === 'Hephaestus' && (
                                            <>
                                                <h2>Hephaestus's Power</h2>
                                                <p>
                                                    You may build one additional block on top of your first block. Do you want to build again?
                                                </p>
                                            </>
                                        )}
                                        <button onClick={handleSecondBuildYes}>Yes</button>
                                        <button onClick={handleSecondBuildNo}>No</button>
                                    </div>
                                </div>
                            )}
                            {/* Hermes's End Move Button */}
                            {currentPlayerGod === 'Hermes' && gameState.gamePhase === 'MOVE' && (
                                <div className="hermes-end-move-button">
                                    <button onClick={handleEndMove}>End Move</button>
                                </div>
                            )}
                        </>
                    )}
               {showGodCards && <GodCardsAppendix onClose={() => setShowGodCards(false)} />}
                </>
            ) : (
                <div className="god-selection-container">
                    <h2>Select God Cards</h2>
                    <div>
                        <label>Player A God:</label>
                        <select value={playerAGod} onChange={(e) => setPlayerAGod(e.target.value)}>
                            <option value="">Select God</option>
                            {godOptions.map((god) => (
                                <option key={god} value={god}>
                                    {god}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label>Player B God:</label>
                        <select value={playerBGod} onChange={(e) => setPlayerBGod(e.target.value)}>
                            <option value="">Select God</option>
                            {godOptions.map((god) => (
                                <option key={god} value={god}>
                                    {god}
                                </option>
                            ))}
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
