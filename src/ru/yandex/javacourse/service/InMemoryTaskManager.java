package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int nextId = 1;
    public final HistoryManager historyManager = Managers.getDefaultHistory();
    // МЕТОДЫ ДЛЯ ЗАДАЧ

    @Override
    public Task addingTask(Task task) {
        task.setId(nextId);
        tasks.put(nextId, task);
        nextId++;
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task gettingTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task currentTask = tasks.get(id);
            historyManager.add(currentTask);
            return currentTask;
        }
        return null;
    }

    @Override
    public void deleteTaskByID(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // МЕТОДЫ ДЛЯ ЭПИКОВ

    @Override
    public Epic addingEpic(Epic epic) { // добавление
        epic.setId(nextId);
        epics.put(nextId, epic);
        nextId++;
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpic() { // вывод
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic gettingEpicById(int id) { // получение по ID
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            historyManager.add(currentEpic);
            return currentEpic;
        }
        return null;
    }

    @Override
    public void removeEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpicByID(int id) { // удаление по ID
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public ArrayList<Subtask> gettingSubtaskByEpicId(int id) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epics.get(epic.getId()).setStatus(calculateEpicStatus(epic.getId()));
    }

    // МЕТОДЫ ДЛЯ ПОДЗАДАЧ

    @Override
    public Subtask addingSubtask(Subtask subtask) {
        subtask.setId(nextId);
        subtasks.put(nextId, subtask);

        Epic currentEpic = epics.get(subtask.getEpicId());
        if (currentEpic != null) {
            ArrayList<Integer> epicSubtasks = currentEpic.getSubtaskIds();
            epicSubtasks.add(subtask.getId());
            currentEpic.setStatus(calculateEpicStatus(currentEpic.getId()));
        }

        nextId++;
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() { // вывод
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask gettingSubtaskById(int id) { // получение подзадачи по ID
        if (subtasks.containsKey(id)) {
            Subtask currentSubtask = subtasks.get(id);
            historyManager.add(currentSubtask);
            return currentSubtask;
        }
        return null;
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtaskIds().remove(Integer.valueOf(id));
            epic.setStatus(calculateEpicStatus(epicId));
        }
        subtasks.remove(id);
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(calculateEpicStatus(epic.getId()));
        }
        subtasks.clear();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).setStatus(calculateEpicStatus(subtask.getEpicId()));
    }

    private Status calculateEpicStatus(int epicId) {
        int countNew = 0;
        int countDone = 0;
        for (Integer id : epics.get(epicId).getSubtaskIds()) {
            Status currentStatus = subtasks.get(id).getStatus();
            if (currentStatus == Status.IN_PROGRESS) {
                return Status.IN_PROGRESS;
            } else if (currentStatus == Status.NEW) {
                countNew++;
            } else {
                countDone++;
            }
        }

        if (countNew == 0 && countDone > 0) {
            return Status.DONE;
        } else if (countNew > 0 && countDone > 0) {
            return Status.IN_PROGRESS;
        }
        return Status.NEW;
    }
}


