package ru.yandex.javacourse;

import ru.yandex.javacourse.model.Epic;
import ru.yandex.javacourse.model.Status;
import ru.yandex.javacourse.model.Subtask;
import ru.yandex.javacourse.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();


        Epic epic1 = new Epic("Уборка", "всей квартиры");
        manager.addingEpic(epic1);

        Subtask subtask1 = new Subtask("кухня", "помыть посуду", epic1.getId());
        manager.addingSubtask(subtask1);
        Subtask subtask2 = new Subtask("кухня", "помыть пол", epic1.getId());
        manager.addingSubtask(subtask2);
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);

        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
        }

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
        }

        for (Subtask subtask : manager.getAllSubtask()) {
            System.out.println(subtask);
        }

        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
        }

        manager.deleteAllSubtask();
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
        }
    }
}

