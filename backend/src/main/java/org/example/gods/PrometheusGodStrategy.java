// // src/main/java/org/example/gods/PrometheusGodStrategy.java
// package org.example.gods;

// import org.example.Board;
// import org.example.Game;
// import org.example.Worker;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.logging.Logger;

// /**
//  * Prometheus's Strategy Implementation.
//  * Prometheus allows a player to build before moving.
//  * If a player builds before moving, they cannot move up during their movement phase.
//  */
// public class PrometheusGodStrategy extends DefaultGodStrategy {
//     private static final Logger logger = Logger.getLogger(PrometheusGodStrategy.class.getName());

//     // Tracks if the player has built before moving
//     private boolean hasBuiltBeforeMove = false;

//     @Override
//     public String getName() {
//         return "Prometheus";
//     }

//     @Override
//     public Map<String, Object> getStrategyState() {
//         Map<String, Object> state = super.getStrategyState();
//         state.put("hasBuiltBeforeMove", hasBuiltBeforeMove);
//         return state;
//     }

//     /**
//      * Overrides the build method to allow building before moving.
//      */
//     @Override
//     public boolean build(Game game, Worker worker, int x, int y) throws Exception {
//         Board board = game.getBoard();

//         if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
//             // Build before moving
//             if (hasBuiltBeforeMove) {
//                 logger.warning(getName() + " Strategy: Already built before moving.");
//                 throw new Exception("You have already built before moving.");
//             }

//             boolean buildSuccess = super.build(game, worker, x, y);
//             if (buildSuccess) {
//                 hasBuiltBeforeMove = true;
//                 strategyState.put("hasBuiltBeforeMove", hasBuiltBeforeMove);
//                 logger.info(getName() + " Strategy: Built before moving. Movement restrictions applied.");
//             }
//             return buildSuccess;
//         } else if (game.getCurrentPhase() == Game.GamePhase.BUILD_AFTER_MOVE) {
//             // Build after moving
//             return super.build(game, worker, x, y);
//         } else {
//             logger.warning(getName() + " Strategy: Cannot build at this phase.");
//             throw new Exception("Cannot build at this phase.");
//         }
//     }

//     /**
//      * Overrides the move method to enforce movement restrictions if built before moving.
//      */
//     @Override
//     public boolean move(Game game, Worker worker, int x, int y) throws Exception {
//         Board board = game.getBoard();
//         int fromX = worker.getX();
//         int fromY = worker.getY();
//         int fromHeight = board.getTowerHeight(fromX, fromY);
//         int toHeight = board.getTowerHeight(x, y);

//         // If built before moving, cannot move up
//         if (hasBuiltBeforeMove && (toHeight > fromHeight)) {
//             logger.warning(getName() + " Strategy: Cannot move up after building before moving.");
//             throw new Exception("Cannot move up after building before moving.");
//         }

//         // Perform the move using the superclass's move method
//         boolean moveSuccess = super.move(game, worker, x, y);
//         if (!moveSuccess) {
//             logger.warning(getName() + " Strategy: Move failed.");
//             throw new Exception("Invalid move. Try again.");
//         }

//         logger.info(getName() + " Strategy: Move completed to (" + x + ", " + y + ").");
//         return true;
//     }

//     /**
//      * Overrides the nextPhase method to handle phase transitions based on build state.
//      */
//     @Override
//     public void nextPhase(Game game) throws Exception {
//         logger.info(getName() + " Strategy: nextPhase called.");

//         if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
//             if (hasBuiltBeforeMove) {
//                 // Proceed to move phase with movement restrictions
//                 game.setCurrentPhase(Game.GamePhase.BUILD_AFTER_MOVE);
//                 logger.info(getName() + " Strategy: Proceeding to move phase with movement restrictions.");
//             } else {
//                 // Standard move phase
//                 super.nextPhase(game);
//                 logger.info(getName() + " Strategy: Proceeding to move phase without movement restrictions.");
//             }
//         } else if (game.getCurrentPhase() == Game.GamePhase.BUILD_AFTER_MOVE || game.getCurrentPhase() == Game.GamePhase.BUILD) {
//             // Proceed to build phase or end turn
//             super.nextPhase(game);
//             logger.info(getName() + " Strategy: Proceeding to build phase or ending turn.");
//         }
//     }

//     /**
//      * Overrides the checkVictory method to include Prometheus's special victory condition.
//      * Prometheus's victory conditions are the same as the standard ones.
//      */
//     @Override
//     public boolean checkVictory(Game game, Worker worker) throws Exception {
//         // Standard victory conditions
//         return super.checkVictory(game, worker);
//     }

//     /**
//      * Overrides the playerEndsTurn method to reset Prometheus's state.
//      */
//     @Override
//     public void playerEndsTurn(Game game) throws Exception {
//         logger.info(getName() + " Strategy: playerEndsTurn called.");
//         // Reset Prometheus's build state
//         hasBuiltBeforeMove = false;
//         strategyState.put("hasBuiltBeforeMove", hasBuiltBeforeMove);
//         // Delegate to superclass to handle any additional reset logic
//         super.playerEndsTurn(game);
//     }

//     /**
//      * Overrides setCannotMoveUp, but Prometheus's strategy does not utilize this method.
//      */
//     @Override
//     public void setCannotMoveUp(boolean cannotMoveUp) {
//         // Prometheus's strategy does not utilize this method
//         // Do nothing
//     }
// }
