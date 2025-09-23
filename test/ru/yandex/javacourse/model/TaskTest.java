package ru.yandex.javacourse.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private Task task2;

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
    @Test
    void taskSettersAndGettersShouldWorkCorrectly() {
        Task task = new Task("Test", "Description");
        task.setId(1);
        task.setStatus(Status.IN_PROGRESS);
        task.setName("Updated");
        task.setDescription("Updated Desc");

        assertEquals(1, task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("Updated", task.getName());
        assertEquals("Updated Desc", task.getDescription());
    }

    @Test
    void toStringShouldContainAllFields() {
        Task task = new Task("Test", "Description");
        task.setId(1);
        task.setStatus(Status.DONE);

        String result = task.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("status=DONE"));
    }
}