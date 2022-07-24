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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String HEADER = "id,type,name,status,description,epic,start,end,duration,id epic";

    //Файл для записи и чтения
    private final File file;

    //Конструктор, в который передаётся адрес файла
    public FileBackedTasksManager(String path) {
        this.file = new File(path);
    }

    //Сохранение менеджера задач в файл
    public void save() {
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
        if (tasks.isEmpty()) {
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            //Запись в файл информации менеджера задач
            bw.write(HEADER);
            bw.newLine();
            for (String task : tasks.values()) {
                bw.write(task);
                bw.newLine();
            }
            String historyList = toString(historyManager);
            if (historyList != null) {
                bw.newLine();
                bw.write(historyList);
            }
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
    static FileBackedTasksManager load(String path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        try {
            //Список задач в виде строк
            List<String> tasks = Files.readAllLines(manager.file.toPath(), StandardCharsets.UTF_8);
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
            throw new ManagerSaveException("Не удаётся прочитать файл: " + manager.file.getName(), e);
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
                    LocalDateTime.parse(elements[5], Task.dateFormat),
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
                    LocalDateTime.parse(elements[5], Task.dateFormat),
                    LocalDateTime.parse(elements[6], Task.dateFormat),
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
                    LocalDateTime.parse(elements[5], Task.dateFormat),
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
        if (manager.getHistory() == null) {
            return null;
        }
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
        Task task = super.getTask(id);
        save();
        return task;
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
        Epic epic = super.getEpic(id);
        save();
        return epic;
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
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
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
}
