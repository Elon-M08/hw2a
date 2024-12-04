// src/components/Board.js
import React from 'react';
import Cell from './Cell';
import '../assets/styles/Board.css';

function Board({ grid, onCellClick, selectableCells, selectedWorker }) {
    const boardSize = grid.length; // Should be 5 for a 5x5 grid
    return (
        <div className="board">
            {[...Array(boardSize)].map((_, y) => ( // Loop over rows (y)
                <div key={y} className="board-row">
                    {[...Array(boardSize)].map((_, x) => ( // Loop over columns (x)
                        <Cell
                            key={`${x}-${y}`}
                            x={x}
                            y={y}
                            data={grid[x][y]} // Access grid[x][y]
                            onClick={onCellClick}
                            isSelectable={selectableCells.some(
                                (cell) => cell.x === x && cell.y === y
                            )}
                            selectedWorker={selectedWorker}
                        />
                    ))}
                </div>
            ))}
        </div>
    );
}

export default Board;
