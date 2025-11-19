package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected static int nextId = 1;
    public final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );


    @Override
    public Task addingTask(Task task) {
        if (task == null) {
            return null;
        }

        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей");
        }

        task.setId(nextId);
        tasks.put(nextId, task);
        addToPrioritizedTasks(task);
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
            prioritizedTasks.remove(tasks.get(id));
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
        prioritizedTasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }

        Task existingTask = tasks.get(task.getId());
        prioritizedTasks.remove(existingTask);

        if (isTaskOverlapping(task)) {
            prioritizedTasks.add(existingTask); // Возвращаем обратно
            throw new IllegalArgumentException("Обновленная задача пересекается по времени с существующей задачей");
        }

        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
    }


    @Override
    public Epic addingEpic(Epic epic) {
        epic.setId(nextId);
        epics.put(nextId, epic);
        nextId++;
        updateEpicFields(epic.getId());
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
            prioritizedTasks.remove(subtasks.get(id));
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpicByID(int id) {
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public ArrayList<Subtask> gettingSubtaskByEpicId(int id) {
        if (!epics.containsKey(id)) {
            return new ArrayList<>();
        }

        return epics.get(id).getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());
        if (existingEpic != null) {
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            updateEpicFields(epic.getId());
        }
    }


    @Override
    public Subtask addingSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        if (isTaskOverlapping(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с существующей задачей");
        }

        subtask.setId(nextId);
        subtasks.put(nextId, subtask);
        addToPrioritizedTasks(subtask);

        Epic currentEpic = epics.get(subtask.getEpicId());
        if (currentEpic != null) {
            ArrayList<Integer> epicSubtasks = currentEpic.getSubtaskIds();
            epicSubtasks.add(subtask.getId());
            updateEpicFields(currentEpic.getId());
        }

        nextId++;
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask gettingSubtaskById(int id) {
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
        if (subtask != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicFields(epicId);
            }
            subtasks.remove(id);
        }
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicFields(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask existingSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(existingSubtask);

        if (isTaskOverlapping(subtask)) {
            prioritizedTasks.add(existingSubtask); // Возвращаем обратно
            throw new IllegalArgumentException("Обновленная подзадача пересекается по времени с существующей задачей");
        }

        subtasks.put(subtask.getId(), subtask);
        addToPrioritizedTasks(subtask);
        updateEpicFields(subtask.getEpicId());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTaskOverlapping(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null && t.getDuration() != null)
                .filter(t -> t.getId() != task.getId())
                .anyMatch(existingTask -> isTimeOverlapping(task, existingTask));
    }

    private boolean isTimeOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void updateEpicFields(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = gettingSubtaskByEpicId(epicId);

        epic.setStatus(calculateEpicStatus(epicId));

        if (epicSubtasks.isEmpty()) {
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
        } else {
            Duration totalDuration = epicSubtasks.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);
            epic.setDuration(totalDuration);

            LocalDateTime earliestStart = epicSubtasks.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            epic.setStartTime(earliestStart);

            LocalDateTime latestEnd = epicSubtasks.stream()
                    .map(Subtask::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            epic.setEndTime(latestEnd);
        }
    }

    private Status calculateEpicStatus(int epicId) {
        List<Subtask> epicSubtasks = gettingSubtaskByEpicId(epicId);

        if (epicSubtasks.isEmpty()) {
            return Status.NEW;
        }

        long countNew = epicSubtasks.stream()
                .filter(subtask -> subtask.getStatus() == Status.NEW)
                .count();

        long countDone = epicSubtasks.stream()
                .filter(subtask -> subtask.getStatus() == Status.DONE)
                .count();

        if (countNew == epicSubtasks.size()) {
            return Status.NEW;
        } else if (countDone == epicSubtasks.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }
}