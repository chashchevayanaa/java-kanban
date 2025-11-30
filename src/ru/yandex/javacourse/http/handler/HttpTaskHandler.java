package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.exception.TimeConflictException;
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

        switch (EndpointMapper.mapEndpoint(method, path)) {
            case GET_ALL_TASKS -> getAllTasksHandler(exchange, path);
            case GET_TASK -> getTaskHandler(exchange, path);
            case DELETE_TASK -> deleteTaskHandler(exchange, path);
            case CREATE_TASK -> createTaskHandler(exchange, path);
            case UPDATE_TASK -> updateTaskHandler(exchange, path);
            default -> baseHttpHandler.sendNotFound(exchange, "Неизвестный метод", path);
        }
    }

    private void getAllTasksHandler(HttpExchange exchange, String path) throws IOException {
        try {
            String response = gson.toJson(taskManager.getAllTasks());
            baseHttpHandler.sendText(exchange, response, path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void getTaskHandler(HttpExchange exchange, String path) throws IOException {
        try {
            int taskId = Integer.parseInt(path.replaceAll("\\D+", ""));
            String response = gson.toJson(taskManager.gettingTaskById(taskId));
            if (response.equals("null")) {
                baseHttpHandler.sendNotFound(exchange, "Задача с номером " + taskId + " не найдена", path);
            } else {
                baseHttpHandler.sendText(exchange, response, path);
            }
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void deleteTaskHandler(HttpExchange exchange, String path) throws IOException {
        taskManager.deleteTaskByID(Integer.parseInt(path.replaceAll("\\D+", "")));
        baseHttpHandler.sendText(exchange, "Задача удалена", path);
    }

    private void createTaskHandler(HttpExchange exchange, String path) throws IOException {
        try {
            InputStream bodyInput = exchange.getRequestBody();
            String body = new String(bodyInput.readAllBytes());
            Task newTask = taskManager.addingTask(gson.fromJson(body, Task.class));
            baseHttpHandler.sendCreated(exchange, "Создана задача с ID = " + newTask.getId(), path);
        } catch (TimeConflictException e) {
            baseHttpHandler.sendOverlappingError(exchange, "Задача пересекается с уже существующей", path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void updateTaskHandler(HttpExchange exchange, String path) throws IOException {
        try {
            InputStream bodyInput = exchange.getRequestBody();
            String body = new String(bodyInput.readAllBytes());
            Task updateTask = gson.fromJson(body, Task.class);
            taskManager.updateTask(updateTask);
            baseHttpHandler.sendCreated(exchange, "Обновлена задача с ID = " + updateTask.getId(), path);
        } catch (TimeConflictException e) {
            baseHttpHandler.sendOverlappingError(exchange, "Задача пересекается с уже существующей", path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }
}

