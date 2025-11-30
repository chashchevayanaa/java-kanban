package ru.yandex.javacourse;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.model.Task;
import ru.yandex.javacourse.service.FileBackedTaskManager;
import ru.yandex.javacourse.service.HistoryManager;
import ru.yandex.javacourse.service.Managers;
import ru.yandex.javacourse.service.TaskManager;

import java.io.File;

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

        File file = new File("tasks.csv");
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Task newTask1 = new Task("Новая Задача 1", "Новое описание задачи 1");
        Task newTask2 = new Task("Новая Задача 2", "Новое описание задачи 2");
        manager1.addingTask(newTask1);
        manager1.addingTask(newTask2);

        Epic newEpic1 = new Epic("Новый Эпик 1", "Новое описание эпика 1");
        manager1.addingEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Новая Подзадача 1", "Новое описание подзадачи 1", newEpic1.getId());
        Subtask newSubtask2 = new Subtask("Новая Подзадача 2", "Новое описание подзадачи 2", newEpic1.getId());
        manager1.addingSubtask(newSubtask1);
        manager1.addingSubtask(newSubtask2);

        newTask2.setStatus(Status.DONE);
        manager1.updateTask(newTask2);

        newSubtask1.setStatus(Status.DONE);
        manager1.updateSubtask(newSubtask1);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Задачи восстановлены: " + (manager2.getAllTasks().size() == 2));
        System.out.println("Эпики восстановлены: " + (manager2.getAllEpic().size() == 1));
        System.out.println("Подзадачи восстановлены: " + (manager2.getAllSubtask().size() == 2));

        Epic restoredEpic = manager2.getAllEpic().get(0);
        System.out.println("Статус эпика: " + restoredEpic.getStatus());

    }
}



