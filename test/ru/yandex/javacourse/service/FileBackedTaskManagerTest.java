package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("test_tasks", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания временного файла", e);
        }
    }

    @Test
    @DisplayName("Сохранение и загрузка пустого менеджера")
    void saveAndLoad_ShouldWork_WhenManagerEmpty() throws IOException {
        FileBackedTaskManager manager = createTaskManager();
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpic().isEmpty());
        assertTrue(loadedManager.getAllSubtask().isEmpty());
    }

    @Test
    @DisplayName("Сохранение и загрузка задач с временными параметрами")
    void saveAndLoad_ShouldPreserveTimeFields_WhenTasksWithTime() throws IOException {
        FileBackedTaskManager manager = createTaskManager();

        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(45);

        Task task = new Task("Task", "Desc", duration, startTime);
        manager.addingTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getAllTasks().get(0);

        assertEquals(duration, loadedTask.getDuration());
        assertEquals(startTime, loadedTask.getStartTime());
        assertEquals(startTime.plus(duration), loadedTask.getEndTime());
    }

    @Test
    @DisplayName("Сохранение и загрузка эпика с подзадачами")
    void saveAndLoad_ShouldPreserveEpicSubtasksRelationship_WhenEpicWithSubtasks() throws IOException {
        FileBackedTaskManager manager = createTaskManager();

        Epic epic = manager.addingEpic(new Epic(EPIC_NAME, DESCRIPTION));
        Subtask subtask = manager.addingSubtask(new Subtask(SUBTASK_NAME, DESCRIPTION, epic.getId()));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Epic loadedEpic = loadedManager.getAllEpic().get(0);
        Subtask loadedSubtask = loadedManager.getAllSubtask().get(0);

        assertEquals(1, loadedEpic.getSubtaskIds().size());
        assertEquals(loadedEpic.getId(), loadedSubtask.getEpicId());
        assertTrue(loadedEpic.getSubtaskIds().contains(loadedSubtask.getId()));
    }

    @Test
    @DisplayName("Сохранение должно создавать файл с корректным содержимым")
    void save_ShouldCreateFileWithCorrectContent_WhenTasksExist() throws IOException {
        FileBackedTaskManager manager = createTaskManager();

        Task task = new Task(TASK_NAME, DESCRIPTION);
        manager.addingTask(task);

        String content = Files.readString(tempFile.toPath());

        assertTrue(content.contains("id,type,name,status,description,epic,duration,startTime,endTime"));
        assertTrue(content.contains(TASK_NAME));
        assertTrue(content.contains("TASK"));
    }
}