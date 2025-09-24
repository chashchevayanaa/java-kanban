package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static final String EPIC_NAME_1 = "Epic 1";
    private static final String EPIC_NAME_2 = "Epic 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String TEST_NAME = "Test";
    private static final String TEST_DESCRIPTION = "Description";
    private static final int EPIC_ID = 1;
    private static final int SUBTASK_ID_1 = 1;
    private static final int SUBTASK_ID_2 = 2;

    @Test
    @DisplayName("Два эпика с одинаковым id должны быть равны")
    void equals_ShouldBeEqual_WhenEpicsHaveSameId() {
        Epic epic1 = new Epic(EPIC_NAME_1, DESCRIPTION_1);
        Epic epic2 = new Epic(EPIC_NAME_2, DESCRIPTION_2);
        epic1.setId(EPIC_ID);
        epic2.setId(EPIC_ID);

        assertEquals(epic1, epic2);
        assertEquals(epic1.hashCode(), epic2.hashCode());
    }

    @Test
    @DisplayName("Эпик должен инициализироваться с пустым списком подзадач")
    void getSubtaskIds_ShouldReturnEmptyList_WhenEpicIsCreated() {
        Epic epic = new Epic(TEST_NAME, TEST_DESCRIPTION);

        assertTrue(epic.getSubtaskIds().isEmpty());
    }

    @Test
    @DisplayName("Установка списка подзадач должна корректно обновлять список")
    void setSubtaskIds_ShouldUpdateSubtaskIds_WhenSetNewList() {
        Epic epic = new Epic(TEST_NAME, TEST_DESCRIPTION);
        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(SUBTASK_ID_1);
        subtasks.add(SUBTASK_ID_2);

        epic.setSubtaskIds(subtasks);

        assertEquals(SUBTASK_ID_2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(SUBTASK_ID_1));
    }
}