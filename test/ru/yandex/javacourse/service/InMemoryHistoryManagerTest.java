package ru.yandex.javacourse.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Task;

import static org.junit.Assert.assertEquals;

public class InMemoryHistoryManagerTest {

    private static final String TASK_NAME = "Task";
    private static final String TASK_DESCRIPTION = "Description";
    private static final int TASK_ID_1 = 1;
    private static final int TASK_ID_2 = 2;
    private static final int TASK_ID_3 = 3;

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    @DisplayName("Добавление задачи в историю должно корректно работать")
    public void add_ShouldAddTaskToHistory_WhenTaskAdded() {
        Task task = new Task(TASK_NAME, TASK_DESCRIPTION);
        task.setId(TASK_ID_1);
        Task task2 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task2.setId(TASK_ID_2);


        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        task.setStatus(Status.IN_PROGRESS);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.getHistory().clear();
    }

    @Test
    @DisplayName("Удаление дубликатов из истории должно работать корректно")
    public void add_ShouldRemoveDuplicates_WhenTaskAddedMultipleTimes() {
        Task task1 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task1.setId(TASK_ID_1);
        Task task2 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task2.setId(TASK_ID_2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Порядок задач в истории должен сохраняться правильно")
    public void add_ShouldKeepTaskOrder_WhenTaskAdded() {
        Task task1 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task1.setId(TASK_ID_1);
        Task task2 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task2.setId(TASK_ID_2);
        Task task3 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task3.setId(TASK_ID_3);

        historyManager.add(task1);
        historyManager.add(task3);
        historyManager.add(task2);

        assertEquals(TASK_ID_1, historyManager.getHistory().get(0).getId());
        assertEquals(TASK_ID_3, historyManager.getHistory().get(1).getId());
        assertEquals(TASK_ID_2, historyManager.getHistory().get(2).getId());

        historyManager.add(task1);
        assertEquals(TASK_ID_1, historyManager.getHistory().get(2).getId());
    }

    @Test
    @DisplayName("Удаление задачи из истории должно работать корректно")
    public void remove_ShouldWorkCorrectly_WhenRemoveTask() {
        Task task1 = new Task(TASK_NAME, TASK_DESCRIPTION);
        task1.setId(TASK_ID_1);
        historyManager.add(task1);
        historyManager.remove(task1.getId());

        assertEquals(0, historyManager.getHistory().size());
    }
}

