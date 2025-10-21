package ru.yandex.javacourse;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addingTask(task1);
        manager.addingTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.addingEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());
        manager.addingSubtask(subtask1);
        manager.addingSubtask(subtask2);
        manager.addingSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.addingEpic(epic2);

        manager.gettingTaskById(task1.getId());
        manager.gettingTaskById(task2.getId());
        manager.gettingEpicById(epic1.getId());
        manager.gettingEpicById(epic2.getId());
        manager.gettingSubtaskById(subtask1.getId());
        manager.gettingSubtaskById(subtask2.getId());
        System.out.println(historyManager.getHistory());

        manager.gettingTaskById(task2.getId());
        manager.gettingTaskById(task1.getId());
        System.out.println(historyManager.getHistory());

        manager.deleteTaskByID(task1.getId());
        System.out.println(historyManager.getHistory());

        manager.deleteSubtaskById(subtask1.getId());
        System.out.println(historyManager.getHistory());

        manager.deleteEpicByID(epic1.getId());
        System.out.println(historyManager.getHistory());
    }
}

