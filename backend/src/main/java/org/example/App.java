// src/main/java/org/example/gods/App.java
package org.example;

import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.example.gods.*; // Ensure all strategy classes are imported

public class App extends NanoHTTPD {

    private Game game;

    public App() throws IOException {
        super(8080);
        this.game = new Game(); // Players will choose their God strategies via API
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
                return addCORSHeaders(serveStaticFile(uri));
            } else if (method == Method.POST && uri.equals("/start-game")) {
                return addCORSHeaders(handleStartGame(session));
            } else if (method == Method.GET && uri.equals("/game-state")) {
                return addCORSHeaders(handleGetGameState());
            } 
            else if (method == Method.GET && uri.equals("/selectable-move-cells")) {
                return addCORSHeaders(handleGetSelectableMoveCells(session));
            } else if (method == Method.GET && uri.equals("/selectable-build-cells")) {
                return addCORSHeaders(handleGetSelectableBuildCells(session));
            }
            else if (method == Method.POST && uri.equals("/action")) {
                return addCORSHeaders(handleAction(session));
            } else if (method == Method.OPTIONS) {
                // Handle CORS preflight requests
                return addCORSHeaders(newFixedLengthResponse(Response.Status.OK, "text/plain", ""));
            } else {
                return addCORSHeaders(createJsonResponse(Response.Status.NOT_FOUND, Map.of("error", "Endpoint not found")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return addCORSHeaders(createJsonResponse(Response.Status.INTERNAL_ERROR, Map.of("error", e.getMessage())));
        }
    }

    private GodStrategy createGodStrategy(String godName) {
        switch (godName.toLowerCase()) {
            case "demeter":
                return new DemeterGodStrategy();
            case "hephaestus":
                return new HephaestusGodStrategy();
            case "minotaur":
                return new MinotaurGodStrategy();
            case "pan":
                return new PanGodStrategy();
            case "apollo":
                return new ApolloGodStrategy();
            case "artemis":
                return new ArtemisGodStrategy();
            case "athena":
                return new AthenaGodStrategy();
            // case "prometheus":
            //     return new PrometheusGodStrategy(); // Uncomment if needed
            // case "atlas":
            //     return new AtlasGodStrategy(); // Uncomment if needed
            case "hermes":
                return new HermesGodStrategy();
            default:
                return new DefaultGodStrategy(); // Use default strategy if invalid
        }
    }

    private Response handleStartGame(IHTTPSession session) throws Exception {
        Map<String, String> postData = new HashMap<>();
        try {
            session.parseBody(postData);
        } catch (ResponseException | IOException e) {
            throw new Exception("Error parsing request body: " + e.getMessage());
        }

        String jsonBody = postData.get("postData");
        if (jsonBody == null || jsonBody.isEmpty()) {
            throw new Exception("Empty request body.");
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonBody);
        } catch (Exception e) {
            throw new Exception("Invalid JSON format.");
        }

        String playerAGod = json.optString("playerAGod", "").toLowerCase();
        String playerBGod = json.optString("playerBGod", "").toLowerCase();

        // Validate and create GodStrategy instances
        GodStrategy playerAStrategy = createGodStrategy(playerAGod);
        GodStrategy playerBStrategy = createGodStrategy(playerBGod);

        // Check if both gods are valid (i.e., not DefaultGodStrategy)
        if (playerAGod.isEmpty() || playerBGod.isEmpty()) {
            throw new Exception("God names cannot be empty.");
        }

        // Optionally, check if gods are unique or allow duplicates
        // For example, to prevent both players from having the same god:
        if (playerAGod.equals(playerBGod)) {
            throw new Exception("Both players cannot have the same God.");
        }

        this.game = new Game(playerAStrategy, playerBStrategy);

        // Prepare the game state to return
        Map<String, Object> state = new HashMap<>();
        state.put("message", "Game started with chosen Gods.");
        state.put("grid", serializeGrid());
        state.put("workers", serializeWorkers());
        state.put("currentPlayer", game.getCurrentPlayer().getName());
        state.put("gamePhase", game.getCurrentPhase().toString());
        state.put("gameEnded", game.isGameEnded());
        String status = game.isGameEnded() ? game.getWinner() + " Wins!" : "In Progress";
        state.put("status", status);
        state.put("playerAGod", game.getPlayerA().getGodStrategy().getName());
        state.put("playerBGod", game.getPlayerB().getGodStrategy().getName());

        // Ensure strategyState is never null
        Map<String, Object> strategyState = game.getCurrentPlayer().getGodStrategy().getStrategyState();
        if (strategyState == null) {
            strategyState = new HashMap<>();
        }
        state.put("strategyState", strategyState);

        state.put("currentPlayerGod", game.getCurrentPlayer().getGodStrategy().getName());

        return createJsonResponse(Response.Status.OK, state);
    }

    private Response handleGetSelectableBuildCells(IHTTPSession session) throws Exception {
        Map<String, String> params = session.getParms();
        int workerIndex = Integer.parseInt(params.get("workerIndex"));

        List<Map<String, Integer>> selectableCells = game.getSelectableBuildCells(workerIndex);
        Map<String, Object> response = new HashMap<>();
        response.put("selectableCells", selectableCells);
        return createJsonResponse(Response.Status.OK, response);
    }

