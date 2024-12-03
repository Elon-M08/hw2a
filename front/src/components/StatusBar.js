// src/components/StatusBar.js
import React from 'react';
import '../assets/styles/StatusBar.css';


function StatusBar({ gameState }) {
    const { currentPlayer, gamePhase, status } = gameState;

    return (
        <div className="status-bar">
            <div>
                <strong>Status:</strong> {status}
            </div>
            <div>
                <strong>Current Player:</strong> {currentPlayer}
            </div>
            <div>
                <strong>Phase:</strong> {gamePhase}
            </div>
        </div>
    );
}

export default StatusBar;
