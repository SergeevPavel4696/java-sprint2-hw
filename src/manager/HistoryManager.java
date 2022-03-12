package manager;

import tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {

    //Добавление задачи в список истории
    void add(Task task);

    //Получение списка истории задач
    ArrayList<Task> getHistory();
}
