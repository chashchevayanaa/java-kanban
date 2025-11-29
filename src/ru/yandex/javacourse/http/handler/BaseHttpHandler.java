package ru.yandex.javacourse.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    private static BaseHttpHandler handler;

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    public void sendText(HttpExchange exchange, String response, String path) throws IOException {

        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println("\t" + path + " 200 success");
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange, String response, String path) throws IOException {

        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println("\t" + path + " 404 error");
        exchange.close();
    }

    public void sendServerError(HttpExchange exchange, String response, String path) throws IOException {

        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(500, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println("\t" + path + " 500 error");
        exchange.close();
    }

    public void sendOverlappingError(HttpExchange exchange, String response, String path) throws IOException {

        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, resp.length);
        exchange.getResponseBody().write(resp);
        System.out.println("\t" + path + " 406 error");
        exchange.close();
    }

    public static BaseHttpHandler getBaseHandler() {
        if (handler == null) {
            handler = new BaseHttpHandler();
        }
        return handler;
    }
}
