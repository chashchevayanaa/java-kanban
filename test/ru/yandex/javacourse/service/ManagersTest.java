package ru.yandex.javacourse.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    @DisplayName("Менеджеры должны возвращать проинициализированные экземпляры")
    void managersShouldReturnInitializedInstances() {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(manager);
        assertNotNull(historyManager);
    }
}
