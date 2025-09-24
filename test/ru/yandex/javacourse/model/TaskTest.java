package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static final String TASK_NAME = "Task 1";
    private static final String TASK_NAME_2 = "Task 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final String TEST_NAME = "Test";
    private static final String TEST_DESCRIPTION = "Description";
    private static final String UPDATED_NAME = "Updated";
    private static final String UPDATED_DESCRIPTION = "Updated Desc";
    private static final int TASK_ID = 1;

    @Test
    @DisplayName("Проверка равенства задач с одинаковым id")
    void equals_ShouldBeEqual_WhenTasksHaveSameId() {
        Task task1 = new Task(TASK_NAME, DESCRIPTION_1);
        Task task2 = new Task(TASK_NAME_2, DESCRIPTION_2);
        task1.setId(TASK_ID);
        task2.setId(TASK_ID);

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    @DisplayName("Проверка корректности работы сеттеров и геттеров")
    void settersAndGetters_ShouldWorkCorrectly_WhenTaskFieldsAreSet() {
        Task task = new Task(TEST_NAME, TEST_DESCRIPTION);

        task.setId(TASK_ID);
        task.setStatus(Status.IN_PROGRESS);
        task.setName(UPDATED_NAME);
        task.setDescription(UPDATED_DESCRIPTION);

        assertEquals(TASK_ID, task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals(UPDATED_NAME, task.getName());
        assertEquals(UPDATED_DESCRIPTION, task.getDescription());
    }

    @Test
    @DisplayName("Метод toString должен содержать все поля задачи")
    void toString_ShouldContainAllFields_WhenTaskHasFields() {
        Task task = new Task(TEST_NAME, TEST_DESCRIPTION);
        task.setId(TASK_ID);
        task.setStatus(Status.DONE);

        String result = task.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("status=DONE"));
    }
}