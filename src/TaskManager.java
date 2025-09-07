import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int nextId = 1;

    // МЕТОДЫ ДЛЯ ЗАДАЧ

    public Task addingTask(Task task) {
        task.setId(nextId);
        tasks.put(nextId, task);
        nextId++;
        return task;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    Task gettingTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public void deletionByID(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // МЕТОДЫ ДЛЯ ЭПИКОВ

    public Epic addingEpic(Epic epic) { // добавление
        epic.setId(nextId);
        epics.put(nextId, epic);
        nextId++;
        return epic;
    }

    public ArrayList<Epic> getAllEpic() { // вывод
        return new ArrayList<>(epics.values());
    }

    public Epic gettingEpicById(int id) { // получение по ID
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public void removeEpics() { // удаление
        epics.clear();
        subtasks.clear();
    }

    public void removeEpicByID(int id) { // удаление по ID
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    ArrayList<Subtask> gettingSubtaskByEpicId(int id) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
        }
        return epicSubtasks;
    }

    public void updateEpic(Epic epic) {
        epic.setStatus(epics.get(epic.getId()).getStatus());
        epics.put(epic.getId(), epic);
    }

    // МЕТОДЫ ДЛЯ ПОДЗАДАЧ

    public Subtask addingSubtask(Subtask subtask) { // добавление
        subtask.setId(nextId);
        subtasks.put(nextId, subtask);
        Epic currentEpic = gettingEpicById(subtask.getEpicId());
        ArrayList<Integer> epicSubtasks = currentEpic.getSubtaskIds();
        epicSubtasks.add(subtask.getId());
        nextId++;
        return subtask;
    }

    public ArrayList<Subtask> getAllSubtask() { // вывод
        return new ArrayList<>(subtasks.values());
    }

    public Subtask gettingSubtaskById(int id) { // получение подзадачи по ID
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public void deleteSubtaskById(int id) {
        epics.get(subtasks.get(id).getEpicId()).getSubtaskIds().remove(id);
        subtasks.remove(id);
    }

    public void deleteAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
        subtasks.clear();
    }

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
        if (countNew == 0) {
            return Status.DONE;
        } else if (countNew > 0 && countDone > 0) {
            return Status.IN_PROGRESS;
        }
        return Status.NEW;
    }
}
