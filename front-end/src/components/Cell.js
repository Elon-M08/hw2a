// src/components/Cell.js
import React from 'react';
import classNames from 'classnames';
import Worker from './Worker';
import '../assets/styles/Cell.css';


function Cell({ data, onClick, isSelectable, selectedWorker }) {
    const { x, y, height, worker } = data;

    const cellClass = classNames('cell', {
        'cell-selectable': isSelectable,
        [`height-${height}`]: true,
    });

    const handleCellClick = () => {
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

    return (
        <div className={cellClass} onClick={handleCellClick}>
            {/* Render tower based on height */}
            {height > 0 && <div className={`tower tower-${height}`}></div>}
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
