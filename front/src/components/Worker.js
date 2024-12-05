// src/components/Worker.js
import React from 'react';
import '../assets/styles/Worker.css';

function Worker({ id, player, onClick, isSelected }) {
    const workerImage =
        player === 'Player A' ? '/images/worker-blue.png' : '/images/worker-red.png';

    const handleClick = (e) => {
        e.stopPropagation(); // Prevent the click event from bubbling up to the cell
        if (onClick) onClick();
    };

    return (
        <img
            src={workerImage}
            alt={`Worker of ${player}`}
            className={`worker ${player === 'Player A' ? 'worker-blue' : 'worker-red'} ${
                isSelected ? 'worker-selected' : ''
            }`}
            onClick={handleClick}
        />
    );
}

export default Worker;
