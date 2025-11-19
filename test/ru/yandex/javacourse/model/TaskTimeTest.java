package ru.yandex.javacourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTimeTest {

    @Test
    @DisplayName("Расчет времени завершения задачи")
    void getEndTime_ShouldCalculateCorrectly_WhenStartTimeAndDurationSet() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Duration duration = Duration.ofMinutes(90); // 1.5 часа

        Task task = new Task("Test", "Description", duration, startTime);

        LocalDateTime expectedEndTime = LocalDateTime.of(2024, 1, 1, 11, 30);
        assertEquals(expectedEndTime, task.getEndTime());
    }

    @Test
    @DisplayName("Время завершения null при отсутствии startTime")
    void getEndTime_ShouldReturnNull_WhenStartTimeNull() {
        Task task = new Task("Test", "Description");
        task.setDuration(Duration.ofMinutes(30));

        assertNull(task.getEndTime());
    }

    @Test
    @DisplayName("Время завершения null при отсутствии duration")
    void getEndTime_ShouldReturnNull_WhenDurationNull() {
        Task task = new Task("Test", "Description");
        task.setStartTime(LocalDateTime.now());

        assertNull(task.getEndTime());
    }

    @Test
    @DisplayName("Epic время начала и окончания рассчитывается из подзадач")
    void epicTimeFields_ShouldBeCalculatedFromSubtasks_WhenSubtasksExist() {
        Epic epic = new Epic("Epic", "Description");

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        Duration duration = Duration.ofMinutes(90);

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);

        assertEquals(startTime, epic.getStartTime());
        assertEquals(endTime, epic.getEndTime());
        assertEquals(duration, epic.getDuration());
    }
}