import manager.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.util.ArrayList;

import static tasks.Status.NEW;
import static tasks.Status.IN_PROGRESS;
import static tasks.Status.DONE;

public class Main {

    public static void main(String[] args) {
        File file = new File("Tasks.csv");
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);

        Task task1 = new Task(taskManager.getId(), "Задача 1", "Описание задачи 1", NEW);
        taskManager.createTask(task1);
        Task task2 = new Task(taskManager.getId(), "Задача 2", "Описание задачи 2", NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(taskManager.getId(), "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic(taskManager.getId(), "Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(taskManager.getId(), "Подзадача 1", "Описание подзадачи 1", NEW, 3);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask(taskManager.getId(), "Подзадача 2", "Описание подзадачи 2", NEW, 3);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask(taskManager.getId(), "Подзадача 3", "Описание подзадачи 3", NEW, 3);
        taskManager.createSubTask(subTask3);

        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getEpic(4));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getSubTask(5));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getSubTask(6));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getSubTask(5));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getSubTask(6));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getSubTask(7));
        System.out.println(taskManager.getHistoryManager().getHistory());
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getHistoryManager().getHistory());


        taskManager.updateTask(new Task(task1.getId(), "Задача 1", "Описание задачи 1", IN_PROGRESS));
        taskManager.updateTask(new Task(task2.getId(), "Задача 2", "Описание задачи 2", DONE));
        taskManager.updateSubTask(new SubTask(subTask1.getId(), "Подзадача 1", "Описание подзадачи 1", DONE, 3));
        taskManager.updateSubTask(new SubTask(subTask3.getId(), "Подзадача 3", "Описание подзадачи 3", IN_PROGRESS, 4));

        System.out.println("\n" + taskManager.getHistoryManager().getHistory() + "\n");

        taskManager.deleteTask(1);
        taskManager.deleteEpic(3);

        System.out.println(taskManager.getHistoryManager().getHistory() + "\n");

        taskManager.deleteAllTask();
        Task task = taskManager.getTask(10);
        System.out.println(task);
        ArrayList<Task> taskList = taskManager.getTaskList();
        System.out.println(taskList);
        taskManager.deleteAllEpic();
        Epic epic = taskManager.getEpic(10);
        System.out.println(epic);
        ArrayList<Epic> epicList = taskManager.getEpicList();
        System.out.println(epicList);
        taskManager.deleteSubTask(10);
        taskManager.deleteAllSubTask();
        SubTask subTask = taskManager.getSubTask(10);
        System.out.println(subTask);
        ArrayList<SubTask> subTasks = taskManager.getSubTaskList();
        System.out.println(subTasks);
        ArrayList<SubTask> subTaskListOfEpic = taskManager.getSubTaskListOfEpic(10);
        System.out.println(subTaskListOfEpic);

        System.out.println(taskManager.getTaskMap());
        System.out.println(taskManager.getEpicMap());
        System.out.println(taskManager.getSubTaskMap());
    }
}
