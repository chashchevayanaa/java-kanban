package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() == 11) {
            history.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
