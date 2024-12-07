package org.example;

import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppTest {

    private App app;

    @BeforeEach
    void setUp() throws IOException {
        app = new App();
    }

    @AfterEach
    void tearDown() {
        app.stop();
    }


 


    @Test
    void testHandleGetGameState() throws Exception {
        NanoHTTPD.IHTTPSession session = createSession("GET", "/game-state");
        NanoHTTPD.Response response = app.serve(session);

        assertNotNull(response);
        assertEquals(NanoHTTPD.Response.Status.OK, response.getStatus());

        String responseBody = readResponseBody(response);
        JSONObject jsonResponse = new JSONObject(responseBody);
        // Assuming "In Progress" is the default before any moves:
        assertEquals("In Progress", jsonResponse.getString("status"));
    }


    @Test
    void testInvalidEndpoint() throws Exception {
        NanoHTTPD.IHTTPSession session = createSession("GET", "/invalid-endpoint");
        NanoHTTPD.Response response = app.serve(session);

        assertNotNull(response);
        assertEquals(NanoHTTPD.Response.Status.NOT_FOUND, response.getStatus());

        String responseBody = readResponseBody(response);
        JSONObject jsonResponse = new JSONObject(responseBody);
        assertEquals("Endpoint not found", jsonResponse.getString("error"));
    }

    // Helper methods to create mock sessions
    private NanoHTTPD.IHTTPSession createSession(String method, String uri) {
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        when(session.getMethod()).thenReturn(NanoHTTPD.Method.valueOf(method));
        when(session.getUri()).thenReturn(uri);
        when(session.getParms()).thenReturn(Map.of()); // Default no parameters
        return session;
    }

    private NanoHTTPD.IHTTPSession createPostSession(String uri, String jsonBody) {
        NanoHTTPD.IHTTPSession session = mock(NanoHTTPD.IHTTPSession.class);
        when(session.getMethod()).thenReturn(NanoHTTPD.Method.POST);
        when(session.getUri()).thenReturn(uri);
        try {
            doNothing().when(session).parseBody(Mockito.anyMap());
        } catch (Exception ignored) {
        }
        when(session.getParms()).thenReturn(Map.of("postData", jsonBody));
        return session;
    }

    private String readResponseBody(NanoHTTPD.Response response) throws IOException {
        try (InputStream is = response.getData()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
