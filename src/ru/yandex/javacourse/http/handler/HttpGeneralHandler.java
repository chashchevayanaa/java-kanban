package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.http.adapter.DurationAdapter;
import ru.yandex.javacourse.http.adapter.LocalDateTimeAdapter;
import ru.yandex.javacourse.http.mapper.EndpointMapper;
import ru.yandex.javacourse.service.HistoryManager;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpGeneralHandler implements HttpHandler {
    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final TaskManager taskManager = Managers.getDefault();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static final BaseHttpHandler baseHttpHandler = BaseHttpHandler.getBaseHandler();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String response = "";

        switch (EndpointMapper.mapEndpoint(method, path)) {
            case GET_HISTORY -> {
                try {
                    response = gson.toJson(historyManager.getHistory());
                    baseHttpHandler.sendText(exchange, response, path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case GET_PRIORITIZED_TASKS -> {
                try {
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    baseHttpHandler.sendText(exchange, response, path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            default -> baseHttpHandler.sendNotFound(exchange, "Неизвестный метод", path);
        }
    }
}
