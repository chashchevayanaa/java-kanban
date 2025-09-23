package ru.yandex.javacourse.service;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ManagersTest {

    @Test
    void managersShouldReturnInitializedInstances() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}
