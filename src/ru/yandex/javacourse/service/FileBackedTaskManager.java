package ru.yandex.javacourse.service;

import ru.yandex.javacourse.exception.ManagerSaveException;
import ru.yandex.javacourse.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task addingTask(Task task) {
        Task result = super.addingTask(task);
        save();
        return result;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Epic addingEpic(Epic epic) {
        Epic result = super.addingEpic(epic);
        save();
        return result;
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Subtask addingSubtask(Subtask subtask) {
        Subtask result = super.addingSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    private void save() {
        try {
            String header = "id,type,name,status,description,epic\n";
            StringBuilder content = new StringBuilder();
            content.append(header);

            for (Task task : getAllTasks()) {
                content.append(toString(task)).append("\n");
            }
            for (Epic epic : getAllEpic()) {
                content.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getAllSubtask()) {
                content.append(toString(subtask)).append("\n");
            }

            Files.writeString(file.toPath(), content.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String toString(Task task) {
        String type = TaskType.TASK.toString();
        String epic = "";

        if (task instanceof Epic) {
            type = TaskType.EPIC.toString();
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK.toString();
            epic = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epic);
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicId = fields.length > 5 ? fields[5] : "";

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                task = new Subtask(name, description, Integer.parseInt(epicId));
                break;
            default:
                throw new IllegalStateException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        task.setStatus(status);
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            int maxId = 0;
            HashMap<Integer, Task> tasks = new HashMap<>();
            HashMap<Integer, Epic> epics = new HashMap<>();
            HashMap<Integer, Subtask> subtasks = new HashMap<>();

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) continue;

                Task task = manager.fromString(lines[i]);
                int id = task.getId();
                maxId = Math.max(maxId, id);

                if (task instanceof Epic) {
                    epics.put(id, (Epic) task);
                } else if (task instanceof Subtask) {
                    subtasks.put(id, (Subtask) task);
                } else {
                    tasks.put(id, task);
                }
            }

            for (Subtask subtask : subtasks.values()) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtaskIds().add(subtask.getId());
                }
            }

            manager.tasks.putAll(tasks);
            manager.epics.putAll(epics);
            manager.subtasks.putAll(subtasks);
            manager.nextId = maxId + 1;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }
}
