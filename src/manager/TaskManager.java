package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    //Получить список задач, ранжированный по времени
    TreeSet<Task> getPrioritizedTasks();

    //Проверка пересечения времени задач
    <T extends Task> boolean timeValidation(T task);

    //Получить историю запросов
    HistoryManager getHistoryManager();

    //Получить следующий id
    Integer getId();

    //Создать задачу
    void createTask(Task task);

    //Обновить задачу
    void updateTask(Task task);

    //Удалить задачу
    void deleteTask(Integer id);

    //Удалить все задачи
    void deleteAllTask();

    //Получить задачу
    Task getTask(Integer id);

    //Получить список задач
    ArrayList<Task> getTaskList();

    //Создать эпик
    void createEpic(Epic epic);

    //Обновить эпик
    void updateEpic(Epic epic);

    //Удалить эпик
    void deleteEpic(Integer idEpic);

    //Удалить все эпики
    void deleteAllEpic();

    //Получить эпик
    Epic getEpic(Integer id);

    //Получить список эпиков
    ArrayList<Epic> getEpicList();

    //проверка и обновление статуса
    void determineStatus(SubTask subTask);

    //Рассчитать время эпика
    Epic setEpicTime(Epic epic);

    //Создать подзадачу
    void createSubTask(SubTask subTask);

    //Обновить подзадачу
    void updateSubTask(SubTask subTask);

    //Удалить подзадачу
    void deleteSubTask(Integer id);

    //Удалить все подзадачи
    void deleteAllSubTask();

    //Получить подзадачу
    SubTask getSubTask(Integer id);

    //Получить список подзадач
    ArrayList<SubTask> getSubTaskList();

    //Получить список подзадач эпика
    ArrayList<SubTask> getSubTaskListOfEpic(Integer idEpic);

    //Получить историю просмотров
    List<Task> getHistory();

    //Получить карту задач
    HashMap<Integer, Task> getTaskMap();

    //Получить карту эпиков
    HashMap<Integer, Epic> getEpicMap();

    //Получить карту подзадач
    HashMap<Integer, SubTask> getSubTaskMap();
}
