import React from 'react';
import Game from './components/Game';
import './assets/styles/App.css'; // Add global styles here if needed

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <h1>Santorini Game</h1>
            </header>
            <main>
                {/* Render the Game component */}
                <Game />
            </main>
            <footer className="App-footer">
                <p>© 2024 Santorini Game | Built with React</p>
            </footer>
        </div>
    );
}

export default App;


