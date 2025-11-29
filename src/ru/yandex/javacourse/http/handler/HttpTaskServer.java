package ru.yandex.javacourse.http.handler;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static void main(String[] args) {

        // TODO: удалить
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.addingTask(task1);
        taskManager.addingTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addingEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());
        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);
        taskManager.addingSubtask(subtask3);
        //

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/tasks", new HttpTaskHandler());
            server.createContext("/subtasks", new HttpSubtaskHandler());
            server.createContext("/epics", new HttpEpicHandler());
            server.createContext("/", new HttpGeneralHandler());

            server.start();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

}

