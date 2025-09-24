package ru.yandex.javacourse.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static final String SUBTASK_NAME_1 = "Subtask 1";
    private static final String SUBTASK_DESCRIPTION_1 = "Description 1";
    private static final String SUBTASK_NAME_2 = "Subtask 2";
    private static final String SUBTASK_DESCRIPTION_2 = "Description 2";
    private static final String TEST_NAME = "Test";
    private static final String TEST_DESCRIPTION = "Description";
    private static final int SUBTASK_ID = 1;
    private static final int INITIAL_EPIC_ID = 5;
    private static final int UPDATED_EPIC_ID = 10;

    @Test
    @DisplayName("Две подзадачи с одинаковым id должны быть равны")
    void equals_ShouldBeEqual_WhenSubtasksHaveSameId() {
        Subtask subtask1 = new Subtask(SUBTASK_NAME_1, SUBTASK_DESCRIPTION_1, INITIAL_EPIC_ID);
        Subtask subtask2 = new Subtask(SUBTASK_NAME_2, SUBTASK_DESCRIPTION_2, UPDATED_EPIC_ID);
        subtask1.setId(SUBTASK_ID);
        subtask2.setId(SUBTASK_ID);

        assertEquals(subtask1, subtask2);
        assertEquals(subtask1.hashCode(), subtask2.hashCode());
    }

    @Test
    @DisplayName("Подзадача должна корректно хранить и изменять epicId")
    void getEpicId_ShouldReturnCorrectEpicId_WhenEpicIdIsSet() {
        Subtask subtask = new Subtask(TEST_NAME, TEST_DESCRIPTION, INITIAL_EPIC_ID);
        assertEquals(INITIAL_EPIC_ID, subtask.getEpicId());

        subtask.setEpicId(UPDATED_EPIC_ID);
        assertEquals(UPDATED_EPIC_ID, subtask.getEpicId());
    }

    @Test
    @DisplayName("Подзадача должна наследовать поведение задачи")
    void settersAndGetters_ShouldWorkCorrectly_WhenSubtaskInheritsTask() {
        Subtask subtask = new Subtask(TEST_NAME, TEST_DESCRIPTION, 1);

        subtask.setId(SUBTASK_ID);
        subtask.setStatus(Status.IN_PROGRESS);

        assertEquals(SUBTASK_ID, subtask.getId());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }
}
