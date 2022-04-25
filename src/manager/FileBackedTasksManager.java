package manager;

import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    //Файл для записи и чтения
    private final File file;

    public static void main(String[] args) {
        File file = new File("Tasks.csv");
        FileBackedTasksManager taskManager = loadFromFile(file);

        Task task1 = new Task(taskManager.getId(), "Задача 3", "Описание задачи 3", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task(taskManager.getId(), "Задача 4", "Описание задачи 4", Status.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(taskManager.getId(), "Эпик 3", "Описание эпика 3");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic(taskManager.getId(), "Эпик 4", "Описание эпика 4");
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(taskManager.getId(), "Подзадача 4", "Описание подзадачи 4", Status.NEW, 3);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask(taskManager.getId(), "Подзадача 5", "Описание подзадачи 5", Status.NEW, 3);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask(taskManager.getId(), "Подзадача 6", "Описание подзадачи 6", Status.NEW, 3);
        taskManager.createSubTask(subTask3);

        taskManager.getSubTask(12);
        taskManager.getSubTask(14);
        taskManager.getTask(9);
        taskManager.getEpic(10);
    }

    //Конструктор, в который передаётся файл
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    //Сохранение менеджера задач в файл
    private void save() {
        //Общая карта задач по id
        TreeMap<Integer, String> tasks = new TreeMap<>(Comparator.comparingInt(o -> o));
        for (Task task : taskMap.values()) {
            tasks.put(task.getId(), toString(task));
        }
        for (Epic epic : epicMap.values()) {
            tasks.put(epic.getId(), toString(epic));
        }
        for (SubTask subTask : subTaskMap.values()) {
            tasks.put(subTask.getId(), toString(subTask));
        }
        try (OutputStream os = new FileOutputStream(file)) {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "windows-1251"), true);
            //Запись в файл информации менеджера задач
            pw.println("id,type,name,status,description,epic");
            for (String task : tasks.values()) {
                pw.println(task);
            }
            pw.println("\n" + toString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удаётся сохранить в файл: " + file.getName(), e);
        }
    }

    //Восстановление менеджера задач из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbtm = new FileBackedTasksManager(file);
        try {
            //Список задач в виде строк
            List<String> tasks = Files.readAllLines(file.toPath(), Charset.forName("windows-1251"));
            //Заполнение карт задач возвращаемого менеджера
            for (int i = 1; i < tasks.size() - 1; i++) {
                String str = tasks.get(i);
                if (str.isEmpty()) {
                    tasks.remove(str);
                    break;
                }
                String type = taskFromString(str).getClass().getSimpleName();
                if ("Task".equals(type)) {
                    fbtm.taskMap.put(Integer.parseInt(str.split(",")[0]), taskFromString(str));
                }
                if ("Epic".equals(type)) {
                    fbtm.epicMap.put(Integer.parseInt(str.split(",")[0]), (Epic) taskFromString(str));
                }
                if ("SubTask".equals(type)) {
                    fbtm.subTaskMap.put(Integer.parseInt(str.split(",")[0]), (SubTask) taskFromString(str));
                }
            }
            //Вызов истории просмотров задач
            for (Integer id : historyFromString(tasks.get(tasks.size() - 1))) {
                fbtm.getTask(id);
                fbtm.getEpic(id);
                fbtm.getSubTask(id);
            }
            //Восстановление последнего id менеджера
            fbtm.id = tasks.size() - 2;
        } catch (IOException e) {
            throw new ManagerSaveException("Не удаётся прочитать файл: " + file.getName(), e);
        }
        return fbtm;
    }

    //Превращение задачи в строку
    private <T extends Task> String toString(T task) {
        String taskLine = String.join(",",
                task.getId().toString(),
                task.getClass().getSimpleName(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription());
        if (task instanceof SubTask) {
            return taskLine + "," + ((SubTask) task).getIdEpic();
        } else {
            return taskLine;
        }
    }

    //Получение задачи из строки
    private static Task taskFromString(String value) {
        String[] tasks = value.split(",");
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = tasks[i].trim();
        }
        switch (Type.valueOf(tasks[1].toUpperCase())) {
            case TASK:
                return new Task(
                        Integer.parseInt(tasks[0]),
                        tasks[2],
                        tasks[4],
                        Status.valueOf(tasks[3]));
            case EPIC:
                return new Epic(
                        Integer.parseInt(tasks[0]),
                        tasks[2],
                        tasks[4],
                        Status.valueOf(tasks[3]));
            default:
                return new SubTask(
                        Integer.parseInt(tasks[0]),
                        tasks[2],
                        tasks[4],
                        Status.valueOf(tasks[3]),
                        Integer.parseInt(tasks[5]));
        }
    }

    //Превращение истории в строку
    private static String toString(HistoryManager manager) {
        List<String> list = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            list.add(task.getId().toString());
        }
        return String.join(",", list);
    }

    //Получение истории из строки
    private static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        for (String id : value.split(",")) {
            list.add(Integer.parseInt(id.trim()));
        }
        return list;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Task getTask(Integer id) {
        save();
        return super.getTask(id);
    }

    @Override
    public ArrayList<Task> getTaskList() {
        save();
        return super.getTaskList();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(Integer idEpic) {
        super.deleteEpic(idEpic);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic getEpic(Integer id) {
        save();
        return super.getEpic(id);
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        save();
        return super.getEpicList();
    }

    @Override
    public void determineStatus(SubTask subTask) {
        super.determineStatus(subTask);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteSubTask(Integer id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public SubTask getSubTask(Integer id) {
        save();
        return super.getSubTask(id);
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {
        save();
        return super.getSubTaskList();
    }

    @Override
    public ArrayList<SubTask> getSubTaskListOfEpic(Integer idEpic) {
        save();
        return super.getSubTaskListOfEpic(idEpic);
    }

    @Override
    public List<Task> history() {
        return super.history();
    }

    @Override
    public HashMap<Integer, Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public HashMap<Integer, Epic> getEpicMap() {
        return super.getEpicMap();
    }

    @Override
    public HashMap<Integer, SubTask> getSubTaskMap() {
        return super.getSubTaskMap();
    }
}
