package ru.yandex.javacourse.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
        assertEquals(epic1.hashCode(), epic2.hashCode());
    }

    @Test
    void epicShouldInitializeWithEmptySubtaskList() {
        Epic epic = new Epic("Test", "Description");
        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    void setSubtaskIdsShouldWorkCorrectly() {
        Epic epic = new Epic("Test", "Description");
        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(1);
        subtasks.add(2);

        epic.setSubtaskIds(subtasks);
        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(1));
    }
}