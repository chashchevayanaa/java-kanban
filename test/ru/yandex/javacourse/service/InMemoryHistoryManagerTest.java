package ru.yandex.javacourse.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Task;

import static org.junit.Assert.assertEquals;

public class InMemoryHistoryManagerTest {
    private static final String TASK_NAME = "Task";
    private static final String TASK_DESCRIPTION = "Description";
    private static final int TASK_ID = 1;
    private static final int HISTORY_LIMIT = 10;
    private static final int EXCESS_TASKS_COUNT = 15;

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    @DisplayName("Добавление задачи в историю должно корректно работать")
    public void add_ShouldAddTaskToHistory_WhenTaskAdded() {
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION);

        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        task.setStatus(Status.IN_PROGRESS);
        historyManager.add(task);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.getHistory().clear();
    }

    @Test
    @DisplayName("История не должна превышать лимит в 10 задач")
    public void add_ShouldNotExceedLimit_WhenManyTasksAdded() {

        for (int i = TASK_ID; i <= EXCESS_TASKS_COUNT; i++) {
            Task task = new Task(TASK_NAME + i, TASK_DESCRIPTION);
            task.setId(i);
            historyManager.add(task);
        }

        assertEquals(HISTORY_LIMIT, historyManager.getHistory().size());
        historyManager.getHistory().clear();
    }
}

