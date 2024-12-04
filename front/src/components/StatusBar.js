// src/components/StatusBar.js
import React from 'react';
import '../assets/styles/StatusBar.css';

function StatusBar({ gameState }) {
    const { currentPlayer, gamePhase, status, playerAGod, playerBGod } = gameState;

    return (
        <div className="status-bar">
            <div>
                <strong>Status:</strong> {status}
            </div>
            <div>
                <strong>Current Player:</strong> {currentPlayer} (
                {currentPlayer === 'Player A' ? playerAGod : playerBGod})
            </div>
            <div>
                <strong>Phase:</strong> {gamePhase}
            </div>
            <div>
                <strong>Player A God:</strong> {playerAGod}
            </div>
            <div>
                <strong>Player B God:</strong> {playerBGod}
            </div>
        </div>
    );
}

export default StatusBar;
