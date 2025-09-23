package ru.yandex.javacourse.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Task;

import static org.junit.Assert.assertEquals;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void addingTasksToHistory() {
        Task task = new Task("Task", "Description");
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        task.setStatus(Status.IN_PROGRESS);
        historyManager.add(task);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.getHistory().clear();
    }

    @Test
    public void historyShouldNotExceedLimit() {

        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Description");
            task.setId(i);
            historyManager.add(task);
        }

        assertEquals(10, historyManager.getHistory().size());
        historyManager.getHistory().clear();
    }
}

