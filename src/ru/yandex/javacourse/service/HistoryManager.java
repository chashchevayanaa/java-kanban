package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
