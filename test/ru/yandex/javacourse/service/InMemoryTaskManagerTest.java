package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    @DisplayName("История должна обновляться при получении задач")
    void historyManager_ShouldUpdate_WhenTasksRetrieved() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Task addedTask = taskManager.addingTask(task);

        taskManager.gettingTaskById(addedTask.getId());

        assertEquals(4, taskManager.historyManager.getHistory().size());
    }
}