    private Response handleGetSelectableMoveCells(IHTTPSession session) throws Exception {
        Map<String, String> params = session.getParms();
        int workerIndex = Integer.parseInt(params.get("workerIndex"));

        List<Map<String, Integer>> selectableCells = game.getSelectableMoveCells(workerIndex);
        Map<String, Object> response = new HashMap<>();
        response.put("selectableCells", selectableCells);
        return createJsonResponse(Response.Status.OK, response);
    }

    private Response handleGetGameState() throws Exception {
        return createGameStateResponse(null);
    }

    private Response handleAction(IHTTPSession session) throws Exception {
        Map<String, String> postData = new HashMap<>();
        try {
            session.parseBody(postData);
        } catch (ResponseException | IOException e) {
            throw new Exception("Error parsing request body: " + e.getMessage());
        }

        String jsonBody = postData.get("postData");
        if (jsonBody == null || jsonBody.isEmpty()) {
            throw new Exception("Empty request body.");
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonBody);
        } catch (Exception e) {
            throw new Exception("Invalid JSON format.");
        }

        String actionType = json.optString("actionType", "");
        int workerIndex = json.optInt("workerIndex", -1);
        int x = json.optInt("x", -1);
        int y = json.optInt("y", -1);
        boolean buildDome = json.optBoolean("buildDome", false); // For Atlas

        switch (actionType) {
            case "placeWorker":
                if (x == -1 || y == -1) throw new Exception("Invalid coordinates for placement.");
                game.placeWorker(x, y);
                break;

            case "move":
                if (workerIndex == -1 || x == -1 || y == -1) throw new Exception("Invalid move parameters.");
                game.moveWorker(workerIndex, x, y);
                break;

            case "build":
                if (workerIndex == -1 || x == -1 || y == -1) throw new Exception("Invalid build parameters.");
                // Strategies handle build actions internally
                game.build(x, y);
                break;

            case "endTurn":
                game.getCurrentPlayer().getGodStrategy().playerEndsTurn(game);
                break;

            default:
                throw new Exception("Unknown action type.");
        }

        return createGameStateResponse("Action " + actionType + " processed successfully.");
    }

    private Response createGameStateResponse(String message) throws Exception {
        Map<String, Object> state = new HashMap<>();
        if (message != null) state.put("message", message);
        state.put("grid", serializeGrid());
        state.put("workers", serializeWorkers());
        state.put("currentPlayer", game.getCurrentPlayer().getName());
        state.put("gamePhase", game.getCurrentPhase().toString());
        state.put("gameEnded", game.isGameEnded());
        String status = game.isGameEnded() ? game.getWinner() + " Wins!" : "In Progress";
        state.put("status", status);
        state.put("winner", game.getWinner()); // Add this line
        state.put("playerAGod", game.getPlayerA().getGodStrategy().getName());
        state.put("playerBGod", game.getPlayerB().getGodStrategy().getName());

        // Ensure strategyState is never null
        Map<String, Object> strategyState = game.getCurrentPlayer().getGodStrategy().getStrategyState();
        if (strategyState == null) {
            strategyState = new HashMap<>();
        }
        state.put("strategyState", strategyState);

        state.put("currentPlayerGod", game.getCurrentPlayer().getGodStrategy().getName());
        return createJsonResponse(Response.Status.OK, state);
    }

    private List<List<Map<String, Object>>> serializeGrid() throws Exception {
        int boardSize = 5;
        List<List<Map<String, Object>>> serializedGrid = new ArrayList<>();

        for (int x = 0; x < boardSize; x++) {
            List<Map<String, Object>> row = new ArrayList<>();
            for (int y = 0; y < boardSize; y++) {
                Map<String, Object> cell = new HashMap<>();
                cell.put("x", x);
                cell.put("y", y);
                cell.put("height", game.getBoard().getTowerHeight(x, y));

                Worker worker = game.getBoard().getWorkerAt(x, y);
                if (worker != null) {
                    cell.put("worker", Map.of(
                            "id", worker.hashCode(),
                            "player", worker.getOwner().getName(),
                            "x", worker.getX(),
                            "y", worker.getY()
                    ));
                }

                row.add(cell);
            }
            serializedGrid.add(row);
        }

        return serializedGrid;
    }

    private List<Map<String, Object>> serializeWorkers() throws Exception {
        List<Map<String, Object>> workersList = new ArrayList<>();
        for (Worker worker : game.getAllWorkers()) {
            workersList.add(Map.of(
                    "id", worker.hashCode(),
                    "player", worker.getOwner().getName(),
                    "position", Map.of("x", worker.getX(), "y", worker.getY())
            ));
        }
        return workersList;
    }

    private Response createJsonResponse(Response.Status status, Map<String, Object> data) {
        return newFixedLengthResponse(status, "application/json", new JSONObject(data).toString());
    }

    private Response serveStaticFile(String uri) {
        String filePath = uri.equals("/") ? "static/index.html" : uri.substring(1);
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not found");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(filePath), fis, file.length());
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Internal Server Error");
        }
    }

    private Response addCORSHeaders(Response response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return response;
    }
}
