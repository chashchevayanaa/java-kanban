package ru.yandex.javacourse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    protected static final String TASK_NAME = "Task";
    protected static final String EPIC_NAME = "Epic";
    protected static final String SUBTASK_NAME = "Subtask";
    protected static final String DESCRIPTION = "Description";

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    @DisplayName("Добавление и поиск задачи")
    void addingTask_ShouldAddAndFindTask_WhenTaskAdded() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Task addedTask = taskManager.addingTask(task);

        assertNotNull(addedTask.getId());
        assertEquals(task.getName(), addedTask.getName());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(Status.NEW, addedTask.getStatus());
    }

    @Test
    @DisplayName("Получение всех задач")
    void getAllTasks_ShouldReturnAllTasks_WhenTasksExist() {
        Task task1 = new Task(TASK_NAME + "1", DESCRIPTION);
        Task task2 = new Task(TASK_NAME + "2", DESCRIPTION);

        taskManager.addingTask(task1);
        taskManager.addingTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Удаление всех задач")
    void removeAllTasks_ShouldClearAllTasks_WhenCalled() {
        Task task1 = new Task(TASK_NAME + "1", DESCRIPTION);
        Task task2 = new Task(TASK_NAME + "2", DESCRIPTION);

        taskManager.addingTask(task1);
        taskManager.addingTask(task2);

        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    @DisplayName("Получение задачи по ID")
    void gettingTaskById_ShouldReturnTask_WhenTaskExists() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Task addedTask = taskManager.addingTask(task);

        Task foundTask = taskManager.gettingTaskById(addedTask.getId());

        assertNotNull(foundTask);
        assertEquals(addedTask.getId(), foundTask.getId());
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID")
    void gettingTaskById_ShouldReturnNull_WhenTaskNotExists() {
        assertNull(taskManager.gettingTaskById(999));
    }

    @Test
    @DisplayName("Удаление задачи по ID")
    void deleteTaskByID_ShouldRemoveTask_WhenTaskExists() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Task addedTask = taskManager.addingTask(task);

        taskManager.deleteTaskByID(addedTask.getId());

        assertNull(taskManager.gettingTaskById(addedTask.getId()));
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    @DisplayName("Обновление задачи")
    void updateTask_ShouldUpdateTask_WhenTaskExists() {
        Task task = new Task(TASK_NAME, DESCRIPTION);
        Task addedTask = taskManager.addingTask(task);

        Task updatedTask = new Task("Updated", "Updated Description");
        updatedTask.setId(addedTask.getId());
        updatedTask.setStatus(Status.IN_PROGRESS);

        taskManager.updateTask(updatedTask);

        Task foundTask = taskManager.gettingTaskById(addedTask.getId());
        assertEquals("Updated", foundTask.getName());
        assertEquals("Updated Description", foundTask.getDescription());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    @DisplayName("Добавление и поиск эпика")
    void addingEpic_ShouldAddAndFindEpic_WhenEpicAdded() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        assertNotNull(addedEpic.getId());
        assertEquals(epic.getName(), addedEpic.getName());
        assertEquals(epic.getDescription(), addedEpic.getDescription());
        assertEquals(Status.NEW, addedEpic.getStatus());
        assertTrue(addedEpic.getSubtaskIds().isEmpty());
    }

    @Test
    @DisplayName("Получение всех эпиков")
    void getAllEpic_ShouldReturnAllEpics_WhenEpicsExist() {
        Epic epic1 = new Epic(EPIC_NAME + "1", DESCRIPTION);
        Epic epic2 = new Epic(EPIC_NAME + "2", DESCRIPTION);

        taskManager.addingEpic(epic1);
        taskManager.addingEpic(epic2);

        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(2, epics.size());
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    void removeEpics_ShouldClearAllEpicsAndSubtasks_WhenCalled() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, addedEpic.getId());
        taskManager.addingSubtask(subtask);

        taskManager.removeEpics();

        assertTrue(taskManager.getAllEpic().isEmpty());
        assertTrue(taskManager.getAllSubtask().isEmpty());
    }

    @Test
    @DisplayName("Получение эпика по ID")
    void gettingEpicById_ShouldReturnEpic_WhenEpicExists() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Epic foundEpic = taskManager.gettingEpicById(addedEpic.getId());

        assertNotNull(foundEpic);
        assertEquals(addedEpic.getId(), foundEpic.getId());
    }

    @Test
    @DisplayName("Удаление эпика по ID")
    void deleteEpicByID_ShouldRemoveEpicAndSubtasks_WhenEpicExists() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, addedEpic.getId());
        Subtask addedSubtask = taskManager.addingSubtask(subtask);

        taskManager.deleteEpicByID(addedEpic.getId());

        assertNull(taskManager.gettingEpicById(addedEpic.getId()));
        assertNull(taskManager.gettingSubtaskById(addedSubtask.getId()));
    }

    @Test
    @DisplayName("Получение подзадач эпика")
    void gettingSubtaskByEpicId_ShouldReturnSubtasks_WhenEpicHasSubtasks() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask1 = new Subtask(SUBTASK_NAME + "1", DESCRIPTION, addedEpic.getId());
        Subtask subtask2 = new Subtask(SUBTASK_NAME + "2", DESCRIPTION, addedEpic.getId());

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        List<Subtask> subtasks = taskManager.gettingSubtaskByEpicId(addedEpic.getId());
        assertEquals(2, subtasks.size());
    }

    @Test
    @DisplayName("Добавление и поиск подзадачи")
    void addingSubtask_ShouldAddAndFindSubtask_WhenSubtaskAdded() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, addedEpic.getId());
        Subtask addedSubtask = taskManager.addingSubtask(subtask);

        assertNotNull(addedSubtask.getId());
        assertEquals(subtask.getName(), addedSubtask.getName());
        assertEquals(subtask.getDescription(), addedSubtask.getDescription());
        assertEquals(Status.NEW, addedSubtask.getStatus());
        assertEquals(addedEpic.getId(), addedSubtask.getEpicId());
    }

    @Test
    @DisplayName("Получение всех подзадач")
    void getAllSubtask_ShouldReturnAllSubtasks_WhenSubtasksExist() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask1 = new Subtask(SUBTASK_NAME + "1", DESCRIPTION, addedEpic.getId());
        Subtask subtask2 = new Subtask(SUBTASK_NAME + "2", DESCRIPTION, addedEpic.getId());

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(2, subtasks.size());
    }

    @Test
    @DisplayName("Удаление подзадачи по ID")
    void deleteSubtaskById_ShouldRemoveSubtask_WhenSubtaskExists() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask = new Subtask(SUBTASK_NAME, DESCRIPTION, addedEpic.getId());
        Subtask addedSubtask = taskManager.addingSubtask(subtask);

        taskManager.deleteSubtaskById(addedSubtask.getId());

        assertNull(taskManager.gettingSubtaskById(addedSubtask.getId()));
        assertTrue(addedEpic.getSubtaskIds().isEmpty());
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    void deleteAllSubtask_ShouldClearAllSubtasks_WhenCalled() {
        Epic epic = new Epic(EPIC_NAME, DESCRIPTION);
        Epic addedEpic = taskManager.addingEpic(epic);

        Subtask subtask1 = new Subtask(SUBTASK_NAME + "1", DESCRIPTION, addedEpic.getId());
        Subtask subtask2 = new Subtask(SUBTASK_NAME + "2", DESCRIPTION, addedEpic.getId());

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        taskManager.deleteAllSubtask();

        assertTrue(taskManager.getAllSubtask().isEmpty());
        assertTrue(addedEpic.getSubtaskIds().isEmpty());
    }

    @Test
    @DisplayName("Статус эпика NEW когда все подзадачи NEW")
    void epicStatus_ShouldBeNew_WhenAllSubtasksNew() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId());

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Статус эпика DONE когда все подзадачи DONE")
    void epicStatus_ShouldBeDone_WhenAllSubtasksDone() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда подзадачи NEW и DONE")
    void epicStatus_ShouldBeInProgress_WhenSubtasksNewAndDone() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId());

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Статус эпика IN_PROGRESS когда есть подзадача IN_PROGRESS")
    void epicStatus_ShouldBeInProgress_WhenAnySubtaskInProgress() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask1 = new Subtask("Sub1", "Desc1", epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", epic.getId());

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.NEW);

        taskManager.addingSubtask(subtask1);
        taskManager.addingSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Статус эпика NEW когда нет подзадач")
    void epicStatus_ShouldBeNew_WhenNoSubtasks() {
        Epic epic = taskManager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));

        assertEquals(Status.NEW, epic.getStatus());
    }

    // ТЕСТЫ ДЛЯ ПРИОРИТЕТНЫХ ЗАДАЧ
    @Test
    @DisplayName("Получение приоритетных задач")
    void getPrioritizedTasks_ShouldReturnSortedTasks_WhenTasksHaveStartTime() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task2", "Desc2", Duration.ofMinutes(45), now.plusHours(1));
        Task task3 = new Task("Task3", "Desc3", Duration.ofMinutes(60), now.plusHours(3));

        taskManager.addingTask(task3);
        taskManager.addingTask(task1);
        taskManager.addingTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getName()); // Самая ранняя
        assertEquals("Task1", prioritized.get(1).getName());
        assertEquals("Task3", prioritized.get(2).getName()); // Самая поздняя
    }

    @Test
    @DisplayName("Приоритетные задачи не включают задачи без startTime")
    void getPrioritizedTasks_ShouldExcludeTasksWithoutStartTime_WhenMixedTasks() {
        Task taskWithTime = new Task("WithTime", "Desc", Duration.ofMinutes(30), LocalDateTime.now());
        Task taskWithoutTime = new Task("WithoutTime", "Desc", null, null);

        taskManager.addingTask(taskWithTime);
        taskManager.addingTask(taskWithoutTime);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(1, prioritized.size());
        assertEquals("WithTime", prioritized.get(0).getName());
    }

    // ТЕСТЫ ДЛЯ ПРОВЕРКИ ПЕРЕСЕЧЕНИЙ
    @Test
    @DisplayName("Задачи не пересекаются по времени")
    void isTaskOverlapping_ShouldReturnFalse_WhenTasksNotOverlapping() {
        LocalDateTime baseTime = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc", Duration.ofMinutes(30), baseTime);
        Task task2 = new Task("Task2", "Desc", Duration.ofMinutes(30), baseTime.plusHours(1));

        taskManager.addingTask(task1);

        assertFalse(taskManager.isTaskOverlapping(task2));
    }

    @Test
    @DisplayName("Задачи пересекаются по времени")
    void isTaskOverlapping_ShouldReturnTrue_WhenTasksOverlapping() {
        LocalDateTime baseTime = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc", Duration.ofMinutes(60), baseTime);
        Task task2 = new Task("Task2", "Desc", Duration.ofMinutes(30), baseTime.plusMinutes(30));

        taskManager.addingTask(task1);

        assertTrue(taskManager.isTaskOverlapping(task2));
    }

    @Test
    @DisplayName("Исключение при добавлении пересекающейся задачи")
    void addingTask_ShouldThrowException_WhenTaskOverlaps() {
        LocalDateTime baseTime = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc", Duration.ofMinutes(60), baseTime);
        Task task2 = new Task("Task2", "Desc", Duration.ofMinutes(30), baseTime.plusMinutes(30));

        taskManager.addingTask(task1);

        assertThrows(IllegalArgumentException.class, () -> taskManager.addingTask(task2));
    }

    @Test
    @DisplayName("Задачи без времени не вызывают пересечений")
    void isTaskOverlapping_ShouldReturnFalse_WhenTaskHasNoTime() {
        Task taskWithTime = new Task("WithTime", "Desc", Duration.ofMinutes(30), LocalDateTime.now());
        Task taskWithoutTime = new Task("WithoutTime", "Desc", null, null);

        taskManager.addingTask(taskWithTime);

        assertFalse(taskManager.isTaskOverlapping(taskWithoutTime));
    }
}