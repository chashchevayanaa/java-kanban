package ru.yandex.javacourse.http.mapper;

import java.util.regex.Pattern;

public class EndpointMapper {

    public static EndpointEnum mapEndpoint(String method, String path) {

        switch (method) {
            case "GET" -> {
                if (path.equals("/tasks"))
                    return EndpointEnum.GET_ALL_TASKS;
                else if (Pattern.matches("/tasks/\\d", path))
                    return EndpointEnum.GET_TASK;
                else if (path.equals("/subtasks"))
                    return EndpointEnum.GET_ALL_SUBTASKS;
                else if (Pattern.matches("/subtasks/\\d", path))
                    return EndpointEnum.GET_SUBTASK;
                else if (path.equals("/epics"))
                    return EndpointEnum.GET_ALL_EPICS;
                else if (Pattern.matches("/epics/\\d", path))
                    return EndpointEnum.GET_EPIC;
                else if (Pattern.matches("/epics/\\d/subtasks", path))
                    return EndpointEnum.GET_EPIC_SUBTASKS;
                else if (path.equals("/history"))
                    return EndpointEnum.GET_HISTORY;
                else if (path.equals("/prioritized"))
                    return EndpointEnum.GET_PRIORITIZED_TASKS;
            }
            case "DELETE" -> {
                if (Pattern.matches("/tasks/\\d", path))
                    return EndpointEnum.DELETE_TASK;
                else if (Pattern.matches("/subtasks/\\d", path))
                    return EndpointEnum.DELETE_SUBTASK;
                else if (Pattern.matches("/epics/\\d", path))
                    return EndpointEnum.DELETE_EPIC;
            }
            case "POST" -> {
                if (path.equals("/tasks"))
                    return EndpointEnum.CREATE_TASK;
                else if (Pattern.matches("/tasks/\\d", path))
                    return EndpointEnum.UPDATE_TASK;
                else if (path.equals("/subtasks"))
                    return EndpointEnum.CREATE_SUBTASK;
                else if (Pattern.matches("/subtasks/\\d", path))
                    return EndpointEnum.UPDATE_SUBTASK;
                else if (path.equals("/epics"))
                    return EndpointEnum.CREATE_EPIC;
                else if (Pattern.matches("/epics/\\d", path))
                    return EndpointEnum.UPDATE_EPIC;
            }
        }

        return EndpointEnum.UNKNOWN;
    }
}
