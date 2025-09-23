package ru.yandex.javacourse.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import ru.yandex.javacourse.model.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void taskManagerShouldAddAndFindDifferentTaskTypes() {
        Task task = new Task("Task", "Description");
        Epic epic = new Epic("Epic", "Description");
        Subtask subtask = new Subtask("Subtask", "Description", 2);

        Task addedTask = taskManager.addingTask(task);
        Epic addedEpic = taskManager.addingEpic(epic);
        subtask.setEpicId(addedEpic.getId());
        Subtask addedSubtask = taskManager.addingSubtask(subtask);

        assertNotNull(taskManager.gettingTaskById(addedTask.getId()));
        assertNotNull(taskManager.gettingEpicById(addedEpic.getId()));
        assertNotNull(taskManager.gettingSubtaskById(addedSubtask.getId()));
    }

    @Test
    void taskShouldRemainUnchangedWhenAddedToManager() {
        Task originalTask = new Task("Original", "Original Description");
        originalTask.setStatus(Status.IN_PROGRESS);

        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        Task addedTask = taskManager.addingTask(originalTask);

        assertEquals(originalName, addedTask.getName());
        assertEquals(originalDescription, addedTask.getDescription());
        assertEquals(originalStatus, addedTask.getStatus());
    }

    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addingEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        taskManager.addingSubtask(subtask);

        assertEquals(1, taskManager.gettingSubtaskByEpicId(epic.getId()).size());
    }

    @Test
    void epicStatusShouldBeCalculatedCorrectly() {
        Epic epic = taskManager.addingEpic(new Epic("Epic", "Description"));

        Subtask subtask1 = new Subtask("Sub 1", "Desc 1", epic.getId());
        subtask1.setStatus(Status.NEW);
        taskManager.addingSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void deletingEpicShouldDeleteItsSubtasks() {
        Epic epic = taskManager.addingEpic(new Epic("Epic", "Description"));
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        taskManager.addingSubtask(subtask);

        assertEquals(1, taskManager.getAllSubtask().size());

        taskManager.removeEpicByID(epic.getId());

        assertEquals(0, taskManager.getAllSubtask().size());
        assertNull(taskManager.gettingSubtaskById(subtask.getId()));
    }
}