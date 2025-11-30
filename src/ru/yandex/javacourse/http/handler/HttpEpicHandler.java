package ru.yandex.javacourse.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.http.adapter.DurationAdapter;
import ru.yandex.javacourse.http.adapter.LocalDateTimeAdapter;
import ru.yandex.javacourse.http.mapper.EndpointMapper;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpEpicHandler implements HttpHandler {

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

            case GET_ALL_EPICS -> getAllEpicsHandler(exchange, path);
            case GET_EPIC -> getEpicHandler(exchange, path);
            case DELETE_EPIC -> deleteEpicHandler(exchange, path);
            case CREATE_EPIC -> createEpicHandler(exchange, path);
            case UPDATE_EPIC -> updateEpicHandler(exchange, path);
            case GET_EPIC_SUBTASKS -> getEpicSubtasksHandler(exchange, path);
            default -> baseHttpHandler.sendNotFound(exchange, "Неизвестный метод", path);
        }
    }

    private void getAllEpicsHandler(HttpExchange exchange, String path) throws IOException {
        try {
            String response = gson.toJson(taskManager.getAllEpic());
            baseHttpHandler.sendText(exchange, response, path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void getEpicHandler(HttpExchange exchange, String path) throws IOException {
        try {
            int epicId = Integer.parseInt(path.replaceAll("\\D+", ""));
            String response = gson.toJson(taskManager.gettingEpicById(epicId));
            if (response.equals("null")) {
                baseHttpHandler.sendNotFound(exchange, "Эпик с номером " + epicId + " не найден", path);
            } else {
                baseHttpHandler.sendText(exchange, response, path);
            }
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void deleteEpicHandler(HttpExchange exchange, String path) throws IOException {
        taskManager.deleteEpicByID(Integer.parseInt(path.replaceAll("\\D+", "")));
        baseHttpHandler.sendText(exchange, "Эпик удален", path);
    }

    private void createEpicHandler(HttpExchange exchange, String path) throws IOException {
        try {
            InputStream bodyInput = exchange.getRequestBody();
            String body = new String(bodyInput.readAllBytes());
            Epic newEpic = taskManager.addingEpic(gson.fromJson(body, Epic.class));
            baseHttpHandler.sendCreated(exchange, "Создана задача с ID = " + newEpic.getId(), path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void updateEpicHandler(HttpExchange exchange, String path) throws IOException {
        try {
            InputStream bodyInput = exchange.getRequestBody();
            String body = new String(bodyInput.readAllBytes());
            Epic updateEpic = gson.fromJson(body, Epic.class);
            taskManager.updateEpic(updateEpic);
            baseHttpHandler.sendCreated(exchange, "Обновлен эпик с ID = " + updateEpic.getId(), path);
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }

    private void getEpicSubtasksHandler(HttpExchange exchange, String path) throws IOException {
        try {
            int epicId = Integer.parseInt(path.replaceAll("\\D+", ""));
            String response = gson.toJson(taskManager.gettingSubtaskByEpicId(epicId));
            if (response.equals("null")) {
                baseHttpHandler.sendNotFound(exchange, "Подзадачи по эпику с номером " + epicId + " не найдены", path);
            } else {
                baseHttpHandler.sendText(exchange, response, path);
            }
        } catch (Exception e) {
            baseHttpHandler.sendServerError(exchange, e.getMessage(), path);
        }
    }
}