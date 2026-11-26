import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                respond(exchange, 405, "Method not allowed");
                return;
            }

            Path page = Path.of("index.html");
            if (!Files.isRegularFile(page)) {
                respond(exchange, 500, "Could not find index.html in the working directory.");
                return;
            }

            byte[] content = Files.readAllBytes(page);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, content.length);
            try (var body = exchange.getResponseBody()) {
                body.write(content);
            }
        });

        server.createContext("/feedback", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                respond(exchange, 405, "Method not allowed");
                return;
            }

            exchange.getRequestBody().readAllBytes();
            respond(exchange, 200, "Thank you! Your feedback was received.");
        });

        server.start();
        System.out.println("Feedback app is running at http://localhost:" + port);
    }

    private static void respond(HttpExchange exchange, int status, String message) throws IOException {
        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, content.length);
        try (var body = exchange.getResponseBody()) {
            body.write(content);
        }
    }
}