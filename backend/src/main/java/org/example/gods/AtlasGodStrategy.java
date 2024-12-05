// // src/main/java/org/example/gods/AtlasGodStrategy.java
// package org.example.gods;

// import org.example.Board;
// import org.example.Game;
// import org.example.Worker;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.logging.Logger;

// /**
//  * Atlas's Strategy Implementation.
//  * Atlas allows building a dome at any level, effectively preventing further building on that cell.
//  */
// public class AtlasGodStrategy extends DefaultGodStrategy {
//     private static final Logger logger = Logger.getLogger(AtlasGodStrategy.class.getName());

//     // Flag to determine if a dome should be built
//     private boolean buildDome = false;

//     @Override
//     public String getName() {
//         return "Atlas";
//     }

//     @Override
//     public Map<String, Object> getStrategyState() {
//         Map<String, Object> state = super.getStrategyState();
//         state.put("canBuildDome", buildDome);
//         return state;
//     }

//     /**
//      * Overrides the build method to implement Atlas's special ability.
//      * Allows building a dome at any level.
//      */
//     @Override
//     public boolean build(Game game, Worker worker, int x, int y) throws Exception {
//         Board board = game.getBoard();

//         if (!board.isAdjacent(worker.getX(), worker.getY(), x, y)) {
//             throw new Exception("Build position must be adjacent.");
//         }

//         if (board.isOccupied(x, y)) {
//             throw new Exception("Cannot build on an occupied space.");
//         }

//         if (buildDome) {
//             // Build a dome regardless of current height
//             int currentHeight = board.getTowerHeight(x, y);
//             if (currentHeight >= 4) {
//                 throw new Exception("Cannot build a dome on a cell that already has a dome.");
//             }
//             board.setTowerHeight(x, y, 4);
//             logger.info(getName() + " Strategy: Built a dome at (" + x + ", " + y + ")");
//             buildDome = false;
//             getStrategyState().put("canBuildDome", false);
//         } else {
//             // Standard build
//             boolean buildSuccess = super.build(game, worker, x, y);
//             if (buildSuccess) {
//                 logger.info(getName() + " Strategy: Standard build completed at (" + x + ", " + y + ")");
//             }
//             return buildSuccess;
//         }

//         return true;
//     }

//     /**
//      * Overrides the setBuildDome method to enable dome building.
//      */
//     @Override
//     public void setBuildDome(boolean buildDome) {
//         this.buildDome = buildDome;
//         getStrategyState().put("canBuildDome", this.buildDome);
//         logger.info(getName() + " Strategy: setBuildDome set to " + this.buildDome);
//     }

//     /**
//      * Overrides the nextPhase method to transition to the build phase after building.
//      */
//     @Override
//     public void nextPhase(Game game) throws Exception {
//         logger.info(getName() + " Strategy: nextPhase called");
//         // Proceed to build phase
//         game.setCurrentPhase(Game.GamePhase.BUILD);
//     }

//     /**
//      * Overrides the playerEndsTurn method to reset Atlas's state.
//      */
//     @Override
//     public void playerEndsTurn(Game game) throws Exception {
//         logger.info(getName() + " Strategy: playerEndsTurn called");
//         // Reset Atlas's state
//         buildDome = false;
//         getStrategyState().put("canBuildDome", false);
//         super.playerEndsTurn(game);
//     }

//     /**
//      * Overrides setCannotMoveUp, but Atlas's strategy does not utilize this method.
//      */
//     @Override
//     public void setCannotMoveUp(boolean cannotMoveUp) {
//         // Atlas's strategy does not utilize this method
//         // Do nothing
//     }
// }
