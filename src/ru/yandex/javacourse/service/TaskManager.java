package ru.yandex.javacourse.service;

import ru.yandex.javacourse.model.*;

import java.util.*;

public interface TaskManager {

    Task addingTask(Task task);

    ArrayList<Task> getAllTasks();

    void removeAllTasks();

    Task gettingTaskById(int id);

    void deletionByID(int id);

    void updateTask(Task task);

    Epic addingEpic(Epic epic);

    ArrayList<Epic> getAllEpic();

    Epic gettingEpicById(int id);

    void removeEpics();

    void removeEpicByID(int id);

    ArrayList<Subtask> gettingSubtaskByEpicId(int id);

    void updateEpic(Epic epic);

    Subtask addingSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtask();

    Subtask gettingSubtaskById(int id);

    void deleteSubtaskById(int id);

    void deleteAllSubtask();

    void updateSubtask(Subtask subtask);

}


