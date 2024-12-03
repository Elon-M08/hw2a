// src/components/Board.js
import React from 'react';
import Cell from './Cell';
import '../assets/styles/Board.css';

function Board({ grid, onCellClick, selectableCells, selectedWorker }) {
    const boardSize = grid.length;

    return (
        <div className="board">
            {[...Array(boardSize)].map((_, y) => (
                <div key={y} className="board-row">
                    {[...Array(boardSize)].map((_, x) => {
                        const cellData = grid[x][y];
                        return (
                            <Cell
                                key={`${x}-${y}`}
                                data={cellData}
                                onClick={onCellClick}
                                isSelectable={selectableCells.some(
                                    (cell) => cell.x === x && cell.y === y
                                )}
                                selectedWorker={selectedWorker}
                            />
                        );
                    })}
                </div>
            ))}
        </div>
    );
}

export default Board;
