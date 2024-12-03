
package org.example;

import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class App extends NanoHTTPD {

    private Game game;


    public App() throws IOException {
        super(8080);
        this.game = new Game(); // Initialize the game
        start(SOCKET_READ_TIMEOUT, false);
        System.out.println("Server running at http://localhost:8080/");
    }

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException e) {
            System.err.println("Couldn't start server:\n" + e);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();

        try {
            if (uri.equals("/") || uri.startsWith("/static/")) {
                return serveStaticFile(uri);
            } else if (method == Method.POST && uri.equals("/start-game")) {
                return handleStartGame();
            } else if (method == Method.GET && uri.equals("/game-state")) {
                return handleGetGameState();
            } else if (method == Method.POST && uri.equals("/action")) {
                return handleAction(session);
            } else {
                return createJsonResponse(Response.Status.NOT_FOUND, Map.of("error", "Endpoint not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createJsonResponse(Response.Status.INTERNAL_ERROR, Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles the `/start-game` endpoint to reset the game.
     *
     * @return The HTTP response indicating the game has been restarted.
     */
    private Response handleStartGame() {
        this.game = new Game(); // Reset the game

        // Prepare the game state to return
        Map<String, Object> state = new HashMap<>();
        state.put("message", "Game restarted");
        state.put("grid", serializeGrid());
        state.put("workers", serializeWorkers());
        state.put("currentPlayer", game.getCurrentPlayer().getName());
        state.put("gamePhase", game.getCurrentPhase().toString());
        state.put("gameEnded", game.isGameEnded());
        String status = game.isGameEnded() ? game.getCurrentPlayer().getName() + " Wins!" : "In Progress";
        state.put("status", status);

        return createJsonResponse(Response.Status.OK, state);
    }


    /**
     * Handles the `/game-state` endpoint to retrieve the current game state.
     *
     * @return The HTTP response containing the game state.
     */
    private Response handleGetGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("grid", serializeGrid());
        state.put("workers", serializeWorkers());
        state.put("currentPlayer", game.getCurrentPlayer().getName());
        state.put("gamePhase", game.getCurrentPhase().toString());
        state.put("gameEnded", game.isGameEnded());
        String status = game.isGameEnded() ? game.getCurrentPlayer().getName() + " Wins!" : "In Progress";
        state.put("status", status);
        return createJsonResponse(Response.Status.OK, state);
    }

    /**
     * Serializes the game board into a JSON-friendly format.
     *
     * @return A list representing the serialized grid.
     */
    private List<List<Map<String, Object>>> serializeGrid() {
        int boardSize = 5; // Assuming a 5x5 board
        List<List<Map<String, Object>>> serializedGrid = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            List<Map<String, Object>> row = new ArrayList<>();
            for (int y = 0; y < boardSize; y++) {
                Map<String, Object> cellData = new HashMap<>();
                cellData.put("x", x);
                cellData.put("y", y);
                cellData.put("height", game.getBoard().getTowerHeight(x, y));

                Worker worker = game.getBoard().getWorkerAt(x, y);
                if (worker != null) {
                    Map<String, Object> workerData = new HashMap<>();
                    workerData.put("id", worker.hashCode()); // or any unique identifier
                    workerData.put("player", worker.getOwner().getName());
                    workerData.put("x", worker.getX());
                    workerData.put("y", worker.getY());
                    cellData.put("worker", workerData);
                }

                row.add(cellData);
            }
            serializedGrid.add(row);
        }

        return serializedGrid;
    }

    /**
     * Serializes the workers into a JSON-friendly format.
     *
     * @return A list representing the serialized workers.
     */
    private List<Map<String, Object>> serializeWorkers() {
        List<Map<String, Object>> workersList = new ArrayList<>();
        for (Worker worker : game.getAllWorkers()) {
            Map<String, Object> workerData = new HashMap<>();
            workerData.put("id", worker.hashCode()); // or any unique identifier
            workerData.put("player", worker.getOwner().getName());
            workerData.put("position", Map.of("x", worker.getX(), "y", worker.getY()));
            workersList.add(workerData);
        }
        return workersList;
    }

    /**
     * Handles the `/action` endpoint to process player actions such as placing a worker, moving, or building.
     *
     * @param session The HTTP session containing the action details.
     * @return The HTTP response indicating the result of the action.
     */
    private Response handleAction(IHTTPSession session) {
        Map<String, String> postData = new HashMap<>();
        try {
            session.parseBody(postData);
        } catch (ResponseException re) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", "ResponseException: " + re.getMessage()));
        } catch (IOException ioe) {
            return createJsonResponse(Response.Status.INTERNAL_ERROR, Map.of("error", "IOException: " + ioe.getMessage()));
        }

        String jsonBody = postData.get("postData");
        if (jsonBody == null || jsonBody.isEmpty()) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", "Empty request body."));
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonBody);
        } catch (Exception e) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", "Invalid JSON format."));
        }

        String actionType = json.optString("actionType", "");
        int workerIndex = json.optInt("workerIndex", -1);
        int x = json.optInt("x", -1);
        int y = json.optInt("y", -1);

        try {
            switch (actionType) {
                case "placeWorker":
                    // Place a worker during the placement phase
                    if (x == -1 || y == -1) {
                        return createJsonResponse(Response.Status.BAD_REQUEST,
                                Map.of("error", "Placement action requires 'x' and 'y'."));
                    }
                    game.placeWorker(x, y);
                    break;

                case "move":
                    // Move a worker
                    if (workerIndex == -1 || x == -1 || y == -1) {
                        return createJsonResponse(Response.Status.BAD_REQUEST,
                                Map.of("error", "Move action requires 'workerIndex', 'x', and 'y'."));
                    }
                    game.moveWorker(workerIndex, x, y);
                    break;

                case "build":
                    // Build action
                    if (x == -1 || y == -1) {
                        return createJsonResponse(Response.Status.BAD_REQUEST,
                                Map.of("error", "Build action requires 'x' and 'y'."));
                    }
                    game.build(x, y);
                    break;

                default:
                    // Handle invalid action type
                    return createJsonResponse(Response.Status.BAD_REQUEST,
                            Map.of("error", "Invalid action type."));
            }

            // Prepare the game state to return
            Map<String, Object> state = new HashMap<>();
            state.put("message", "Action processed.");
            state.put("grid", serializeGrid());
            state.put("workers", serializeWorkers());
            state.put("currentPlayer", game.getCurrentPlayer().getName());
            state.put("gamePhase", game.getCurrentPhase().toString());
            state.put("gameEnded", game.isGameEnded());
            String status = game.isGameEnded() ? game.getCurrentPlayer().getName() + " Wins!" : "In Progress";
            state.put("status", status);

            return createJsonResponse(Response.Status.OK, state);

        } catch (Exception e) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", e.getMessage()));
        }
    }

    /**
     * Utility method to create a JSON-formatted HTTP response.
     *
     * @param status The HTTP status.
     * @param data   The data to include in the JSON response.
     * @return The HTTP response.
     */
    private Response createJsonResponse(Response.Status status, Map<String, Object> data) {
        return newFixedLengthResponse(status, "application/json", new JSONObject(data).toString());
    }
    /**
     * Serves static files (HTML, CSS, JS) from the 'static' directory.
     *
     * @param uri The requested URI.
     * @return The HTTP response containing the file content.
     */
    private Response serveStaticFile(String uri) {
        String filePath;
        if (uri.equals("/")) {
            filePath = "static/index.html"; // Default to index.html
        } else {
            filePath = uri.substring(1); // Remove leading '/'
        }

        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not found");
        }

        String mimeType = getMimeTypeForFile(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            return newFixedLengthResponse(Response.Status.OK, mimeType, fis, file.length());
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Internal Server Error");
        }
    }
}