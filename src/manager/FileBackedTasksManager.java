package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String FILE_HEADER = "id,type,name,status,description,epic,start,end,duration,id epic";

    //Файл для записи и чтения
    private final File file;

    public static void main(String[] args) {
        File file = new File("Tasks.csv");
        FileBackedTasksManager taskManager = loadFromFile(file);

        LocalDateTime time1 = LocalDateTime.of(2020, 10, 1, 10, 0, 0);
        Duration dur1 = Duration.ofMinutes(60);

        Task task1 = new Task(taskManager.getId(), "Задача 3", "Описание задачи 3", Status.NEW,
                time1, dur1);
        taskManager.createTask(task1);
        Task task2 = new Task(taskManager.getId(), "Задача 4", "Описание задачи 4", Status.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic(taskManager.getId(), "Эпик 3", "Описание эпика 3");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic(taskManager.getId(), "Эпик 4", "Описание эпика 4");
        taskManager.createEpic(epic2);

        LocalDateTime time3 = LocalDateTime.of(2020, 10, 1, 9, 0, 0);
        Duration dur3 = Duration.ofMinutes(15);
        LocalDateTime time4 = LocalDateTime.of(2020, 10, 1, 9, 30, 0);
        Duration dur4 = Duration.ofMinutes(30);
        LocalDateTime time5 = LocalDateTime.of(2020, 10, 1, 10, 30, 0);
        Duration dur5 = Duration.ofMinutes(20);
        LocalDateTime time6 = LocalDateTime.of(2020, 10, 1, 10, 15, 0);
        Duration dur6 = Duration.ofMinutes(60);
        LocalDateTime time7 = LocalDateTime.of(2020, 10, 1, 9, 45, 0);
        Duration dur7 = Duration.ofMinutes(90);
        LocalDateTime time8 = LocalDateTime.of(2020, 10, 1, 11, 0, 0);
        Duration dur8 = Duration.ofMinutes(15);

        SubTask subTask1 = new SubTask(taskManager.getId(), "Подзадача 4", "Описание подзадачи 4", Status.IN_PROGRESS,
                time3, dur3, 3);
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask(taskManager.getId(), "Подзадача 5", "Описание подзадачи 5", Status.DONE,
                time4, dur4, 3);
        taskManager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask(taskManager.getId(), "Подзадача 6", "Описание подзадачи 6", Status.DONE,
                time5, dur5, 3);
        taskManager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask(taskManager.getId(), "Подзадача 7", "Описание подзадачи 7", Status.NEW,
                time6, dur6, 3);
        taskManager.createSubTask(subTask4);
        SubTask subTask5 = new SubTask(taskManager.getId(), "Подзадача 8", "Описание подзадачи 8", Status.IN_PROGRESS,
                time7, dur7, 3);
        taskManager.createSubTask(subTask5);
        SubTask subTask6 = new SubTask(taskManager.getId(), "Подзадача 9", "Описание подзадачи 9", Status.NEW,
                time8, dur8, 3);
        taskManager.createSubTask(subTask6);

        taskManager.getSubTask(12);
        taskManager.getSubTask(14);
        taskManager.getTask(9);
        taskManager.getEpic(10);
        taskManager.getSubTask(13);

        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task.toStringForFile());
        }
        System.out.println(taskManager.getPrioritizedTasks().size());
    }

    //Конструктор, в который передаётся файл
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    //Сохранение менеджера задач в файл
    private void save() {
        //Общая карта задач по id
        TreeMap<Integer, String> tasks = new TreeMap<>(Comparator.naturalOrder());
        for (Task task : taskMap.values()) {
            tasks.put(task.getId(), toString(task));
        }
        for (Epic epic : epicMap.values()) {
            tasks.put(epic.getId(), toString(epic));
        }
        for (SubTask subTask : subTaskMap.values()) {
            tasks.put(subTask.getId(), toString(subTask));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            //Запись в файл информации менеджера задач
            bw.write(FILE_HEADER);
            bw.newLine();
            for (String task : tasks.values()) {
                bw.write(task);
                bw.newLine();
            }
            bw.newLine();
            bw.write(toString(historyManager));
            bw.flush();
        } catch (IOException exp) {
            throw new ManagerSaveException("Не удаётся сохранить в файл: " + file.getName(), exp);
        }
    }

    //Добавить задачу в восстанавливаемый из файла менеджер задач
    private static void addTask(FileBackedTasksManager manager, String str) {
        Task task = taskFromString(str);
        Type type = Objects.requireNonNull(task).getType();
        switch (type) {
            case TASK: {
                manager.createTask(task);
                break;
            }
            case EPIC: {
                manager.createEpic((Epic) task);
                break;
            }
            case SUBTASK: {
                manager.createSubTask((SubTask) task);
                break;
            }
        }
    }

    //Восстановление менеджера задач из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            //Список задач в виде строк
            List<String> tasks = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            //Заполнение карт задач возвращаемого менеджера
            for (int i = 1; i < tasks.size() - 1; i++) {
                String str = tasks.get(i);
                if (str.isEmpty()) {
                    tasks.remove(str);
                    break;
                }
                addTask(manager, str);
            }
            String history = tasks.get(tasks.size() - 1);
            //Вызов истории просмотров задач
            for (Integer id : historyFromString(history)) {
                manager.getTask(id);
                manager.getEpic(id);
                manager.getSubTask(id);
            }
            for (Integer id : manager.taskMap.keySet()) {
                if (id > manager.id) {
                    manager.id = id;
                }
            }
            for (Integer id : manager.epicMap.keySet()) {
                if (id > manager.id) {
                    manager.id = id;
                }
            }
            for (Integer id : manager.subTaskMap.keySet()) {
                if (id > manager.id) {
                    manager.id = id;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удаётся прочитать файл: " + file.getName(), e);
        }
        return manager;
    }

    //Превращение задачи в строку
    private <T extends Task> String toString(T task) {
        return task.toStringForFile();
    }

    //Создание задачи из строки
    private static Task getTaskFromString(String[] elements) {
        if (elements.length == 5) {
            return new Task(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]));
        } else {
            return new Task(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]),
                    LocalDateTime.parse(elements[5], Task.dateformat),
                    Duration.ofMinutes(Long.parseLong(elements[7])));
        }
    }

    //Создание эпика из строки
    private static Task getEpicFromString(String[] elements) {
        if (elements.length == 5) {
            return new Epic(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]));
        } else {
            return new Epic(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]),
                    LocalDateTime.parse(elements[5], Task.dateformat),
                    LocalDateTime.parse(elements[6], Task.dateformat),
                    Duration.ofMinutes(Long.parseLong(elements[7])));
        }
    }

    //Создание подзадачи из строки
    private static Task getSubTaskFromString(String[] elements) {
        if (elements.length == 6) {
            return new SubTask(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]),
                    Integer.parseInt(elements[5]));
        } else {
            return new SubTask(
                    Integer.parseInt(elements[0]),
                    elements[2],
                    elements[4],
                    Status.valueOf(elements[3]),
                    LocalDateTime.parse(elements[5], Task.dateformat),
                    Duration.ofMinutes(Long.parseLong(elements[7])),
                    Integer.parseInt(elements[8]));
        }
    }

    //Получение задачи из строки
    private static Task taskFromString(String value) {
        String[] elements = value.split(",");
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].trim();
        }
        switch (Type.valueOf(elements[1].toUpperCase())) {
            case TASK:
                return getTaskFromString(elements);
            case EPIC:
                return getEpicFromString(elements);
            case SUBTASK:
                return getSubTaskFromString(elements);
            default: {
                System.out.println("Данного типа задач не существует");
                return null;
            }
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
        if (id != null) {
            if (taskMap.containsKey(id)) {
                historyManager.linkLast(taskMap.get(id));
                save();
                return taskMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Task> getTaskList() {
        if (!taskMap.isEmpty()) {
            save();
            return new ArrayList<>(taskMap.values());
        } else {
            return null;
        }
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
        if (id != null) {
            if (epicMap.containsKey(id)) {
                historyManager.linkLast(epicMap.get(id));
                save();
                return epicMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        if (!epicMap.isEmpty()) {
            save();
            return new ArrayList<>(epicMap.values());
        } else {
            return null;
        }
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
        if (id != null) {
            if (subTaskMap.containsKey(id)) {
                historyManager.linkLast(subTaskMap.get(id));
                save();
                return subTaskMap.get(id);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<SubTask> getSubTaskList() {
        if (!subTaskMap.isEmpty()) {
            save();
            return new ArrayList<>(subTaskMap.values());
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<SubTask> getSubTaskListOfEpic(Integer idEpic) {
        if (idEpic != null) {
            ArrayList<SubTask> subTaskListOfEpic = new ArrayList<>();
            if (epicMap.containsKey(idEpic)) {
                if (!epicMap.get(idEpic).getSubTaskIdMap().isEmpty()) {
                    for (Integer id : epicMap.get(idEpic).getSubTaskIdMap().keySet()) {
                        subTaskListOfEpic.add(subTaskMap.get(id));
                    }
                }
            }
            if (!subTaskListOfEpic.isEmpty()) {
                save();
                return new ArrayList<>(subTaskListOfEpic);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
