package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task);
        int maxSize = 11;
        if (history.size() == maxSize) {
            history.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
