package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.http.adapter.DurationAdapter;
import ru.yandex.javacourse.http.adapter.LocalDateTimeAdapter;
import ru.yandex.javacourse.http.mapper.EndpointMapper;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpSubtaskHandler implements HttpHandler {
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

            case GET_ALL_SUBTASKS -> {
                try {
                    response = gson.toJson(taskManager.getAllSubtask());
                    baseHttpHandler.sendText(exchange, response, path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case GET_SUBTASK ->  {
                try {
                    int subtaskId = Integer.parseInt(path.replaceAll("\\D+", ""));
                    response = gson.toJson(taskManager.gettingSubtaskById(subtaskId));
                    if (response.equals("null")) {
                        baseHttpHandler.sendNotFound(exchange, "Задача с номером " + subtaskId + " не найдена", path);
                    } else {
                        baseHttpHandler.sendText(exchange, response, path);
                    }
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case DELETE_SUBTASK ->  {
                taskManager.deleteSubtaskById(Integer.parseInt(path.replaceAll("\\D+", "")));
                baseHttpHandler.sendText(exchange, "Подзадача удалена", path);
            }
            case CREATE_SUBTASK -> {
                try {
                    InputStream bodyInput = exchange.getRequestBody();
                    String body = new String(bodyInput.readAllBytes());
                    Subtask newSubtask = taskManager.addingSubtask(gson.fromJson(body, Subtask.class));
                    baseHttpHandler.sendText(exchange, "Создана подзадача с ID = " + newSubtask.getId(), path);
                } catch (IllegalArgumentException e) {
                    baseHttpHandler.sendOverlappingError(exchange, "Подзадача пересекается с уже существующей", path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case UPDATE_SUBTASK ->  {
                try {
                    InputStream bodyInput = exchange.getRequestBody();
                    String body = new String(bodyInput.readAllBytes());
                    Subtask updateSubtask = gson.fromJson(body, Subtask.class);
                    taskManager.updateSubtask(updateSubtask);
                    baseHttpHandler.sendText(exchange, "Обновлена подзадача с ID = " + updateSubtask.getId(), path);
                } catch (IllegalArgumentException e) {
                    baseHttpHandler.sendOverlappingError(exchange, "Подзадача пересекается с уже существующей", path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            default -> baseHttpHandler.sendNotFound(exchange, "Неизвестный метод", path);
        }
    }
}

