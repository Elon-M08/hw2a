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
                return addCORSHeaders(serveStaticFile(uri));
            } else if (method == Method.POST && uri.equals("/start-game")) {
                return addCORSHeaders(handleStartGame());
            } else if (method == Method.GET && uri.equals("/game-state")) {
                return addCORSHeaders(handleGetGameState());
            } else if (method == Method.POST && uri.equals("/action")) {
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

    private Response handleStartGame() {
        this.game = new Game(); // Reset the game
        return createGameStateResponse("Game restarted");
    }

    private Response handleGetGameState() {
        return createGameStateResponse(null);
    }

    private Response handleAction(IHTTPSession session) {
        Map<String, String> postData = new HashMap<>();
        try {
            session.parseBody(postData);
        } catch (ResponseException | IOException e) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", "Error parsing request body: " + e.getMessage()));
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
                    if (x == -1 || y == -1) throw new IllegalArgumentException("Invalid coordinates for placement.");
                    game.placeWorker(x, y);
                    break;

                case "move":
                    if (workerIndex == -1 || x == -1 || y == -1) throw new IllegalArgumentException("Invalid move parameters.");
                    game.moveWorker(workerIndex, x, y);
                    break;

                case "build":
                    if (x == -1 || y == -1) throw new IllegalArgumentException("Invalid coordinates for building.");
                    game.build(x, y);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action type.");
            }

            return createGameStateResponse("Action processed.");

        } catch (Exception e) {
            return createJsonResponse(Response.Status.BAD_REQUEST, Map.of("error", e.getMessage()));
        }
    }

    private Response createGameStateResponse(String message) {
        Map<String, Object> state = new HashMap<>();
        if (message != null) state.put("message", message);
        state.put("grid", serializeGrid());
        state.put("workers", serializeWorkers());
        state.put("currentPlayer", game.getCurrentPlayer().getName());
        state.put("gamePhase", game.getCurrentPhase().toString());
        state.put("gameEnded", game.isGameEnded());
        String status = game.isGameEnded() ? game.getCurrentPlayer().getName() + " Wins!" : "In Progress";
        state.put("status", status);
        return createJsonResponse(Response.Status.OK, state);
    }

    private List<List<Map<String, Object>>> serializeGrid() {
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

    private List<Map<String, Object>> serializeWorkers() {
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
