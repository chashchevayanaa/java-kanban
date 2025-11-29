package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.http.adapter.DurationAdapter;
import ru.yandex.javacourse.http.adapter.LocalDateTimeAdapter;
import ru.yandex.javacourse.http.mapper.EndpointMapper;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;

class HttpTaskHandler implements HttpHandler {

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

            case GET_ALL_TASKS -> {
                try {
                    response = gson.toJson(taskManager.getAllTasks());
                    baseHttpHandler.sendText(exchange, response, path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case GET_TASK -> {
                try {
                    int taskId = Integer.parseInt(path.replaceAll("\\D+", ""));
                    response = gson.toJson(taskManager.gettingTaskById(taskId));
                    if (response.equals("null")) {
                        baseHttpHandler.sendNotFound(exchange, "Задача с номером " + taskId + " не найдена", path);
                    } else {
                        baseHttpHandler.sendText(exchange, response, path);
                    }
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case DELETE_TASK -> {
                taskManager.deleteTaskByID(Integer.parseInt(path.replaceAll("\\D+", "")));
                baseHttpHandler.sendText(exchange, "Задача удалена", path);
            }
            case CREATE_TASK -> {
                try {
                    InputStream bodyInput = exchange.getRequestBody();
                    String body = new String(bodyInput.readAllBytes());
                    Task newTask = taskManager.addingTask(gson.fromJson(body, Task.class));
                    baseHttpHandler.sendText(exchange, "Создана задача с ID = " + newTask.getId(), path);
                } catch (IllegalArgumentException e) {
                    baseHttpHandler.sendOverlappingError(exchange, "Задача пересекается с уже существующей", path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            case UPDATE_TASK -> {
                try {
                    InputStream bodyInput = exchange.getRequestBody();
                    String body = new String(bodyInput.readAllBytes());
                    Task updateTask = gson.fromJson(body, Task.class);
                    taskManager.updateTask(updateTask);
                    baseHttpHandler.sendText(exchange, "Обновлена задача с ID = " + updateTask.getId(), path);
                } catch (IllegalArgumentException e) {
                    baseHttpHandler.sendOverlappingError(exchange, "Задача пересекается с уже существующей", path);
                } catch (Exception e) {
                    baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
                }
            }
            default -> baseHttpHandler.sendNotFound(exchange, "Неизвестный метод", path);
        }
    }
}
