package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.javacourse.model.*;

class InMemoryTaskManagerTest {
    private static final String TASK_NAME = "Task";
    private static final String EPIC_NAME = "Epic";
    private static final String SUBTASK_NAME = "Subtask";
    private static final String DESCRIPTION = "Description";
    private static final String ORIGINAL_NAME = "Original";
    private static final String ORIGINAL_DESCRIPTION = "Original Description";
    private static final String SUBTASK_DESC_1 = "Desc 1";
    private static final String SUBTASK_NAME_1 = "Sub 1";

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    @DisplayName("Менеджер должен добавлять и находить задачи разных типов")
    void addingTask_ShouldAddAndFindDifferentTaskTypes_WhenTasksAreAdded() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, 2);

        Task addedTask = taskManager.addingTask(task);
        Epic addedEpic = taskManager.addingEpic(epic);
        subtask.setEpicId(addedEpic.getId());
        Subtask addedSubtask = taskManager.addingSubtask(subtask);

        assertNotNull(taskManager.gettingTaskById(addedTask.getId()));
        assertNotNull(taskManager.gettingEpicById(addedEpic.getId()));
        assertNotNull(taskManager.gettingSubtaskById(addedSubtask.getId()));
    }

    @Test
    @DisplayName("Задача не должна изменяться при добавлении в менеджер")
    void addingTask_ShouldNotChangeTask_WhenTaskAdded() {
        Task originalTask = new Task(ORIGINAL_NAME, ORIGINAL_DESCRIPTION);
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
    @DisplayName("Подзадача должна добавляться к эпику")
    void addingSubtask_ShouldAddSubtaskToEpic_WhenEpicExists() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        taskManager.addingEpic(epic);
        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, epic.getId());

        taskManager.addingSubtask(subtask);

        assertEquals(1, taskManager.gettingSubtaskByEpicId(epic.getId()).size());
    }

    @Test
    @DisplayName("Статус эпика должен рассчитываться корректно когда все подзадачи новые")
    void updateEpicStatus_ShouldBeNew_WhenAllSubtasksNew() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask1 = new Subtask(SUBTASK_NAME_1, SUBTASK_DESC_1, epic.getId());
        subtask1.setStatus(Status.NEW);

        taskManager.addingSubtask(subtask1);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Удаление эпика должно удалять его подзадачи")
    void  removeEpicByID_ShouldDeleteEpicAndSubtasks_WhenEpicDeleted() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, epic.getId());
        taskManager.addingSubtask(subtask);
        assertEquals(1, taskManager.getAllSubtask().size());

        taskManager.removeEpicByID(epic.getId());

        assertEquals(0, taskManager.getAllSubtask().size());
        assertNull(taskManager.gettingSubtaskById(subtask.getId()));
    }
}