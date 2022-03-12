package util;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

public class Managers {
    private static HistoryManager historyManager = new InMemoryHistoryManager();
    private static TaskManager taskManager = new InMemoryTaskManager();

    private Managers() {}

    public static TaskManager getDefault(){
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
