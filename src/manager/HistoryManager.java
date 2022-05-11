package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    //Добавление задачи в список истории
    void linkLast(Task task);

    //Удаление задачи из списка истории
    void remove(int id);

    //Получение списка истории задач
    List<Task> getHistory();

    ArrayList<Task> getTasks();
}
