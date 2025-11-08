package ru.yandex.javacourse.service;

import java.io.File;
import java.io.IOException;

public class Managers {

    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    private Managers() {
    }

    public static TaskManager getDefault() {
        if (taskManager == null) {
            try {
                File tempFile = File.createTempFile("tasks", ".csv");
                taskManager = new FileBackedTaskManager(tempFile);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании временного файла", e);
            }
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}