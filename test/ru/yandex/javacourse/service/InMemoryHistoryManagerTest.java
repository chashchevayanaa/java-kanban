package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task("Task1", "Description1");
        task1.setId(1);

        task2 = new Task("Task2", "Description2");
        task2.setId(2);

        task3 = new Task("Task3", "Description3");
        task3.setId(3);
    }

    @Test
    @DisplayName("Пустая история задач")
    void getHistory_ShouldReturnEmptyList_WhenNoTasksAdded() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Дублирование задач в истории")
    void add_ShouldRemoveDuplicates_WhenTaskAddedMultipleTimes() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    @DisplayName("Удаление из начала истории")
    void remove_ShouldRemoveFromBeginning_WhenFirstTaskRemoved() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
        assertFalse(history.contains(task1));
    }

    @Test
    @DisplayName("Удаление из середины истории")
    void remove_ShouldRemoveFromMiddle_WhenMiddleTaskRemoved() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
        assertFalse(history.contains(task2));
    }

    @Test
    @DisplayName("Удаление из конца истории")
    void remove_ShouldRemoveFromEnd_WhenLastTaskRemoved() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertFalse(history.contains(task3));
    }

    @Test
    @DisplayName("Порядок истории при добавлении задач")
    void getHistory_ShouldMaintainOrder_WhenTasksAdded() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    @DisplayName("Добавление null задачи")
    void add_ShouldNotAdd_WhenTaskIsNull() {
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Удаление несуществующей задачи")
    void remove_ShouldNotFail_WhenTaskNotInHistory() {
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
}
