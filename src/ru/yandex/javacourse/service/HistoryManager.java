package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}
