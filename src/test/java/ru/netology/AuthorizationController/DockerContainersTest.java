package ru.netology.AuthorizationController;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DockerContainersTest {

    private static GenericContainer<?> devApp;
    private static GenericContainer<?> prodApp;

    @LocalServerPort
    private int localServerPort;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeAll
    static void setUp() {
        devApp = new GenericContainer<>("devapp:latest")
                .withExposedPorts(8080);

        prodApp = new GenericContainer<>("prodapp:latest")
                .withExposedPorts(8081);

        devApp.start();
        prodApp.start();

        System.out.println("DEV контейнер запущен на порту: " + devApp.getMappedPort(8080));
        System.out.println("PROD контейнер запущен на порту: " + prodApp.getMappedPort(8081));
    }

    @AfterAll
    static void tearDown() {
        if (devApp != null && devApp.isRunning()) {
            devApp.stop();
        }
        if (prodApp != null && prodApp.isRunning()) {
            prodApp.stop();
        }
    }

    @Test
    void testDevAppWithCorrectCredentials() {
        String url = "http://localhost:" + devApp.getMappedPort(8080) +
                "/authorize?user=sergey&password=123pass";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("DEV App Response: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("READ"));
        assertTrue(response.getBody().contains("WRITE"));
    }

    @Test
    void testDevAppWithInvalidPassword() {
        String url = "http://localhost:" + devApp.getMappedPort(8080) +
                "/authorize?user=sergey&password=wrong";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("DEV App Error Response: " + response.getBody());

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid password") ||
                response.getBody().contains("Unknown user"));
    }

    @Test
    void testProdAppWithCorrectCredentials() {
        String url = "http://localhost:" + prodApp.getMappedPort(8081) +
                "/authorize?user=alex&password=1pass1";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("PROD App Response: " + response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("READ"));
        assertTrue(response.getBody().contains("WRITE"));
        assertTrue(response.getBody().contains("DELETE"));
    }

    @Test
    void testProdAppWithEmptyCredentials() {
        String url = "http://localhost:" + prodApp.getMappedPort(8081) +
                "/authorize?user=sergey&password=";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("PROD App Error Response: " + response.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("empty"));
    }

    @Test
    void testDevAppHealthCheck() {
        String url = "http://localhost:" + devApp.getMappedPort(8080) +
                "/authorize?user=sergey&password=123pass";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void testProdAppHealthCheck() {
        String url = "http://localhost:" + prodApp.getMappedPort(8081) +
                "/authorize?user=alex&password=1pass1";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}