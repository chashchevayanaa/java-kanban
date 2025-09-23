package ru.yandex.javacourse.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 2);
        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2);
        assertEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    @Test
    void subtaskShouldStoreEpicId() {
        Subtask subtask = new Subtask("Test", "Description", 5);
        assertEquals(5, subtask.getEpicId());

        subtask.setEpicId(10);
        assertEquals(10, subtask.getEpicId());
    }

    @Test
    void subtaskInheritsTaskBehavior() {
        Subtask subtask = new Subtask("Test", "Description", 1);
        subtask.setId(1);
        subtask.setStatus(Status.IN_PROGRESS);

        assertEquals(1, subtask.getId());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }
}
