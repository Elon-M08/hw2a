// src/components/GodCardsAppendix.js
import React from 'react';
import '../assets/styles/GodCardsAppendix.css';

function GodCardsAppendix({ onClose }) {
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <h2>Appendix: God Cards</h2>
                <p>
                    You can find the original rules of Santorini including all god cards at: <a href="https://roxley.com/products/santorini" target="_blank" rel="noopener noreferrer">https://roxley.com/products/santorini</a>
                </p>
                <p>We consider only basic god cards. The bold ones are required:</p>
                <ul>
                    <li><strong>Apollo:</strong> Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.</li>
                    <li><strong>Artemis:</strong> Your Worker may move one additional time, but not back to its initial space. (UI hint: You will likely need a way to indicate that the player wants to skip the optional second move, either with a "pass" button or by clicking on the worker's current location)</li>
                    <li><strong>Athena:</strong> During opponent’s turn: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.</li>
                    <li><strong>Atlas:</strong> Your Worker may build a dome at any level. (UI hint: You can implement this in the user interface similar to Hephaestus, giving the player a second optional build action; this build action is interpreted as building a dome)</li>
                    <li><strong>Demeter:</strong> Your Worker may build one additional time, but not on the same space. (UI hint: You will likely need a way to indicate that the player wants to skip the optional second build, e.g., with a "pass" button or by clicking on the worker's current location)</li>
                    <li><strong>Hephaestus:</strong> Your Worker may build one additional block (not dome) on top of your first block. (UI hint: You will likely need a way to indicate that the player wants to skip the optional second build, e.g., with a "pass" button or by clicking on the worker's current location)</li>
                    <li><strong>Hermes:</strong> If your Workers do not move up or down, they may each move any number of times (even zero), and then either builds. (UI hint: Rather than allowing multiple move actions, it might be easier to indicate all possible target spaces where a worker can move too)</li>
                    <li><strong>Minotaur:</strong> Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.</li>
                    <li><strong>Pan:</strong> You also win if your Worker moves down two or more levels.</li>
                    <li><strong>Prometheus:</strong> If your Worker does not move up, it may build both before and after moving.</li>
                </ul>
                <button className="close-button" onClick={onClose}>Close</button>
            </div>
        </div>
    );
}

export default GodCardsAppendix;
