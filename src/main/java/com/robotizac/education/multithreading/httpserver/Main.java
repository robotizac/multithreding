package com.robotizac.education.multithreading.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    private static final int THREAD_NUM = 4;
    private static final String SRC_FILE = "./target/classes/war_and_peace.txt";

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Path.of(SRC_FILE)));
        startServer(text);

    }

    private static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(THREAD_NUM);
        server.setExecutor(executor);
        server.start();
    }

    private record WordCountHandler(String text) implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] split = query.split("=");
            String action = split[0];
            String word = split[1];

            if (!"find".equals(action)) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);
            byte[] bytes = Long.toString(count).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(bytes);
            }

        }

        private long countWord(String word) {
            return StringUtils.countMatches(text, word);
        }
    }
}
