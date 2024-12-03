// src/components/Cell.js
import React from 'react';
import classNames from 'classnames';
import Worker from './Worker';
import '../assets/styles/Cell.css';




function Cell({ data, onClick, isSelectable, selectedWorker }) {
    const { x, y, height, worker } = data;

    const cellClass = classNames('cell', {
        'cell-selectable': isSelectable,
    });

    const handleCellClick = () => {
        console.log(`Cell clicked: x=${x}, y=${y}`);
        onClick(x, y);
    };

    const handleWorkerClick = () => {
        onClick(x, y);
    };

    const isWorkerSelected =
        selectedWorker &&
        selectedWorker.position.x === x &&
        selectedWorker.position.y === y &&
        selectedWorker.player === worker.player;

    // Determine the tower image based on height
    
    const towerImages = {
        1: '/images/1.png',
        2: '/images/2.png',
        3: '/images/3.png',
        4: '/images/dome.png',
    };
    const towerImage = towerImages[height] || null;
    return (
        <div className={cellClass} onClick={handleCellClick}>
            {/* Render tower based on height */}
            {towerImage && (
                <div
                    className="tower"
                    style={{ backgroundImage: `url(${towerImage})` }}
                ></div>
            )}
            {/* Render worker if present */}
            {worker && (
                <Worker
                    player={worker.player}
                    onClick={handleWorkerClick}
                    isSelected={isWorkerSelected}
                />
            )}
        </div>
    );
}


export default Cell;
